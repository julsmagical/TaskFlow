import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';

import { finalize } from 'rxjs';

import { UserService } from '../../../../services/private/user.service';
import { UserRole } from '../../../../../shared/enums/user';
import { UserRequest } from '../../../../interfaces/public/user.interface';
import { ROLE_NAME_LABELS } from '../../../../../shared/constants/user';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './user-form.html',
  styleUrl: './user-form.scss',
})
export class UserFormComponent {
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly dialogRef = inject(MatDialogRef<UserFormComponent>);

  readonly loading = signal(false);
  readonly submitted = signal(false);

  readonly roles = Object.values(UserRole);

  readonly roleNameLabel = ROLE_NAME_LABELS;

  readonly form = this.createForm();

  private createForm() {
    return this.fb.nonNullable.group({
      fullname: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      role: [UserRole.DESARROLLADOR, Validators.required],
    });
  }

  submit(): void {
    this.submitted.set(true);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const request: UserRequest = this.form.getRawValue();

    this.loading.set(true);

    this.userService
      .create(request)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (user) => this.dialogRef.close(user),
      });
  }
}
