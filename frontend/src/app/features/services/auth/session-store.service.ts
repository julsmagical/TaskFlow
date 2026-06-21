import { computed, inject, Injectable, signal } from '@angular/core';

import { AuthenticatedUser } from '../../interfaces/public/user.interface';

@Injectable({
  providedIn: 'root',
})
export class SessionStore {
  private readonly _user = signal<AuthenticatedUser | null>(null);
  private readonly _loading = signal(false);

  readonly user = this._user.asReadonly();
  readonly loading = this._loading.asReadonly();

  readonly authenticated = computed(() => this.user() !== null);

  readonly role = computed(() => this.user()?.role ?? null);

  readonly userName = computed(() => this.user()?.username ?? '');

  readonly isAdmin = computed(() => this.role() === 'ADMINISTRADOR');

  setUser(user: AuthenticatedUser): void {
    this._user.set(user);
  }

  clear(): void {
    this._user.set(null);
  }
}
