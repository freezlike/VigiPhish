import { Component, OnInit, computed, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApiService } from '../../../../core/services/api.service';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { buildEmailPreviewDocument } from '../../../../shared/email-html-preview';

@Component({
  selector: 'app-email-template-editor',
  imports: [ReactiveFormsModule, RouterLink, PageHeaderComponent],
  template: `
    <app-page-header [title]="templateId ? 'Modifier le template' : 'Nouveau template'" description="Conservez un ton pédagogique et clairement interne.">
      <a class="button button-ghost" routerLink="/admin/email-templates">Retour</a>
    </app-page-header>
    <section class="editor-preview-grid">
      <form class="form-panel" [formGroup]="form" (ngSubmit)="save()">
        <label>Nom <input formControlName="name" /></label>
        <label>Objet <input formControlName="subject" /></label>
        <label>Contexte éducatif <textarea formControlName="educationalContext" rows="4"></textarea></label>
        <label>Corps HTML <textarea formControlName="body" rows="14" placeholder="<p>Bonjour...</p>"></textarea></label>
        <div class="compliance-warning">Ne pas demander de mot de passe, ne pas imiter un domaine externe et ne pas ajouter de contenu d'évasion antispam. Les scripts sont nettoyés par le rendu Angular.</div>
        <div class="form-actions">
          <button type="button" class="button button-ghost" routerLink="/admin/email-templates">Annuler</button>
          <button type="submit" class="button" [disabled]="form.invalid || saving()">{{ saving() ? 'Enregistrement' : 'Enregistrer' }}</button>
        </div>
      </form>

      <aside class="html-viewer-panel">
        <div class="html-viewer-toolbar">
          <span>HTML viewer</span>
          <small>Aperçu nettoyé</small>
        </div>
        <div class="email-preview">
          <div class="email-header"><strong>Objet:</strong> {{ form.controls.subject.value || 'Sans objet' }}</div>
          <div class="email-context">{{ form.controls.educationalContext.value || 'Contexte éducatif' }}</div>
          <iframe class="html-viewer-frame" title="Aperçu HTML email" sandbox [srcdoc]="bodyHtml()"></iframe>
        </div>
      </aside>
    </section>
  `
})
export class EmailTemplateEditorComponent implements OnInit {
  readonly form = this.fb.group({
    name: ['', Validators.required],
    subject: ['', Validators.required],
    educationalContext: ['', Validators.required],
    body: ['', Validators.required]
  });
  readonly saving = signal(false);
  readonly body = signal('');
  readonly bodyHtml = computed(() => buildEmailPreviewDocument(this.body()));
  templateId: string | null = null;

  constructor(private readonly fb: NonNullableFormBuilder, private readonly api: ApiService, private readonly route: ActivatedRoute, private readonly router: Router) {}

  ngOnInit(): void {
    this.body.set(this.form.controls.body.value);
    this.form.controls.body.valueChanges.subscribe((value) => this.body.set(value));

    this.templateId = this.route.snapshot.paramMap.get('id');
    if (!this.templateId) {
      return;
    }
    this.api.listEmailTemplates().subscribe((templates) => {
      const template = templates.find((item) => item.id === this.templateId);
      if (template) {
        this.form.patchValue(template);
        this.body.set(template.body);
      }
    });
  }

  save(): void {
    if (this.form.invalid) {
      return;
    }
    this.saving.set(true);
    const request = this.form.getRawValue();
    const call = this.templateId ? this.api.updateEmailTemplate(this.templateId, request) : this.api.createEmailTemplate(request);
    call.subscribe({
      next: () => this.router.navigate(['/admin/email-templates']),
      error: () => this.saving.set(false)
    });
  }
}
