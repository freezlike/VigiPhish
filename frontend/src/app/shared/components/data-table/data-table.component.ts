import { Component, input, output } from '@angular/core';

export interface DataTableColumn {
  key: string;
  label: string;
}

@Component({
  selector: 'app-data-table',
  template: `
    <div class="table-wrap">
      <table class="data-table">
        <thead>
          <tr>
            @for (column of columns(); track column.key) {
              <th scope="col">{{ column.label }}</th>
            }
            @if (actionLabel()) {
              <th scope="col" class="action-col">{{ actionLabel() }}</th>
            }
          </tr>
        </thead>
        <tbody>
          @for (row of rows(); track row[idKey()]) {
            <tr>
              @for (column of columns(); track column.key) {
                <td>{{ format(row[column.key]) }}</td>
              }
              @if (actionLabel()) {
                <td class="action-col">
                  <button type="button" class="button button-small" (click)="rowAction.emit(row)">{{ actionLabel() }}</button>
                </td>
              }
            </tr>
          }
        </tbody>
      </table>
    </div>
  `
})
export class DataTableComponent {
  readonly columns = input.required<DataTableColumn[]>();
  readonly rows = input.required<Record<string, unknown>[]>();
  readonly idKey = input('id');
  readonly actionLabel = input<string | null>(null);
  readonly rowAction = output<Record<string, unknown>>();

  format(value: unknown): string {
    if (value === null || value === undefined || value === '') {
      return '-';
    }
    if (typeof value === 'boolean') {
      return value ? 'Oui' : 'Non';
    }
    return String(value);
  }
}
