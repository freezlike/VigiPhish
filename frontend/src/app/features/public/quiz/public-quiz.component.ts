import { Component, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-public-quiz',
  imports: [RouterLink],
  template: `
    <main class="public-shell">
      <section class="public-panel">
        <p class="eyebrow">Quiz de sensibilisation</p>
        <h1>Avant de cliquer</h1>
        <div class="quiz-question">
          <strong>Quel est le meilleur réflexe si un message demande une action urgente inhabituelle ?</strong>
          <div class="choice-list">
            <button type="button" [class.selected]="answer() === 'verify'" (click)="answer.set('verify')">Vérifier par un canal interne fiable</button>
            <button type="button" [class.selected]="answer() === 'submit'" (click)="answer.set('submit')">Saisir rapidement ses identifiants</button>
            <button type="button" [class.selected]="answer() === 'forward'" (click)="answer.set('forward')">Transférer à toute son équipe</button>
          </div>
        </div>
        <button class="button" type="button" [disabled]="!answer() || submitted()" (click)="submit()">Valider</button>
        @if (submitted()) {
          <div class="compliance-note">
            Bonne réponse: vérifier par un canal interne fiable. Aucun contenu sensible n'a été demandé ou stocké.
          </div>
          <a routerLink="/admin" class="public-link">Retour plateforme</a>
        }
        @if (status()) {
          <p class="public-status">{{ status() }}</p>
        }
      </section>
    </main>
  `
})
export class PublicQuizComponent {
  readonly answer = signal<string | null>(null);
  readonly submitted = signal(false);
  readonly status = signal<string | null>(null);
  private readonly token = this.route.snapshot.paramMap.get('token') ?? '';

  constructor(private readonly route: ActivatedRoute, private readonly api: ApiService) {}

  submit(): void {
    this.api.recordPublicEvent({ token: this.token, eventType: 'QUIZ_COMPLETED' }).subscribe({
      next: () => {
        this.submitted.set(true);
        this.status.set('Quiz enregistré.');
      },
      error: () => {
        this.submitted.set(true);
        this.status.set('Session expirée ou invalide.');
      }
    });
  }
}
