import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionStore } from '../../features/services/auth/session-store.service';

export const rootGuard: CanActivateFn = () => {
  const sessionStore = inject(SessionStore);
  const router = inject(Router);

  return sessionStore.authenticated()
    ? router.createUrlTree(['/home'])
    : router.createUrlTree(['/login']);
};
