import { inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { SessionStore } from '../../features/services/auth/session-store.service';

export function sessionInitializer() {
  const sessionStore = inject(SessionStore);

  return firstValueFrom(sessionStore.restoreSession());
}
