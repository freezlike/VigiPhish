import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../../core/services/api.service';
import { Campaign } from '../../../../core/models/api.models';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-campaign-list',
  imports: [RouterLink, PageHeaderComponent, StatusBadgeComponent, LoadingStateComponent, EmptyStateComponent],
  template: `
    <app-page-header title="Campagnes" description="Préparez, validez et suivez les campagnes internes de sensibilisation.">
      <a class="button" routerLink="/admin/campaigns/new">Créer</a>
    </app-page-header>

    @if (loading()) {
      <app-loading-state label="Chargement des campagnes" />
    } @else if (campaigns().length === 0) {
      <app-empty-state title="Aucune campagne" message="Commencez par une campagne en brouillon." />
    } @else {
      <section class="panel list-panel">
        @for (campaign of campaigns(); track campaign.id) {
          <a class="item-row" [routerLink]="['/admin/campaigns', campaign.id]">
            <div>
              <strong>{{ campaign.name }}</strong>
              <span>Domaines internes: {{ campaign.internalDomainAllowlist }}</span>
            </div>
            <app-status-badge [status]="campaign.status" />
          </a>
        }
      </section>
    }
  `
})
export class CampaignListComponent implements OnInit {
  readonly loading = signal(true);
  readonly campaigns = signal<Campaign[]>([]);

  constructor(private readonly api: ApiService) {}

  ngOnInit(): void {
    this.api.listCampaigns().subscribe({
      next: (campaigns) => {
        this.campaigns.set(campaigns);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }
}
