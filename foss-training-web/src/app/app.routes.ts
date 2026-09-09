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
  }
];
