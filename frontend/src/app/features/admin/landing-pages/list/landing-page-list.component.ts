import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { LandingPage } from '../../../../core/models/api.models';
import { ApiService } from '../../../../core/services/api.service';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';

@Component({
  selector: 'app-landing-page-list',
  imports: [RouterLink, PageHeaderComponent, EmptyStateComponent, LoadingStateComponent],
  template: `
    <app-page-header title="Pages de sensibilisation" description="Pages internes informatives, reliées aux campagnes pour mesurer l'impact pédagogique.">
      <a class="button" routerLink="/admin/landing-pages/new">Nouvelle</a>
    </app-page-header>
    @if (loading()) {
      <app-loading-state label="Chargement des pages" />
    } @else if (pages().length === 0) {
      <app-empty-state title="Aucune page" message="Ajoutez une page de sensibilisation claire, interne et sans formulaire." />
    } @else {
      <section class="panel list-panel">
        @for (page of pages(); track page.id) {
          <a class="item-row" [routerLink]="['/admin/landing-pages', page.id, 'edit']">
            <div>
              <strong>{{ page.name }}</strong>
              <span>{{ page.educationalMessage }}</span>
            </div>
          </a>
        }
      </section>
    }
  `
})
export class LandingPageListComponent implements OnInit {
  readonly loading = signal(true);
  readonly pages = signal<LandingPage[]>([]);
  constructor(private readonly api: ApiService) {}
  ngOnInit(): void {
    this.api.listLandingPages().subscribe({
      next: (pages) => {
        this.pages.set(pages);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }
}
