import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';

import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { finalize } from 'rxjs';
import { ProjectRequest, ProjectResponse } from '../../../../interfaces/private/project.interface';
import { ProjectService } from '../../../../services/private/project.service';
import { MatIconModule } from '@angular/material/icon';

export interface ProjectFormData {
  project?: ProjectResponse;
}

@Component({
  selector: 'app-project-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './project-form.html',
  styleUrl: './project-form.scss',
})
export class ProjectFormComponent {
  private readonly fb = inject(FormBuilder);
  private readonly projectService = inject(ProjectService);
  private readonly dialogRef = inject(MatDialogRef<ProjectFormComponent>);
  readonly data = inject<ProjectFormData>(MAT_DIALOG_DATA, { optional: true }) ?? {};

  readonly loading = signal(false);
  readonly submitted = signal(false);

  readonly form = this.createForm();

  private createForm() {
    return this.fb.nonNullable.group({
      name: [
        this.data?.project?.name ?? '',
        [Validators.required, Validators.minLength(3), Validators.maxLength(100)],
      ],
      description: [this.data?.project?.description ?? '', Validators.maxLength(500)],
    });
  }

  submit(): void {
    this.submitted.set(true);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const request: ProjectRequest = this.form.getRawValue();

    this.loading.set(true);

    const operation = this.data?.project
      ? this.projectService.update(this.data.project.id, request)
      : this.projectService.create(request);

    operation.pipe(finalize(() => this.loading.set(false))).subscribe({
      next: (project) => this.dialogRef.close(project),
    });
  }
}
