import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'admin',
    pathMatch: 'full'
  },
  {
    path: 'admin',
    loadChildren: () => import('./features/admin/admin.routes').then((module) => module.adminRoutes)
  },
  {
    path: 'public/training/:token',
    loadComponent: () => import('./features/public/training/public-training.component').then((module) => module.PublicTrainingComponent)
  },
  {
    path: 'public/quiz/:token',
    loadComponent: () => import('./features/public/quiz/public-quiz.component').then((module) => module.PublicQuizComponent)
  },
  {
    path: '**',
    redirectTo: 'admin'
  }
];
