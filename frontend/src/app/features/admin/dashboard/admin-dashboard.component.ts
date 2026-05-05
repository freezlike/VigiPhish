import { Component, OnInit, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { Campaign, EmailTemplate, LandingPage, Quiz } from '../../../core/models/api.models';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { LoadingStateComponent } from '../../../shared/components/loading-state/loading-state.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-admin-dashboard',
  imports: [RouterLink, PageHeaderComponent, LoadingStateComponent, EmptyStateComponent, StatusBadgeComponent],
  template: `
    <app-page-header
      title="Tableau de bord"
      description="Vue de pilotage des campagnes de sensibilisation internes et des contenus pédagogiques."
    >
      <a class="button" routerLink="/admin/campaigns/new">Nouvelle campagne</a>
    </app-page-header>

    @if (loading()) {
      <app-loading-state label="Chargement du pilotage" />
    } @else if (error()) {
      <app-empty-state title="Données indisponibles" [message]="error()!" />
    } @else {
      <section class="metric-grid">
        <article class="metric"><span>Campagnes</span><strong>{{ campaigns().length }}</strong></article>
        <article class="metric"><span>En validation</span><strong>{{ pendingCount() }}</strong></article>
        <article class="metric"><span>Templates</span><strong>{{ templates().length }}</strong></article>
        <article class="metric"><span>Pages / Quiz</span><strong>{{ landingPages().length + quizzes().length }}</strong></article>
      </section>

      <section class="panel">
        <div class="panel-heading">
          <h2>Campagnes récentes</h2>
          <a routerLink="/admin/campaigns">Voir tout</a>
        </div>
        @if (campaigns().length === 0) {
          <app-empty-state title="Aucune campagne" message="Créez une campagne en brouillon avant validation." />
        } @else {
          <div class="campaign-list">
            @for (campaign of campaigns().slice(0, 5); track campaign.id) {
              <a class="campaign-row" [routerLink]="['/admin/campaigns', campaign.id]">
                <span>{{ campaign.name }}</span>
                <app-status-badge [status]="campaign.status" />
              </a>
            }
          </div>
        }
      </section>
    }
  `
})
export class AdminDashboardComponent implements OnInit {
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly campaigns = signal<Campaign[]>([]);
  readonly templates = signal<EmailTemplate[]>([]);
  readonly landingPages = signal<LandingPage[]>([]);
  readonly quizzes = signal<Quiz[]>([]);
  readonly pendingCount = computed(() => this.campaigns().filter((campaign) => campaign.status === 'PENDING_VALIDATION').length);

  constructor(private readonly api: ApiService) {}

  ngOnInit(): void {
    forkJoin({
      campaigns: this.api.listCampaigns(),
      templates: this.api.listEmailTemplates(),
      landingPages: this.api.listLandingPages(),
      quizzes: this.api.listQuizzes()
    }).subscribe({
      next: ({ campaigns, templates, landingPages, quizzes }) => {
        this.campaigns.set(campaigns);
        this.templates.set(templates);
        this.landingPages.set(landingPages);
        this.quizzes.set(quizzes);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Les APIs admin ne sont pas accessibles avec le contexte courant.');
        this.loading.set(false);
      }
    });
  }
}
