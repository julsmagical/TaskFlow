import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { map, Observable } from 'rxjs';
import { environment } from '../../../../environment/environment';
import { ApiResponse } from '../../interfaces/private/api-response.interface';
import { UserRole } from '../../../shared/enums/user';
import { SelectableUser, UserRequest } from '../../interfaces/public/user.interface';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/users`;

  findAll(role?: UserRole): Observable<SelectableUser[]> {
    let params = new HttpParams();

    if (role) {
      params = params.set('role', role);
    }

    return this.http
      .get<ApiResponse<SelectableUser[]>>(this.apiUrl, { params })
      .pipe(map((response) => response.data));
  }

  findDevelopers(): Observable<SelectableUser[]> {
    return this.findAll(UserRole.DESARROLLADOR);
  }

  create(request: UserRequest): Observable<SelectableUser> {
    return this.http
      .post<ApiResponse<SelectableUser>>(this.apiUrl, request)
      .pipe(map((response) => response.data));
  }
}
