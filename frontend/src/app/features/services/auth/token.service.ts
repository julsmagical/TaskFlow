import { Service } from '@angular/core';
import { environment } from '../../../../environment/environment';
import { jwtDecode } from 'jwt-decode';
import { JwtPayload } from '../../interfaces/private/jwt.interface';

@Service()
export class TokenService {
  saveToken(token: string): void {
    localStorage.setItem(environment.tokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(environment.tokenKey);
  }

  clearToken(): void {
    localStorage.removeItem(environment.tokenKey);
  }

  hasToken(): boolean {
    return !!this.getToken();
  }

  getPayload(): JwtPayload | null {
    const token = this.getToken();
    if (!token) return null;
    return jwtDecode<JwtPayload>(token);
  }

  isExpired(): boolean {
    const payload = this.getPayload();

    if (!payload) return true;

    return payload.exp * 1000 < Date.now();
  }
}
