import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../../core/services/api.service';
import { EmailTemplate } from '../../../../core/models/api.models';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-email-template-list',
  imports: [RouterLink, PageHeaderComponent, EmptyStateComponent, LoadingStateComponent, ConfirmDialogComponent],
  template: `
    <app-page-header title="Templates email" description="Modèles pédagogiques internes, sans usurpation externe ni évasion.">
      <a class="button" routerLink="/admin/email-templates/new">Nouveau</a>
    </app-page-header>
    @if (loading()) {
      <app-loading-state label="Chargement des templates" />
    } @else if (templates().length === 0) {
      <app-empty-state title="Aucun template" message="Créez un modèle pédagogique de départ." />
    } @else {
      <section class="panel list-panel">
        @for (template of templates(); track template.id) {
          <div class="item-row">
            <div>
              <strong>{{ template.name }}</strong>
              <span>{{ template.subject }}</span>
            </div>
            <div class="row-actions">
              <a class="button button-small button-ghost" [routerLink]="['/admin/email-templates', template.id, 'preview']">Prévisualiser</a>
              <a class="button button-small" [routerLink]="['/admin/email-templates', template.id, 'edit']">Modifier</a>
              <button type="button" class="button button-small button-danger" (click)="askDelete(template)">Supprimer</button>
            </div>
          </div>
        }
      </section>
    }

    @if (error()) {
      <section class="state-box state-box-error"><strong>{{ error() }}</strong></section>
    }

    <app-confirm-dialog
      [open]="templateToDelete() !== null"
      title="Supprimer le template"
      [message]="deleteMessage()"
      warning="La suppression est autorisée uniquement si ce template n'est utilisé nulle part."
      confirmLabel="Supprimer"
      (cancel)="templateToDelete.set(null)"
      (confirm)="deleteSelected()"
    />
  `
})
export class EmailTemplateListComponent implements OnInit {
  readonly loading = signal(true);
  readonly templates = signal<EmailTemplate[]>([]);
  readonly templateToDelete = signal<EmailTemplate | null>(null);
  readonly error = signal<string | null>(null);

  constructor(private readonly api: ApiService) {}

  ngOnInit(): void {
    this.api.listEmailTemplates().subscribe({
      next: (templates) => {
        this.templates.set(templates);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  askDelete(template: EmailTemplate): void {
    this.error.set(null);
    this.templateToDelete.set(template);
  }

  deleteMessage(): string {
    const template = this.templateToDelete();
    return template ? `Supprimer "${template.name}" ?` : 'Supprimer ce template ?';
  }

  deleteSelected(): void {
    const template = this.templateToDelete();
    if (!template) {
      return;
    }

    this.api.deleteEmailTemplate(template.id).subscribe({
      next: () => {
        this.templates.update((templates) => templates.filter((item) => item.id !== template.id));
        this.templateToDelete.set(null);
      },
      error: () => {
        this.error.set('Suppression impossible: ce template est utilisé ou le backend a refusé la suppression.');
        this.templateToDelete.set(null);
      }
    });
  }
}
