import { Component, OnInit, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApiService } from '../../../../core/services/api.service';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-landing-page-editor',
  imports: [ReactiveFormsModule, RouterLink, PageHeaderComponent],
  template: `
    <app-page-header [title]="pageId ? 'Modifier la page' : 'Nouvelle page de sensibilisation'" description="Créez une page informative interne sans formulaire ni collecte de contenu.">
      <a class="button button-ghost" routerLink="/admin/landing-pages">Retour</a>
    </app-page-header>
    <div class="editor-preview-grid">
      <form class="form-panel" [formGroup]="form" (ngSubmit)="save()">
        <label>Titre affiché <input formControlName="name" autocomplete="off" /></label>
        <label>Message d'introduction <textarea formControlName="educationalMessage" rows="4"></textarea></label>
        <label>Contenu informatif <textarea formControlName="content" rows="12"></textarea></label>
        <div class="compliance-warning">Page informative uniquement: aucun formulaire, aucun champ de mot de passe, aucune collecte de contenu sensible.</div>
        <div class="form-actions">
          <button type="button" class="button button-ghost" routerLink="/admin/landing-pages">Annuler</button>
          <button type="submit" class="button" [disabled]="form.invalid || saving()">Enregistrer</button>
        </div>
      </form>
      <aside class="panel html-viewer-panel">
        <div class="html-viewer-toolbar">
          <span>Aperçu page interne</span>
          <small>rendu texte sécurisé</small>
        </div>
        <section class="public-panel embedded-preview">
          <p class="eyebrow">Sensibilisation interne DSSI</p>
          <h1>{{ form.controls.name.value || 'Titre de la page' }}</h1>
          <p>{{ form.controls.educationalMessage.value || 'Message pédagogique introductif.' }}</p>
          <div class="awareness-content">
            @for (paragraph of contentParagraphs(); track paragraph) {
              <p>{{ paragraph }}</p>
            }
          </div>
        </section>
      </aside>
    </div>
  `
})
export class LandingPageEditorComponent implements OnInit {
  readonly form = this.fb.group({ name: ['', Validators.required], educationalMessage: ['', Validators.required], content: ['', Validators.required] });
  readonly saving = signal(false);
  pageId: string | null = null;
  constructor(private readonly fb: NonNullableFormBuilder, private readonly api: ApiService, private readonly route: ActivatedRoute, private readonly router: Router) {}
  ngOnInit(): void {
    this.pageId = this.route.snapshot.paramMap.get('id');
    if (!this.pageId) {
      return;
    }
    this.api.listLandingPages().subscribe((pages) => {
      const page = pages.find((item) => item.id === this.pageId);
      if (page) {
        this.form.patchValue(page);
      }
    });
  }
  save(): void {
    if (this.form.invalid) {
      return;
    }
    this.saving.set(true);
    const request = this.form.getRawValue();
    const call = this.pageId ? this.api.updateLandingPage(this.pageId, request) : this.api.createLandingPage(request);
    call.subscribe({ next: () => this.router.navigate(['/admin/landing-pages']), error: () => this.saving.set(false) });
  }

  contentParagraphs(): string[] {
    return (this.form.controls.content.value || 'Contenu de sensibilisation.').split(/\n+/).map((value) => value.trim()).filter(Boolean);
  }
}
