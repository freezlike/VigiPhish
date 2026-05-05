import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../../../../core/services/api.service';
import { EmailTemplate } from '../../../../core/models/api.models';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { buildEmailPreviewDocument } from '../../../../shared/email-html-preview';

@Component({
  selector: 'app-email-template-preview',
  imports: [RouterLink, PageHeaderComponent, EmptyStateComponent],
  template: `
    <app-page-header title="Prévisualisation email" description="Aperçu pédagogique sans envoi.">
      <a class="button button-ghost" routerLink="/admin/email-templates">Retour</a>
    </app-page-header>
    @if (template(); as current) {
      <section class="preview-shell">
        <div class="email-preview">
          <div class="email-header"><strong>Objet:</strong> {{ current.subject }}</div>
          <div class="email-context">{{ current.educationalContext }}</div>
          <iframe class="html-viewer-frame" title="Aperçu HTML email" sandbox [srcdoc]="previewDocument(current.body)"></iframe>
        </div>
        <aside class="compliance-note">
          Cette prévisualisation ne déclenche aucun tracking et n'envoie aucun email. Le HTML est rendu par Angular avec nettoyage de sécurité.
        </aside>
      </section>
    } @else {
      <app-empty-state title="Template introuvable" message="Le modèle demandé n'est pas disponible." />
    }
  `
})
export class EmailTemplatePreviewComponent implements OnInit {
  readonly template = signal<EmailTemplate | null>(null);

  constructor(private readonly api: ApiService, private readonly route: ActivatedRoute) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    this.api.listEmailTemplates().subscribe((templates) => this.template.set(templates.find((item) => item.id === id) ?? null));
  }

  previewDocument(body: string): string {
    return buildEmailPreviewDocument(body);
  }
}
