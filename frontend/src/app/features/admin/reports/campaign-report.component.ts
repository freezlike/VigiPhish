import { Component, signal } from '@angular/core';
import { ReactiveFormsModule, NonNullableFormBuilder, Validators } from '@angular/forms';
import { CampaignReport } from '../../../core/models/api.models';
import { ApiService } from '../../../core/services/api.service';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-campaign-report',
  imports: [ReactiveFormsModule, PageHeaderComponent, EmptyStateComponent],
  template: `
    <app-page-header title="Rapport campagne" description="Vue agrégée, sans exposer les tokens ni l'identité issue des endpoints publics." />
    <form class="inline-form panel" [formGroup]="form" (ngSubmit)="load()">
      <label>UUID campagne <input formControlName="campaignId" /></label>
      <button class="button" type="submit" [disabled]="form.invalid">Charger</button>
    </form>
    @if (report(); as current) {
      <section class="metric-grid">
        <article class="metric"><span>Cibles</span><strong>{{ current.targets }}</strong></article>
        @for (entry of eventEntries(current); track entry[0]) {
          <article class="metric"><span>{{ entry[0].replaceAll('_', ' ') }}</span><strong>{{ entry[1] }}</strong></article>
        }
      </section>
    } @else if (loaded()) {
      <app-empty-state title="Rapport indisponible" message="Aucune donnée agrégée disponible pour cette campagne." />
    }
  `
})
export class CampaignReportComponent {
  readonly form = this.fb.group({ campaignId: ['', Validators.required] });
  readonly report = signal<CampaignReport | null>(null);
  readonly loaded = signal(false);
  constructor(private readonly fb: NonNullableFormBuilder, private readonly api: ApiService) {}
  load(): void {
    this.api.getCampaignReport(this.form.getRawValue().campaignId).subscribe({
      next: (report) => {
        this.report.set(report);
        this.loaded.set(true);
      },
      error: () => {
        this.report.set(null);
        this.loaded.set(true);
      }
    });
  }
  eventEntries(report: CampaignReport): [string, number][] {
    return Object.entries(report.events ?? {});
  }
}
