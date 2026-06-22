import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog } from '@angular/material/dialog';

import { UserService } from '../../../services/private/user.service';
import { UserRole } from '../../../../shared/enums/user';
import { SelectableUser } from '../../../interfaces/public/user.interface';
import { UserCardComponent } from './user-card/user-card';
import { UserFormComponent } from './user-form/user-form';

interface RoleGroup {
  role: UserRole;
  label: string;
  users: SelectableUser[];
}

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    UserCardComponent,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss',
})
export class UsersComponent implements OnInit {
  private readonly userService = inject(UserService);
  private readonly dialog = inject(MatDialog);

  readonly users = signal<SelectableUser[]>([]);
  readonly loading = signal(false);
  readonly search = signal('');

  private readonly roleLabels: Record<UserRole, string> = {
    [UserRole.ADMINISTRADOR]: 'Administradores',
    [UserRole.LIDER]: 'Líderes',
    [UserRole.DESARROLLADOR]: 'Desarrolladores',
  };

  readonly filteredUsers = computed(() => {
    const text = this.search().trim().toLowerCase();

    if (!text) {
      return this.users();
    }

    return this.users().filter(
      (user) =>
        user.fullname.toLowerCase().includes(text) || user.username.toLowerCase().includes(text),
    );
  });

  readonly groupedUsers = computed<RoleGroup[]>(() => {
    const roles = [UserRole.ADMINISTRADOR, UserRole.LIDER, UserRole.DESARROLLADOR];

    return roles
      .map((role) => ({
        role,
        label: this.roleLabels[role],
        users: this.filteredUsers().filter((user) => user.role === role),
      }))
      .filter((group) => group.users.length > 0);
  });

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading.set(true);

    this.userService.findAll().subscribe({
      next: (users) => {
        this.users.set(users);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  createUser(): void {
    const dialogRef = this.dialog.open(UserFormComponent, {
      width: '600px',
      maxWidth: '95vw',
    });

    dialogRef.afterClosed().subscribe((created) => {
      if (!created) {
        return;
      }

      this.users.update((users) => [created, ...users]);
    });
  }
}
