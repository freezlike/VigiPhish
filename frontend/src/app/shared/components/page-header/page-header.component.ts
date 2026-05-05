import { Component, input } from '@angular/core';

@Component({
  selector: 'app-page-header',
  template: `
    <header class="page-header">
      <div>
        <p class="eyebrow">{{ eyebrow() }}</p>
        <h1>{{ title() }}</h1>
        @if (description()) {
          <p class="description">{{ description() }}</p>
        }
      </div>
      <div class="actions">
        <ng-content />
      </div>
    </header>
  `
})
export class PageHeaderComponent {
  readonly eyebrow = input('Plateforme DSSI');
  readonly title = input.required<string>();
  readonly description = input<string | null>(null);
}
