import { Routes } from '@angular/router';
import { roleGuard } from '../../core/guards/role.guard';

export const adminRoutes: Routes = [
  {
    path: '',
    loadComponent: () => import('./layout/admin-layout.component').then((m) => m.AdminLayoutComponent),
    children: [
      { path: '', pathMatch: 'full', loadComponent: () => import('./dashboard/admin-dashboard.component').then((m) => m.AdminDashboardComponent) },
      {
        path: 'campaigns',
        canMatch: [roleGuard],
        data: { roles: ['ROLE_DSSI_ADMIN', 'ROLE_CAMPAIGN_MANAGER', 'ROLE_CAMPAIGN_VALIDATOR', 'ROLE_REPORT_VIEWER'] },
        loadComponent: () => import('./campaigns/list/campaign-list.component').then((m) => m.CampaignListComponent)
      },
      {
        path: 'campaigns/new',
        canMatch: [roleGuard],
        data: { roles: ['ROLE_DSSI_ADMIN', 'ROLE_CAMPAIGN_MANAGER'] },
        loadComponent: () => import('./campaigns/editor/campaign-editor.component').then((m) => m.CampaignEditorComponent)
      },
      {
        path: 'campaigns/:id/edit',
        canMatch: [roleGuard],
        data: { roles: ['ROLE_DSSI_ADMIN', 'ROLE_CAMPAIGN_MANAGER'] },
        loadComponent: () => import('./campaigns/editor/campaign-editor.component').then((m) => m.CampaignEditorComponent)
      },
      {
        path: 'campaigns/:id',
        canMatch: [roleGuard],
        data: { roles: ['ROLE_DSSI_ADMIN', 'ROLE_CAMPAIGN_MANAGER', 'ROLE_CAMPAIGN_VALIDATOR', 'ROLE_REPORT_VIEWER'] },
        loadComponent: () => import('./campaigns/detail/campaign-detail.component').then((m) => m.CampaignDetailComponent)
      },
      {
        path: 'email-templates',
        canMatch: [roleGuard],
        data: { roles: ['ROLE_DSSI_ADMIN', 'ROLE_CAMPAIGN_MANAGER'] },
        loadComponent: () => import('./templates/list/email-template-list.component').then((m) => m.EmailTemplateListComponent)
      },
      {
        path: 'email-templates/new',
        canMatch: [roleGuard],
        data: { roles: ['ROLE_DSSI_ADMIN', 'ROLE_CAMPAIGN_MANAGER'] },
        loadComponent: () => import('./templates/editor/email-template-editor.component').then((m) => m.EmailTemplateEditorComponent)
      },
      {
        path: 'email-templates/:id/edit',
        canMatch: [roleGuard],
        data: { roles: ['ROLE_DSSI_ADMIN', 'ROLE_CAMPAIGN_MANAGER'] },
        loadComponent: () => import('./templates/editor/email-template-editor.component').then((m) => m.EmailTemplateEditorComponent)
      },
      {
        path: 'email-templates/:id/preview',
        canMatch: [roleGuard],
        data: { roles: ['ROLE_DSSI_ADMIN', 'ROLE_CAMPAIGN_MANAGER'] },
        loadComponent: () => import('./templates/preview/email-template-preview.component').then((m) => m.EmailTemplatePreviewComponent)
      },
      {
        path: 'landing-pages',
        canMatch: [roleGuard],
        data: { roles: ['ROLE_DSSI_ADMIN', 'ROLE_CAMPAIGN_MANAGER'] },
        loadComponent: () => import('./landing-pages/list/landing-page-list.component').then((m) => m.LandingPageListComponent)
      },
      {
        path: 'landing-pages/new',
        canMatch: [roleGuard],
        data: { roles: ['ROLE_DSSI_ADMIN', 'ROLE_CAMPAIGN_MANAGER'] },
        loadComponent: () => import('./landing-pages/editor/landing-page-editor.component').then((m) => m.LandingPageEditorComponent)
      },
      {
        path: 'landing-pages/:id/edit',
        canMatch: [roleGuard],
        data: { roles: ['ROLE_DSSI_ADMIN', 'ROLE_CAMPAIGN_MANAGER'] },
        loadComponent: () => import('./landing-pages/editor/landing-page-editor.component').then((m) => m.LandingPageEditorComponent)
      },
      {
        path: 'quizzes',
        canMatch: [roleGuard],
        data: { roles: ['ROLE_DSSI_ADMIN', 'ROLE_CAMPAIGN_MANAGER'] },
        loadComponent: () => import('./quizzes/list/quiz-list.component').then((m) => m.QuizListComponent)
      },
      {
        path: 'quizzes/new',
        canMatch: [roleGuard],
        data: { roles: ['ROLE_DSSI_ADMIN', 'ROLE_CAMPAIGN_MANAGER'] },
        loadComponent: () => import('./quizzes/editor/quiz-editor.component').then((m) => m.QuizEditorComponent)
      },
      {
        path: 'quizzes/:id/edit',
        canMatch: [roleGuard],
        data: { roles: ['ROLE_DSSI_ADMIN', 'ROLE_CAMPAIGN_MANAGER'] },
        loadComponent: () => import('./quizzes/editor/quiz-editor.component').then((m) => m.QuizEditorComponent)
      },
      {
        path: 'user-import',
        canMatch: [roleGuard],
        data: { roles: ['ROLE_DSSI_ADMIN'] },
        loadComponent: () => import('./user-import/user-import.component').then((m) => m.UserImportComponent)
      },
      {
        path: 'reports',
        canMatch: [roleGuard],
        data: { roles: ['ROLE_DSSI_ADMIN', 'ROLE_REPORT_VIEWER'] },
        loadComponent: () => import('./reports/campaign-report.component').then((m) => m.CampaignReportComponent)
      },
      {
        path: 'audit-logs',
        canMatch: [roleGuard],
        data: { roles: ['ROLE_DSSI_ADMIN', 'ROLE_AUDITOR'] },
        loadComponent: () => import('./audit-logs/audit-logs.component').then((m) => m.AuditLogsComponent)
      }
    ]
  }
];
