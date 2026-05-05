import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-admin-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="admin-shell">
      <aside class="sidebar">
        <a class="brand" routerLink="/admin">
          <span class="brand-mark">DSSI</span>
          <strong>Awareness</strong>
        </a>
        <nav class="nav" aria-label="Administration">
          <a routerLink="/admin" routerLinkActive="active" [routerLinkActiveOptions]="{ exact: true }">Dashboard</a>
          <a routerLink="/admin/campaigns" routerLinkActive="active">Campagnes</a>
          <a routerLink="/admin/email-templates" routerLinkActive="active">Templates email</a>
          <a routerLink="/admin/landing-pages" routerLinkActive="active">Pages formation</a>
          <a routerLink="/admin/quizzes" routerLinkActive="active">Quiz</a>
          <a routerLink="/admin/user-import" routerLinkActive="active">Import utilisateurs</a>
          <a routerLink="/admin/reports" routerLinkActive="active">Rapports</a>
          <a routerLink="/admin/audit-logs" routerLinkActive="active">Audit</a>
        </nav>
        <div class="role-panel">
          <span>Rôles interface</span>
          <small>{{ auth.roles().join(', ') }}</small>
        </div>
      </aside>
      <main class="content">
        <router-outlet />
      </main>
    </div>
  `
})
export class AdminLayoutComponent {
  constructor(readonly auth: AuthService) {}
}
