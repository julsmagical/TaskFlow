import { CommonModule, NgClass } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

import { TaskService } from '../../../../services/private/task.service';
import { ProjectService } from '../../../../services/private/project.service';
import { SessionStore } from '../../../../services/auth/session-store.service';
import { TaskResponse } from '../../../../interfaces/private/task.interface';
import { ProjectResponse } from '../../../../interfaces/private/project.interface';
import { TaskStatus, TaskPriority } from '../../../../../shared/enums/task';
import { TaskFormComponent } from '../task-form/task-form';
import { TASK_PRIORITY_LABELS, TASK_STATUS_LABELS } from '../../../../../shared/constants/task';

@Component({
  selector: 'app-task-detail',
  standalone: true,
  imports: [
    CommonModule,
    NgClass,
    MatButtonModule,
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDividerModule,
  ],
  templateUrl: './task-detail.html',
  styleUrl: './task-detail.scss',
})
export class TaskDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly taskService = inject(TaskService);
  private readonly projectService = inject(ProjectService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  readonly sessionStore = inject(SessionStore);

  //mapear constants
  readonly taskStatusLabels = TASK_STATUS_LABELS;
  readonly taskPriorityLabels = TASK_PRIORITY_LABELS;

  readonly TaskStatus = TaskStatus;
  readonly TaskPriority = TaskPriority;

  readonly task = signal<TaskResponse | null>(null);
  readonly project = signal<ProjectResponse | null>(null);
  readonly loading = signal(true);

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.taskService.findById(id).subscribe({
      next: (task) => {
        this.task.set(task);
        this.loading.set(false);
        this.projectService.findById(task.projectId).subscribe((p) => this.project.set(p));
      },
      error: () => {
        this.loading.set(false);
        this.snackBar.open('No se encontró la tarea', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/tasks']);
      },
    });
  }

  canManage(): boolean {
    const p = this.project();
    if (!p) return false;
    return (
      this.sessionStore.isAdmin() ||
      (this.sessionStore.isLeader() && p.leaderId === this.sessionStore.user()?.id)
    );
  }

  isDeveloper(): boolean {
    return !this.sessionStore.isAdmin() && !this.sessionStore.isLeader();
  }

  openEdit(): void {
    console.log('openEdit');
    const task = this.task();
    const project = this.project();
    if (!task || !project) return;

    const ref = this.dialog.open(TaskFormComponent, {
      width: '560px',
      data: { projectId: project.id, task },
    });

    ref.afterClosed().subscribe((updated: TaskResponse | null) => {
      if (updated) {
        this.task.set(updated);
        this.snackBar.open('Tarea actualizada', 'OK', { duration: 3000 });
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/tasks']);
  }

  isOverdue(dueDate: string): boolean {
    return new Date(dueDate).getTime() < Date.now();
  }

  isDueSoon(dueDate: string): boolean {
    const diff = new Date(dueDate).getTime() - Date.now();
    return diff > 0 && diff < 3 * 24 * 60 * 60 * 1000;
  }
}
