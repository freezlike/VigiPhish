import { Component, computed, input } from '@angular/core';
import { CampaignStatus } from '../../../core/models/api.models';

@Component({
  selector: 'app-status-badge',
  template: `<span class="status-badge" [class]="className()">{{ label() }}</span>`
})
export class StatusBadgeComponent {
  readonly status = input.required<CampaignStatus | string>();

  readonly label = computed(() => this.status().replaceAll('_', ' '));
  readonly className = computed(() => `status-badge status-${this.status().toLowerCase().replaceAll('_', '-')}`);
}
