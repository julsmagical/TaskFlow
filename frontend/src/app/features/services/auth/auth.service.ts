import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { map, Observable, switchMap, tap } from 'rxjs';

import { TokenService } from './token.service';
import { LoginRequest, LoginResponse } from '../../interfaces/private/login.interface';
import { JwtPayload } from '../../interfaces/private/jwt.interface';
import { environment } from '../../../../environment/environment';
import { AuthenticatedUser } from '../../interfaces/public/user.interface';
import { SessionStore } from './session-store.service';
import { ApiResponse } from '../../interfaces/private/api-response.interface';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly tokenService = inject(TokenService);
  private readonly sessionStore = inject(SessionStore);

  private readonly authUrl = `${environment.apiUrl}/auth`;

  login(request: LoginRequest): Observable<AuthenticatedUser> {
    return this.http.post<ApiResponse<LoginResponse>>(`${this.authUrl}/login`, request).pipe(
      tap((response) => this.tokenService.saveToken(response.data.token)),
      switchMap(() => this.me()),
      tap((user) => this.sessionStore.setUser(user)),
    );
  }

  logout(): void {
    this.tokenService.clearToken();
    this.sessionStore.clear();
  }

  me(): Observable<AuthenticatedUser> {
    return this.http
      .get<ApiResponse<AuthenticatedUser>>(`${this.authUrl}/me`)
      .pipe(map((response) => response.data));
  }

  isAuthenticated(): boolean {
    return this.tokenService.hasToken() && !this.tokenService.isExpired();
  }

  getToken(): string | null {
    return this.tokenService.getToken();
  }

  getPayload(): JwtPayload | null {
    return this.tokenService.getPayload();
  }

  getRole(): string | null {
    return this.getPayload()?.role ?? null;
  }

  getUserId(): string | null {
    return this.getPayload()?.sub ?? null;
  }
}
