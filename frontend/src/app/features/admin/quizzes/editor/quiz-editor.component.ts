import { Component, OnInit, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApiService } from '../../../../core/services/api.service';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-quiz-editor',
  imports: [ReactiveFormsModule, RouterLink, PageHeaderComponent],
  template: `
    <app-page-header [title]="quizId ? 'Modifier le quiz' : 'Nouveau quiz'" description="Questions pédagogiques, sans demander de secret personnel.">
      <a class="button button-ghost" routerLink="/admin/quizzes">Retour</a>
    </app-page-header>
    <form class="form-panel" [formGroup]="form" (ngSubmit)="save()">
      <label>Nom <input formControlName="name" /></label>
      <label>Description <textarea formControlName="description" rows="8"></textarea></label>
      <div class="form-actions">
        <button type="button" class="button button-ghost" routerLink="/admin/quizzes">Annuler</button>
        <button type="submit" class="button" [disabled]="form.invalid || saving()">Enregistrer</button>
      </div>
    </form>
  `
})
export class QuizEditorComponent implements OnInit {
  readonly form = this.fb.group({ name: ['', Validators.required], description: ['', Validators.required] });
  readonly saving = signal(false);
  quizId: string | null = null;
  constructor(private readonly fb: NonNullableFormBuilder, private readonly api: ApiService, private readonly route: ActivatedRoute, private readonly router: Router) {}
  ngOnInit(): void {
    this.quizId = this.route.snapshot.paramMap.get('id');
    if (!this.quizId) {
      return;
    }
    this.api.listQuizzes().subscribe((quizzes) => {
      const quiz = quizzes.find((item) => item.id === this.quizId);
      if (quiz) {
        this.form.patchValue(quiz);
      }
    });
  }
  save(): void {
    if (this.form.invalid) {
      return;
    }
    this.saving.set(true);
    const request = this.form.getRawValue();
    const call = this.quizId ? this.api.updateQuiz(this.quizId, request) : this.api.createQuiz(request);
    call.subscribe({ next: () => this.router.navigate(['/admin/quizzes']), error: () => this.saving.set(false) });
  }
}
