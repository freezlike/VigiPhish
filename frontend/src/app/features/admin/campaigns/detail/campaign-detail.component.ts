import { DatePipe } from '@angular/common';
import { Component, OnInit, computed, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../../../../core/services/api.service';
import { Campaign, CampaignStatus, CampaignTarget } from '../../../../core/models/api.models';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';

const TIMELINE: CampaignStatus[] = ['DRAFT', 'PENDING_VALIDATION', 'VALIDATED', 'SCHEDULED', 'RUNNING', 'COMPLETED'];

@Component({
  selector: 'app-campaign-detail',
  imports: [
    DatePipe,
    ReactiveFormsModule,
    RouterLink,
    PageHeaderComponent,
    StatusBadgeComponent,
    LoadingStateComponent,
    EmptyStateComponent,
    ConfirmDialogComponent
  ],
  template: `
    @if (loading()) {
      <app-loading-state label="Chargement de la campagne" />
    } @else if (campaign(); as current) {
      <app-page-header [title]="current.name" description="Pilotage de validation, ciblage et actions contrôlées.">
        <a class="button button-ghost" [routerLink]="['/admin/campaigns', current.id, 'edit']">Modifier</a>
      </app-page-header>

      <section class="panel detail-grid">
        <div>
          <span class="field-label">Statut</span>
          <app-status-badge [status]="current.status" />
        </div>
        <div>
          <span class="field-label">Domaines</span>
          <strong>{{ current.internalDomainAllowlist }}</strong>
        </div>
        <div>
          <span class="field-label">Validation</span>
          <strong>{{ current.validationRequired ? 'Obligatoire' : 'Non requise' }}</strong>
        </div>
      </section>

      <section class="panel">
        <h2>Timeline statut</h2>
        <ol class="timeline">
          @for (status of timeline; track status) {
            <li [class.done]="isReached(status, current.status)">
              <span></span>
              {{ status.replaceAll('_', ' ') }}
            </li>
          }
        </ol>
      </section>

      <section class="panel action-panel">
        <h2>Actions gouvernées</h2>
        <p class="muted">Les actions de lancement restent désactivées tant que la validation attendue n'est pas acquise.</p>
        <div class="action-row">
          <button class="button" type="button" [disabled]="current.status !== 'DRAFT'" (click)="submitValidation()">Soumettre</button>
          <button class="button" type="button" [disabled]="current.status !== 'PENDING_VALIDATION'" (click)="validate()">Valider</button>
          <button class="button" type="button" [disabled]="current.status !== 'VALIDATED'" (click)="openSchedule()">Planifier</button>
          <button class="button button-danger" type="button" [disabled]="current.status !== 'SCHEDULED'" (click)="confirmLaunch.set(true)">Lancer</button>
          <button class="button button-ghost" type="button" [disabled]="current.status !== 'RUNNING'" (click)="complete()">Terminer</button>
          <button class="button button-ghost" type="button" [disabled]="current.status === 'COMPLETED' || current.status === 'CANCELLED'" (click)="cancel()">Annuler</button>
        </div>

        @if (showSchedule()) {
          <form class="inline-form" [formGroup]="scheduleForm" (ngSubmit)="schedule()">
            <label>
              Date UTC
              <input type="datetime-local" formControlName="scheduledAt" />
            </label>
            <button class="button" type="submit" [disabled]="scheduleForm.invalid">Confirmer la planification</button>
          </form>
        }
      </section>

      <section class="panel">
        <h2>Cibles de campagne</h2>
        <form class="inline-form" [formGroup]="targetForm" (ngSubmit)="createTarget()">
          <label>
            UUID utilisateur
            <input formControlName="userId" placeholder="UUID interne" />
          </label>
          <label>
            Expiration UTC optionnelle
            <input type="datetime-local" formControlName="expiresAt" />
          </label>
          <button class="button" type="submit" [disabled]="targetForm.invalid">Ajouter cible</button>
        </form>
        @if (createdToken()) {
          <div class="compliance-warning">Token créé. Il est affiché une seule fois; ne pas le stocker dans un document partagé. {{ createdToken() }}</div>
        }
        @if (targets().length === 0) {
          <app-empty-state title="Aucune cible" message="Ajoutez des utilisateurs internes autorisés." />
        } @else {
          <div class="target-list">
            @for (target of targets(); track target.id) {
              <div class="target-row">
                <span>{{ target.userId }}</span>
                <small>Expire: {{ target.expiresAt | date:'short' }} · Dernier événement: {{ target.lastEventAt ? (target.lastEventAt | date:'short') : '-' }}</small>
              </div>
            }
          </div>
        }
      </section>

      <app-confirm-dialog
        [open]="confirmLaunch()"
        title="Confirmer le lancement"
        message="Le lancement enverra une campagne interne validée aux cibles configurées."
        warning="Vérifiez les domaines internes, la validation four-eyes et l'absence de collecte de secrets avant de continuer."
        confirmLabel="Lancer"
        (cancel)="confirmLaunch.set(false)"
        (confirm)="start()"
      />
    } @else {
      <app-empty-state title="Campagne introuvable" message="La campagne demandée n'est pas disponible." />
    }
  `
})
export class CampaignDetailComponent implements OnInit {
  readonly timeline = TIMELINE;
  readonly loading = signal(true);
  readonly campaign = signal<Campaign | null>(null);
  readonly targets = signal<CampaignTarget[]>([]);
  readonly showSchedule = signal(false);
  readonly confirmLaunch = signal(false);
  readonly createdToken = signal<string | null>(null);
  readonly campaignId = computed(() => this.route.snapshot.paramMap.get('id') ?? '');
  readonly scheduleForm = this.fb.group({ scheduledAt: ['', Validators.required] });
  readonly targetForm = this.fb.group({ userId: ['', Validators.required], expiresAt: [''] });

  constructor(private readonly api: ApiService, private readonly route: ActivatedRoute, private readonly fb: NonNullableFormBuilder) {}

  ngOnInit(): void {
    this.reload();
  }

  isReached(status: CampaignStatus, current: CampaignStatus): boolean {
    return TIMELINE.indexOf(status) <= TIMELINE.indexOf(current);
  }

  submitValidation(): void {
    this.api.submitCampaign(this.campaignId()).subscribe((campaign) => this.campaign.set(campaign));
  }

  validate(): void {
    this.api.validateCampaign(this.campaignId()).subscribe((campaign) => this.campaign.set(campaign));
  }

  openSchedule(): void {
    this.showSchedule.set(true);
  }

  schedule(): void {
    const localValue = this.scheduleForm.getRawValue().scheduledAt;
    this.api.scheduleCampaign(this.campaignId(), new Date(localValue).toISOString()).subscribe((campaign) => {
      this.campaign.set(campaign);
      this.showSchedule.set(false);
    });
  }

  start(): void {
    this.api.startCampaign(this.campaignId()).subscribe((campaign) => {
      this.campaign.set(campaign);
      this.confirmLaunch.set(false);
    });
  }

  complete(): void {
    this.api.completeCampaign(this.campaignId()).subscribe((campaign) => this.campaign.set(campaign));
  }

  cancel(): void {
    this.api.cancelCampaign(this.campaignId()).subscribe((campaign) => this.campaign.set(campaign));
  }

  createTarget(): void {
    const value = this.targetForm.getRawValue();
    const expiresAt = value.expiresAt ? new Date(value.expiresAt).toISOString() : undefined;
    this.api.createTarget(this.campaignId(), value.userId, expiresAt).subscribe((created) => {
      this.createdToken.set(created.rawToken);
      this.targets.update((targets) => [...targets, created.target]);
      this.targetForm.reset();
    });
  }

  private reload(): void {
    const id = this.campaignId();
    this.api.getCampaign(id).subscribe({
      next: (campaign) => {
        this.campaign.set(campaign);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
    this.api.listTargets(id).subscribe({ next: (targets) => this.targets.set(targets), error: () => this.targets.set([]) });
  }
}
