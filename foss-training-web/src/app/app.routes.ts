import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'exercises',
    pathMatch: 'full'
  },
  {
    path: 'exercises',
    loadChildren: () => import('./features/exercises/exercises.routes')
  },
  {
    path: 'sessions',
    loadChildren: () => import('./features/sessions/sessions.routes')
  },
  {
    path: 'trainings',
    loadChildren: () => import('./features/trainings/trainings.routes')
  },
  {
    path: 'programs',
    loadChildren: () => import('./features/programs/programs.routes')
  },
  {
    path: 'analytics',
    loadChildren: () => import('./features/analytics/analytics.routes')
  }
];
