import { TaskPriority, TaskStatus } from '../../../shared/enums/task';

export interface TaskResponse {
    id: string;
    projectId: string;
    title: string;
    description: string | null;
    priority: TaskPriority;
    status: TaskStatus;
    dueDate: string;
    assignedUserID: string | null;
    assignedUsername: string | null;
    createdAt: string;
    updatedAt: string;
}

export interface TaskRequest {
    title: string;
    description: string | null;
    priority: TaskPriority;
    dueDate: string;
    assignedUserId: string | null;
}

export interface TaskStatusRequest {
    newStatus: TaskStatus;
}

export interface TaskFilters {
    estado?: TaskStatus;
    prioridad?: TaskPriority;
}