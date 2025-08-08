import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { AuthService } from '../service/auth.service';

export const authenticatedGuard: CanActivateFn = (): boolean => {
  const authService = inject(AuthService);

  if (authService.currentUser()) {
    return true;
  }
  return false;
};
