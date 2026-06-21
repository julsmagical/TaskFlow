import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { map, Observable } from 'rxjs';
import { environment } from '../../../../environment/environment';
import { ProjectStatus } from '../../../shared/enums/project';
import { ApiResponse } from '../../interfaces/private/api-response.interface';
import { ProjectRequest, ProjectResponse } from '../../interfaces/private/project.interface';

@Injectable({
  providedIn: 'root',
})
export class ProjectService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl = `${environment.apiUrl}/projects`;

  findAll(status?: ProjectStatus): Observable<ProjectResponse[]> {
    let params = new HttpParams();

    if (status) {
      params = params.set('estado', status);
    }

    return this.http
      .get<ApiResponse<ProjectResponse[]>>(this.apiUrl, { params })
      .pipe(map((response) => response.data));
  }

  findById(id: string): Observable<ProjectResponse> {
    return this.http
      .get<ApiResponse<ProjectResponse>>(`${this.apiUrl}/${id}`)
      .pipe(map((response) => response.data));
  }

  create(request: ProjectRequest): Observable<ProjectResponse> {
    return this.http
      .post<ApiResponse<ProjectResponse>>(this.apiUrl, request)
      .pipe(map((response) => response.data));
  }

  update(id: string, request: ProjectRequest): Observable<ProjectResponse> {
    return this.http
      .put<ApiResponse<ProjectResponse>>(`${this.apiUrl}/${id}`, request)
      .pipe(map((response) => response.data));
  }

  archive(id: string): Observable<ProjectResponse> {
    return this.http
      .patch<ApiResponse<ProjectResponse>>(`${this.apiUrl}/${id}/archive`, {})
      .pipe(map((response) => response.data));
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
