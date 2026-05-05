import { Component, OnInit, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { LandingPage, User } from '../../../../core/models/api.models';
import { ApiService } from '../../../../core/services/api.service';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';

@Component({
  selector: 'app-campaign-editor',
  imports: [ReactiveFormsModule, RouterLink, PageHeaderComponent, LoadingStateComponent],
  template: `
    <app-page-header
      [title]="campaignId ? 'Modifier la campagne' : 'Créer une campagne'"
      description="Une campagne reste en brouillon tant qu'elle n'a pas été soumise puis validée."
    >
      <a class="button button-ghost" routerLink="/admin/campaigns">Retour</a>
    </app-page-header>

    @if (loading()) {
      <app-loading-state label="Chargement de la campagne" />
    } @else {
      <form class="form-panel" [formGroup]="form" (ngSubmit)="save()">
        <label>
          Nom
          <input formControlName="name" autocomplete="off" />
        </label>
        <label>
          Domaines internes autorisés
          <input formControlName="internalDomainAllowlist" placeholder="example.internal, filiale.internal" />
        </label>
        <label>
          Responsable optionnel
          <select formControlName="ownerId">
            <option value="">Aucun responsable</option>
            @for (user of users(); track user.id) {
              <option [value]="user.id">{{ user.displayName }} · {{ user.email }} · {{ user.role }}</option>
            }
          </select>
        </label>
        <label>
          Page de sensibilisation interne
          <select formControlName="landingPageId">
            <option value="">Page générique</option>
            @for (page of landingPages(); track page.id) {
              <option [value]="page.id">{{ page.name }}</option>
            }
          </select>
        </label>
        <label class="checkbox-row">
          <input type="checkbox" formControlName="validationRequired" />
          Validation obligatoire avant lancement
        </label>
        <div class="compliance-warning">
          Les campagnes doivent rester internes, pédagogiques et validées avant lancement. Aucun domaine externe trompeur ni mécanisme d'évasion ne doit être utilisé.
        </div>
        @if (error()) {
          <p class="form-error">{{ error() }}</p>
        }
        <div class="form-actions">
          <button type="button" class="button button-ghost" routerLink="/admin/campaigns">Annuler</button>
          <button type="submit" class="button" [disabled]="form.invalid || saving()">{{ saving() ? 'Enregistrement' : 'Enregistrer' }}</button>
        </div>
      </form>
    }
  `
})
export class CampaignEditorComponent implements OnInit {
  readonly form = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(200)]],
    ownerId: [''],
    landingPageId: [''],
    internalDomainAllowlist: ['example.internal', [Validators.required]],
    validationRequired: [true]
  });
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly error = signal<string | null>(null);
  readonly users = signal<User[]>([]);
  readonly landingPages = signal<LandingPage[]>([]);
  campaignId: string | null = null;

  constructor(
    private readonly fb: NonNullableFormBuilder,
    private readonly api: ApiService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.api.listUsers().subscribe({
      next: (users) => this.users.set(users.filter((user) => user.active)),
      error: () => this.users.set([])
    });
    this.api.listLandingPages().subscribe({
      next: (pages) => this.landingPages.set(pages),
      error: () => this.landingPages.set([])
    });

    this.campaignId = this.route.snapshot.paramMap.get('id');
    if (!this.campaignId) {
      return;
    }
    this.loading.set(true);
    this.api.getCampaign(this.campaignId).subscribe({
      next: (campaign) => {
        this.form.patchValue({
          name: campaign.name,
          ownerId: campaign.ownerId ?? '',
          landingPageId: campaign.landingPageId ?? '',
          internalDomainAllowlist: campaign.internalDomainAllowlist,
          validationRequired: campaign.validationRequired
        });
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Campagne indisponible.');
        this.loading.set(false);
      }
    });
  }

  save(): void {
    if (this.form.invalid) {
      return;
    }
    this.saving.set(true);
    const value = this.form.getRawValue();
    const request = {
      name: value.name,
      ownerId: value.ownerId || null,
      landingPageId: value.landingPageId || null,
      internalDomainAllowlist: value.internalDomainAllowlist,
      validationRequired: value.validationRequired
    };
    const call = this.campaignId ? this.api.updateCampaign(this.campaignId, request) : this.api.createCampaign(request);
    call.subscribe({
      next: (campaign) => this.router.navigate(['/admin/campaigns', campaign.id]),
      error: (error: unknown) => {
        this.error.set(this.formatError(error));
        this.saving.set(false);
      }
    });
  }

  private formatError(error: unknown): string {
    if (typeof error === 'object' && error !== null && 'error' in error) {
      const payload = (error as { error?: { message?: string; detail?: string; code?: string } }).error;
      return payload?.detail ?? payload?.message ?? payload?.code ?? 'Enregistrement impossible avec le contexte courant.';
    }

    return 'Enregistrement impossible avec le contexte courant.';
  }
}
