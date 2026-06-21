import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';

import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';

import { computed } from '@angular/core';
import { TaskService } from '../../../../services/private/task.service';
import { UserService } from '../../../../services/private/user.service';
import { SessionStore } from '../../../../services/auth/session-store.service';
import { TaskResponse, TaskRequest, TaskStatusRequest } from '../../../../interfaces/private/task.interface';
import { SelectableUser } from '../../../../interfaces/public/user.interface';
import { TaskStatus, TaskPriority } from '../../../../../shared/enums/task';
import { signal } from '@angular/core';

export interface TaskFormData {
  projectId: string;
  task: TaskResponse | null;
}

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule,  MatButtonModule, MatFormFieldModule, MatIconModule, MatInputModule, MatSelectModule, MatProgressSpinnerModule],
  templateUrl: './task-form.html',
  styleUrl: './task-form.scss',
})
export class TaskFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly taskService = inject(TaskService);
  private readonly userService = inject(UserService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly dialogRef = inject(MatDialogRef<TaskFormComponent>);
  readonly data: TaskFormData = inject(MAT_DIALOG_DATA);
  readonly sessionStore = inject(SessionStore);

  readonly TaskStatus = TaskStatus;
  readonly TaskPriority = TaskPriority;

  readonly developers = signal<SelectableUser[]>([]);
  readonly saving = signal(false);

  readonly isEdit = !!this.data.task;
  readonly isDeveloper = computed(() => !this.sessionStore.isAdmin() && !this.sessionStore.isLeader());

  //para admin/lider
  readonly form = this.fb.group({
    title: [this.data.task?.title ?? '', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
    description: [this.data.task?.description ?? ''],
    priority: [this.data.task?.priority ?? TaskPriority.MEDIO, Validators.required],
    dueDate: [this.data.task ? this.toDateInputValue(this.data.task.dueDate) : '', Validators.required],
    assignedUserId: [this.data.task?.assignedUserID ?? null],
  });

  //desarrolladores (solo cambiar estado)
  readonly statusForm = this.fb.group({
    newStatus: [this.data.task?.status ?? TaskStatus.PENDIENTE, Validators.required],
  });

  get allowedStatuses(): TaskStatus[] {
    const current = this.data.task?.status;
    //pendiente -> en progreso -> en revision -> completada
    const transitions: Record<string, TaskStatus[]> = {
      [TaskStatus.PENDIENTE]: [TaskStatus.EN_PROGRESO],
      [TaskStatus.EN_PROGRESO]: [TaskStatus.EN_REVISION],
      [TaskStatus.EN_REVISION]: [TaskStatus.COMPLETADA],
      [TaskStatus.COMPLETADA]: [],
    };
    const next = current ? transitions[current] ?? [] : [];
    return current ? [current, ...next] : Object.values(TaskStatus);
  }

  ngOnInit(): void {
    if (!this.isDeveloper) {
      this.userService.findDevelopers().subscribe((devs) => this.developers.set(devs));
    }
  }

  private toDateInputValue(dateStr: string): string {
    return dateStr ? dateStr.substring(0, 10) : '';
  }

  save(): void {
    if (this.isDeveloper()) {
      this.saveStatus();
    } else {
      this.saveTask();
    }
  }

  private saveTask(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    const v = this.form.getRawValue();
    const request: TaskRequest = {
      title: v.title!,
      description: v.description || null,
      priority: v.priority as TaskPriority,
      dueDate: v.dueDate!,
      assignedUserId: v.assignedUserId || null,
    };

    const op$ = this.isEdit
      ? this.taskService.update(this.data.task!.id, request)
      : this.taskService.create(this.data.projectId, request);

    op$.subscribe({
      next: (task) => { this.saving.set(false); this.dialogRef.close(task); },
      error: (err) => {
        this.saving.set(false);
        const msg = err?.error?.message ?? 'Error al guardar la tarea';
        this.snackBar.open(msg, 'Cerrar', { duration: 4000 });
      },
    });
  }

  private saveStatus(): void {
    if (this.statusForm.invalid || !this.data.task) return;
    this.saving.set(true);
    const req: TaskStatusRequest = { newStatus: this.statusForm.value.newStatus as TaskStatus };

    this.taskService.updateStatus(this.data.task.id, req as any).subscribe({
      next: (task) => { this.saving.set(false); this.dialogRef.close(task); },
      error: (err) => {
        this.saving.set(false);
        const msg = err?.error?.message ?? 'Transición de estado no permitida';
        this.snackBar.open(msg, 'Cerrar', { duration: 4000 });
      },
    });
  }

  cancel(): void {
    this.dialogRef.close(null);
  }
}