import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionStore } from '../../features/services/auth/session-store.service';

export const guestGuard: CanActivateFn = () => {
  const sessionStore = inject(SessionStore);
  const router = inject(Router);

  if (sessionStore.authenticated()) {
    return router.createUrlTree(['/home']);
  }

  return true;
};
