import { Component, OnInit, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApiService } from '../../../../core/services/api.service';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-landing-page-editor',
  imports: [ReactiveFormsModule, RouterLink, PageHeaderComponent],
  template: `
    <app-page-header [title]="pageId ? 'Modifier la page' : 'Nouvelle page'" description="Expliquez les signaux de vigilance sans demander de secret.">
      <a class="button button-ghost" routerLink="/admin/landing-pages">Retour</a>
    </app-page-header>
    <form class="form-panel" [formGroup]="form" (ngSubmit)="save()">
      <label>Nom <input formControlName="name" /></label>
      <label>Message pédagogique <textarea formControlName="educationalMessage" rows="4"></textarea></label>
      <label>Contenu <textarea formControlName="content" rows="10"></textarea></label>
      <div class="compliance-warning">Aucun champ de mot de passe ou secret ne doit être demandé ni conservé.</div>
      <div class="form-actions">
        <button type="button" class="button button-ghost" routerLink="/admin/landing-pages">Annuler</button>
        <button type="submit" class="button" [disabled]="form.invalid || saving()">Enregistrer</button>
      </div>
    </form>
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
}
