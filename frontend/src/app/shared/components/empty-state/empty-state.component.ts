import { Component, input } from '@angular/core';

@Component({
  selector: 'app-empty-state',
  template: `
    <section class="state-box">
      <strong>{{ title() }}</strong>
      <p>{{ message() }}</p>
    </section>
  `
})
export class EmptyStateComponent {
  readonly title = input('Aucune donnée');
  readonly message = input('Aucun élément ne correspond à cette vue.');
}
