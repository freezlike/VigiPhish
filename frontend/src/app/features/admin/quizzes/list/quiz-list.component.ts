import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Quiz } from '../../../../core/models/api.models';
import { ApiService } from '../../../../core/services/api.service';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';

@Component({
  selector: 'app-quiz-list',
  imports: [RouterLink, PageHeaderComponent, EmptyStateComponent, LoadingStateComponent],
  template: `
    <app-page-header title="Quiz" description="Questionnaires de sensibilisation et rappel des bons réflexes.">
      <a class="button" routerLink="/admin/quizzes/new">Nouveau</a>
    </app-page-header>
    @if (loading()) {
      <app-loading-state label="Chargement des quiz" />
    } @else if (quizzes().length === 0) {
      <app-empty-state title="Aucun quiz" message="Ajoutez un quiz court pour renforcer les apprentissages." />
    } @else {
      <section class="panel list-panel">
        @for (quiz of quizzes(); track quiz.id) {
          <a class="item-row" [routerLink]="['/admin/quizzes', quiz.id, 'edit']">
            <div>
              <strong>{{ quiz.name }}</strong>
              <span>{{ quiz.description }}</span>
            </div>
          </a>
        }
      </section>
    }
  `
})
export class QuizListComponent implements OnInit {
  readonly loading = signal(true);
  readonly quizzes = signal<Quiz[]>([]);
  constructor(private readonly api: ApiService) {}
  ngOnInit(): void {
    this.api.listQuizzes().subscribe({
      next: (quizzes) => {
        this.quizzes.set(quizzes);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }
}
