import { Component, output, input } from '@angular/core';

@Component({
  selector: 'app-confirm-dialog',
  template: `
    @if (open()) {
      <div class="dialog-backdrop" role="presentation">
        <section class="dialog" role="dialog" aria-modal="true" [attr.aria-label]="title()">
          <h2>{{ title() }}</h2>
          <p>{{ message() }}</p>
          @if (warning()) {
            <div class="compliance-warning">{{ warning() }}</div>
          }
          <div class="dialog-actions">
            <button type="button" class="button button-ghost" (click)="cancel.emit()">Annuler</button>
            <button type="button" class="button button-danger" (click)="confirm.emit()">{{ confirmLabel() }}</button>
          </div>
        </section>
      </div>
    }
  `
})
export class ConfirmDialogComponent {
  readonly open = input(false);
  readonly title = input('Confirmation');
  readonly message = input('Confirmer cette action.');
  readonly warning = input<string | null>(null);
  readonly confirmLabel = input('Confirmer');
  readonly confirm = output<void>();
  readonly cancel = output<void>();
}
