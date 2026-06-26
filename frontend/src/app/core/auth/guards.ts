import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '@core/auth/auth.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.isAuthenticated() && auth.hasRefreshToken()) {
    return true;
  }
  if (auth.isAuthenticated()) {
    auth.clearSession('Your session expired. Please log in again.');
    return router.createUrlTree(['/login'], { queryParams: { reason: 'session-expired' } });
  }
  return router.createUrlTree(['/login']);
};

export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (!auth.isAuthenticated() || !auth.hasRefreshToken()) {
    if (auth.isAuthenticated()) {
      auth.clearSession('Your session expired. Please log in again.');
      return router.createUrlTree(['/login'], { queryParams: { reason: 'session-expired' } });
    }
    return router.createUrlTree(['/login']);
  }
  return auth.isAdmin() || router.createUrlTree(['/']);
};
