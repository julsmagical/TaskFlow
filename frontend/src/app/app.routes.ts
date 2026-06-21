import { Routes } from '@angular/router';
import { PublicLayout } from './core/layouts/public/public-layout';
import { authGuard } from './core/guards/auth.guard';
import { PrivateLayout } from './core/layouts/private/private-layout';

export const routes: Routes = [
  {
    path: '',
    component: PublicLayout,
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
    path: '',
    pathMatch: 'full',
    redirectTo: 'login',
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
