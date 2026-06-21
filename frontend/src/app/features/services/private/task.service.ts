import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { filter, map, Observable } from 'rxjs';
import { environment } from '../../../../environment/environment';
import { ApiResponse } from '../../interfaces/private/api-response.interface';
import { TaskFilters, TaskRequest, TaskResponse, TaskStatusRequest } from '../../interfaces/private/task.interface';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
    private readonly http = inject(HttpClient);
    private readonly apiUrl = environment.apiUrl;

    create(projectId: string, request: TaskRequest): Observable<TaskResponse> {
        return this.http
            .post<ApiResponse<TaskResponse>>(`${this.apiUrl}/projects/${projectId}/tasks`, request)
            .pipe(map((response) => response.data));
    }

    update(id: string, request: TaskRequest): Observable<TaskResponse>{
        return this.http
            .put<ApiResponse<TaskResponse>>(`${this.apiUrl}/tasks/${id}`, request)
            .pipe(map((response) => response.data));
    }
    
    updateStatus(id: string, request: TaskRequest): Observable<TaskResponse>{
        return this.http
            .put<ApiResponse<TaskResponse>>(`${this.apiUrl}/tasks/${id}/status`, request)
            .pipe(map((response) => response.data));
    }

    delete(id: string): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/tasks/${id}`);
    }

    // con los filtros opcionales
    findByProject(projectId: string, filters?: TaskFilters): Observable<TaskResponse[]> {
        let params = new HttpParams();

        if(filters?.estado){
            params = params.set('estado', filters.estado);
        }
        if(filters?.prioridad){
            params = params.set('prioridad', filters.prioridad);
        }

        return this.http
            .get<ApiResponse<TaskResponse[]>>(`${this.apiUrl}/projects/${projectId}/tasks`, { params })
            .pipe(map((response) => response.data))
    }

    findById(id: string): Observable<TaskResponse>{
        return this.http
            .get<ApiResponse<TaskResponse>>(`${this.apiUrl}/tasks/${id}`)
            .pipe(map((response) => response.data));
    }
}