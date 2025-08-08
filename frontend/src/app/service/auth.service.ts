import { inject, Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserRequest } from '../model/user-request';
import { UserResponse } from '../model/user-response';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly currentUserSignal = signal<UserResponse | null>(null);

  readonly currentUser = computed(() => this.currentUserSignal());
  readonly isAuthenticated = computed(() => !!this.currentUserSignal());

  fetchCurrentUser(): Observable<UserResponse> {
    return this.http
      .get<UserResponse>('/api/auth/current-user')
      .pipe(tap((user) => this.currentUserSignal.set(user)));
  }

  signup(userRequest: UserRequest): Observable<void> {
    return this.http.post<void>(`/api/users/signup`, userRequest);
  }

  login(userRequest: UserRequest): Observable<UserResponse> {
    return this.http
      .post<UserResponse>(`/api/auth/login`, userRequest)
      .pipe(tap((user) => this.currentUserSignal.set(user)));
  }

  logout(): Observable<void> {
    return this.http.post<void>(`/api/auth/logout`, {}).pipe(
      tap(() => {
        this.currentUserSignal.set(null);
      }),
    );
  }

  refreshToken(): Observable<UserResponse> {
    return this.http
      .post<UserResponse>(`/api/auth/refresh`, {})
      .pipe(tap((user) => this.currentUserSignal.set(user)));
  }
}
