import {
  HttpErrorResponse,
  HttpHandlerFn,
  HttpInterceptorFn,
  HttpRequest,
  HttpStatusCode,
} from '@angular/common/http';
import { inject, signal } from '@angular/core';
import { throwError } from 'rxjs';
import { catchError, finalize, switchMap } from 'rxjs/operators';
import { AuthService } from '../service/auth.service';

const isRefreshingToken = signal(false);

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((error) => {
      if (shouldAttemptTokenRefresh(error)) {
        return handleTokenRefresh(req, next, authService, error);
      }
      return handleInvalidTokenError(req, error, authService, next);
    }),
  );
};

function handleTokenRefresh(
  request: HttpRequest<any>,
  next: HttpHandlerFn,
  authService: AuthService,
  originalError: HttpErrorResponse,
) {
  isRefreshingToken.set(true);
  return authService.refreshToken().pipe(
    switchMap(() => {
      return next(request);
    }),
    catchError(() => {
      return authService.logout().pipe(
        switchMap(() => {
          return throwError(() => originalError);
        }),
      );
    }),
    finalize(() => {
      isRefreshingToken.set(false);
    }),
  );
}

function shouldAttemptTokenRefresh(error: any): boolean {
  if (
    error instanceof HttpErrorResponse &&
    error.status === HttpStatusCode.Unauthorized &&
    error.headers.has('WWW-Authenticate') &&
    error.headers.get('WWW-Authenticate')?.includes('error=token_expired') &&
    !isRefreshingToken()
  ) {
    return true;
  } else {
    return false;
  }
}

function handleInvalidTokenError(
  request: HttpRequest<any>,
  error: any,
  authService: AuthService,
  next: HttpHandlerFn,
) {
  if (
    error instanceof HttpErrorResponse &&
    error.status === HttpStatusCode.Unauthorized &&
    error.headers.has('WWW-Authenticate') &&
    error.headers.get('WWW-Authenticate')?.includes('error=token_invalid')
  ) {
    return authService.logout().pipe(
      switchMap(() => {
        return next(request);
      }),
    );
  }
  return throwError(() => error);
}
