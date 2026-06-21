import { computed, inject, Injectable, Injector, signal } from '@angular/core';

import { AuthenticatedUser } from '../../interfaces/public/user.interface';
import { catchError, finalize, Observable, of, tap } from 'rxjs';
import { TokenService } from './token.service';
import { AuthService } from './auth.service';
import { UserRole } from '../../../shared/enums/user';

@Injectable({
  providedIn: 'root',
})
export class SessionStore {
  private readonly injector = inject(Injector);

  private readonly _user = signal<AuthenticatedUser | null>(null);
  private readonly _loading = signal(false);
  private readonly _initialized = signal(false);

  readonly user = this._user.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly initialized = this._initialized.asReadonly();

  readonly authenticated = computed(() => this.user() !== null);

  readonly userName = computed(() => this.user()?.username ?? '');

  readonly role = computed(() => this.user()?.role as UserRole | null);

  hasRole(role: UserRole): boolean {
    return this.role() === role;
  }

  hasAnyRole(...roles: UserRole[]): boolean {
    const currentRole = this.role();

    return currentRole !== null && roles.includes(currentRole);
  }

  readonly isAdmin = computed(() => this.hasRole(UserRole.ADMINISTRADOR));

  readonly isLeader = computed(() => this.hasRole(UserRole.LIDER));

  setUser(user: AuthenticatedUser): void {
    this._user.set(user);
  }

  clear(): void {
    this._user.set(null);
    this._loading.set(false);
  }

  restoreSession(): Observable<AuthenticatedUser | null> {
    const tokenService = this.injector.get(TokenService);
    const authService = this.injector.get(AuthService);

    if (!tokenService.hasToken()) {
      return of(null);
    }

    if (tokenService.isExpired()) {
      tokenService.clearToken();
      this.clear();
      return of(null);
    }

    this._loading.set(true);

    return authService.me().pipe(
      tap((user) => this.setUser(user)),
      catchError(() => {
        tokenService.clearToken();
        this.clear();
        return of(null);
      }),
      finalize(() => {
        this._loading.set(false);
        this._initialized.set(true);
      }),
    );
  }
}
