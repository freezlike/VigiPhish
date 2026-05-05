import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-public-training',
  imports: [RouterLink],
  template: `
    <main class="public-shell">
      <section class="public-panel">
        <p class="eyebrow">Sensibilisation interne</p>
        <h1>Reconnaître les signaux d'alerte</h1>
        <p>
          Cette page de formation explique les bons réflexes: vérifier l'expéditeur, contrôler le contexte,
          éviter les actions précipitées et signaler les messages suspects à l'équipe sécurité.
        </p>
        <div class="learning-grid">
          <article><strong>Vérifier</strong><span>Contrôlez l'adresse, le domaine et le contexte métier.</span></article>
          <article><strong>Limiter</strong><span>Ne transmettez jamais de mot de passe ou secret dans un formulaire.</span></article>
          <article><strong>Signaler</strong><span>En cas de doute, utilisez le canal interne de signalement.</span></article>
        </div>
        <a class="button" [routerLink]="['/public/quiz', token()]">Continuer vers le quiz</a>
        @if (status()) {
          <p class="public-status">{{ status() }}</p>
        }
      </section>
    </main>
  `
})
export class PublicTrainingComponent implements OnInit {
  readonly token = signal('');
  readonly status = signal<string | null>(null);
  constructor(private readonly route: ActivatedRoute, private readonly api: ApiService) {}
  ngOnInit(): void {
    this.token.set(this.route.snapshot.paramMap.get('token') ?? '');
    this.api.recordPublicEvent({ token: this.token(), eventType: 'TRAINING_VIEWED' }).subscribe({
      next: () => this.status.set('Progression enregistrée.'),
      error: () => this.status.set('Session expirée ou invalide.')
    });
  }
}
