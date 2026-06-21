import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { SessionStore } from '../../../features/services/auth/session-store.service';
import { AuthService } from '../../../features/services/auth/auth.service';
import { UserRole } from '../../../shared/enums/user';
import { NavigationItem } from '../../../features/interfaces/private/navigation-item.interface';

@Component({
  selector: 'app-private-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
  ],
  templateUrl: './private-layout.html',
  styleUrl: './private-layout.scss',
})
export class PrivateLayout {
  protected readonly sessionStore = inject(SessionStore);

  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly UserRole = UserRole;

  protected readonly menuItems: NavigationItem[] = [
    {
      label: 'Inicio',
      icon: 'home',
      route: '/home',
    },
    {
      label: 'Proyectos',
      icon: 'folder',
      route: '/projects',
    },
    {
      label: 'Tareas',
      icon: 'task',
      route: '/tasks',
    },
    {
      label: 'Usuarios',
      icon: 'group',
      route: '/users',
      roles: [UserRole.ADMINISTRADOR],
    },
  ];

  logout(): void {
    this.authService.logout();

    this.router.navigate(['/login']);
  }

  protected canShow(item: NavigationItem): boolean {
    if (!item.roles?.length) {
      return true;
    }
    return this.sessionStore.hasAnyRole(...item.roles);
  }
}
