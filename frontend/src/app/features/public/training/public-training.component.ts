import { Component, OnInit, computed, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PublicAwarenessPage } from '../../../core/models/api.models';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-public-training',
  imports: [RouterLink],
  template: `
    <main class="public-shell">
      <section class="public-panel">
        <p class="eyebrow">Sensibilisation interne DSSI</p>
        @if (loading()) {
          <h1>Chargement de la page</h1>
          <p>Validation du lien interne en cours.</p>
        } @else if (page()) {
          <h1>{{ page()?.title }}</h1>
          <p>{{ page()?.educationalMessage }}</p>
          <div class="awareness-content">
            @for (paragraph of contentParagraphs(); track paragraph) {
              <p>{{ paragraph }}</p>
            }
          </div>
          <div class="learning-grid">
            <article><strong>Vérifier</strong><span>Contrôlez le contexte, le domaine et le canal interne attendu.</span></article>
            <article><strong>Protéger</strong><span>Ne transmettez jamais de mot de passe ou de secret dans une page non vérifiée.</span></article>
            <article><strong>Signaler</strong><span>En cas de doute, utilisez le canal de signalement DSSI.</span></article>
          </div>
          <div class="actions">
            <button class="button" type="button" [disabled]="completed()" (click)="complete()">{{ completed() ? 'Progression enregistrée' : "J'ai compris" }}</button>
            <a class="button button-ghost" [routerLink]="['/public/quiz', token()]">Continuer vers le quiz</a>
          </div>
        } @else {
          <h1>Lien indisponible</h1>
          <p>La session est expirée ou invalide. Aucune donnée sensible n'a été demandée ni enregistrée.</p>
        }
        @if (status()) {
          <p class="public-status">{{ status() }}</p>
        }
      </section>
    </main>
  `
})
export class PublicTrainingComponent implements OnInit {
  readonly token = signal('');
  readonly loading = signal(true);
  readonly page = signal<PublicAwarenessPage | null>(null);
  readonly completed = signal(false);
  readonly status = signal<string | null>(null);
  readonly contentParagraphs = computed(() => (this.page()?.content ?? '').split(/\n+/).map((value) => value.trim()).filter(Boolean));
  constructor(private readonly route: ActivatedRoute, private readonly api: ApiService) {}
  ngOnInit(): void {
    this.token.set(this.route.snapshot.paramMap.get('token') ?? '');
    this.api.getPublicAwarenessPage(this.token()).subscribe({
      next: (page) => {
        this.page.set(page);
        this.loading.set(false);
        this.recordImpact();
      },
      error: () => {
        this.loading.set(false);
        this.status.set('Session expirée ou invalide.');
      }
    });
  }

  complete(): void {
    this.api.recordPublicEvent({ token: this.token(), eventType: 'TRAINING_COMPLETED' }).subscribe({
      next: () => {
        this.completed.set(true);
        this.status.set('Progression enregistrée.');
      },
      error: () => this.status.set('Session expirée ou invalide.')
    });
  }

  private recordImpact(): void {
    this.api.recordPublicEvent({ token: this.token(), eventType: 'LINK_CLICKED' }).subscribe({ error: () => undefined });
    this.api.recordPublicEvent({ token: this.token(), eventType: 'TRAINING_VIEWED' }).subscribe({
      next: () => this.status.set('Consultation enregistrée.'),
      error: () => this.status.set('Session expirée ou invalide.')
    });
  }
}
