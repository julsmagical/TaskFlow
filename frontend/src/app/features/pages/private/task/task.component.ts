import { CommonModule, NgClass } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

import { TaskService } from '../../../services/private/task.service';
import { ProjectService } from '../../../services/private/project.service';
import { SessionStore } from '../../../services/auth/session-store.service';
import { TaskResponse, TaskFilters } from '../../../interfaces/private/task.interface';
import { ProjectResponse } from '../../../interfaces/private/project.interface';
import { TaskStatus, TaskPriority } from '../../../../shared/enums/task';
import { TaskFormComponent } from './task-form/task-form';
import { MatDividerModule } from '@angular/material/divider';
import { TASK_PRIORITY_LABELS, TASK_STATUS_LABELS } from '../../../../shared/constants/task';
import { ConfirmDialogComponent } from './task-confirm/task-confirm';

@Component({
  selector: 'app-tasks',
  standalone: true,
  imports: [
    CommonModule,
    NgClass,
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatChipsModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatIconModule,
    MatMenuModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatTooltipModule,
    MatDividerModule,
  ],
  templateUrl: './task.component.html',
  styleUrl: './task.component.scss',
})
export class TasksComponent implements OnInit {
  private readonly taskService = inject(TaskService);
  private readonly projectService = inject(ProjectService);
  private readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  readonly sessionStore = inject(SessionStore);

  //mapear constants
  readonly taskStatusLabels = TASK_STATUS_LABELS;
  readonly taskPriorityLabels = TASK_PRIORITY_LABELS;

  readonly TaskStatus = TaskStatus;
  readonly TaskPriority = TaskPriority;

  readonly projects = signal<ProjectResponse[]>([]);
  readonly tasksByProject = signal(new Map<string, TaskResponse[]>());
  readonly loadedProjects = signal(new Set<string>());
  readonly loading = signal(false);

  readonly selectedProjectId = signal<string | null>(null);
  readonly selectedStatus = signal<TaskStatus | null>(null);
  readonly selectedPriority = signal<TaskPriority | null>(null);

  //sidebar de tareas pendientes
  readonly myPendingTasks = computed(() => {
    const userId = this.sessionStore.user()?.id;
    const allTasks: TaskResponse[] = [];
    this.tasksByProject().forEach((tasks) => allTasks.push(...tasks));
    return allTasks.filter((t) => t.assignedUserID === userId && t.status === TaskStatus.PENDIENTE);
  });

  readonly filteredTasksByProject = computed(() => {
    const result = new Map<string, TaskResponse[]>();
    this.tasksByProject().forEach((tasks, projectId) => {
      let filtered = [...tasks];
      if (this.selectedStatus()) {
        filtered = filtered.filter((t) => t.status === this.selectedStatus());
      }
      if (this.selectedPriority()) {
        filtered = filtered.filter((t) => t.priority === this.selectedPriority());
      }
      filtered.sort((a, b) => new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime());
      result.set(projectId, filtered);
    });
    return result;
  });

  readonly visibleProjects = computed(() => {
    if (this.selectedProjectId()) {
      return this.projects().filter((p) => p.id === this.selectedProjectId());
    }
    return this.projects();
  });

  ngOnInit(): void {
    this.loadProjects();
  }

  loadProjects(): void {
    this.loading.set(true);
    this.projectService.findAll().subscribe({
      next: (projects) => {
        this.projects.set(projects);
        this.loading.set(false);
        projects.forEach((p) => this.loadTasks(p.id));
      },
      error: () => this.loading.set(false),
    });
  }

  loadTasks(projectId: string, force = false): void {
    if (this.loadedProjects().has(projectId) && !force) return;

    const filters: TaskFilters = {};
    if (this.selectedStatus()) filters.estado = this.selectedStatus()!;
    if (this.selectedPriority()) filters.prioridad = this.selectedPriority()!;

    this.taskService.findByProject(projectId, filters).subscribe((tasks) => {
      const map = new Map(this.tasksByProject());
      map.set(projectId, tasks);
      this.tasksByProject.set(map);

      const loaded = new Set(this.loadedProjects());
      loaded.add(projectId);
      this.loadedProjects.set(loaded);
    });
  }

  applyFilters(): void {
    const loaded = new Set(this.loadedProjects());
    loaded.forEach((id) => {
      const filters: TaskFilters = {};
      if (this.selectedStatus()) filters.estado = this.selectedStatus()!;
      if (this.selectedPriority()) filters.prioridad = this.selectedPriority()!;
      this.taskService.findByProject(id, filters).subscribe((tasks) => {
        const map = new Map(this.tasksByProject());
        map.set(id, tasks);
        this.tasksByProject.set(map);
      });
    });
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  getTasks(projectId: string): TaskResponse[] {
    return this.filteredTasksByProject().get(projectId) ?? [];
  }

  canManage(project: ProjectResponse): boolean {
    return (
      this.sessionStore.isAdmin() ||
      (this.sessionStore.isLeader() && project.leaderId === this.sessionStore.user()?.id)
    );
  }

  viewTask(task: TaskResponse): void {
    this.router.navigate(['/tasks', task.id]);
  }

  openCreateTask(project: ProjectResponse): void {
    const ref = this.dialog.open(TaskFormComponent, {
      width: '560px',
      data: { projectId: project.id, task: null },
    });

    ref.afterClosed().subscribe((created) => {
      if (!created) {
        return;
      }

      this.refreshProjectTasks(project.id);

      this.snackBar.open('Tarea creada correctamente', 'OK', {
        duration: 3000,
      });
    });
  }

  openEditTask(task: TaskResponse, project: ProjectResponse): void {
    const ref = this.dialog.open(TaskFormComponent, {
      width: '560px',
      data: { projectId: project.id, task },
    });

    ref.afterClosed().subscribe((updated) => {
      if (!updated) {
        return;
      }

      this.refreshProjectTasks(project.id);

      this.snackBar.open('Tarea actualizada correctamente', 'OK', {
        duration: 3000,
      });
    });
  }

  deleteTask(task: TaskResponse, project: ProjectResponse): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '380px',
      data: {
        title: 'Eliminar tarea',
        message: `¿Seguro que deseas eliminar "${task.title}"? Esta acción no se puede deshacer.`,
        confirmLabel: 'Eliminar',
      },
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }
      this.taskService.delete(task.id).subscribe({
        next: () => {
          this.refreshProjectTasks(project.id);
          this.snackBar.open('Tarea eliminada correctamente', 'OK', {
            duration: 3000,
          });
        },
        error: (err) => {
          const msg = err?.error?.message ?? 'Error al eliminar la tarea';
          this.snackBar.open(msg, 'Cerrar', {
            duration: 4000,
          });
        },
      });
    });
  }

  isDueSoon(dueDate: string): boolean {
    const diff = new Date(dueDate).getTime() - Date.now();
    return diff > 0 && diff < 3 * 24 * 60 * 60 * 1000;
  }

  isOverdue(dueDate: string): boolean {
    return new Date(dueDate).getTime() < Date.now();
  }

  private refreshProjectTasks(projectId: string): void {
    const loaded = new Set(this.loadedProjects());
    loaded.delete(projectId);
    this.loadedProjects.set(loaded);
    this.loadTasks(projectId);
  }
}
