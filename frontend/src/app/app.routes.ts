import { Routes } from '@angular/router';
import { PublicLayout } from './core/layouts/public/public-layout';
import { authGuard } from './core/guards/auth.guard';
import { PrivateLayout } from './core/layouts/private/private-layout';
import { guestGuard } from './core/guards/guest.guard';
import { rootGuard } from './core/guards/root.guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    canActivate: [rootGuard],
    children: [],
  },
  {
    path: '',
    component: PublicLayout,
    canActivateChild: [guestGuard],
    children: [
      {
        path: 'login',
        loadComponent: () =>
          import('./features/pages/public/login/login').then((c) => c.LoginComponent),
      },
    ],
  },
  {
    path: '',
    component: PrivateLayout,
    canActivateChild: [authGuard],
    children: [
      {
        path: 'home',
        loadComponent: () =>
          import('./features/pages/private/home/home').then((c) => c.HomeComponent),
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
