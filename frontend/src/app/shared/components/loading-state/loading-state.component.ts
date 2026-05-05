import { Component, input } from '@angular/core';

@Component({
  selector: 'app-loading-state',
  template: `
    <section class="state-box state-box-loading" aria-live="polite">
      <span class="spinner" aria-hidden="true"></span>
      <strong>{{ label() }}</strong>
    </section>
  `
})
export class LoadingStateComponent {
  readonly label = input('Chargement');
}
