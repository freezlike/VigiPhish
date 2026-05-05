import { Component, OnInit, signal } from '@angular/core';
import { AuditLog } from '../../../core/models/api.models';
import { ApiService } from '../../../core/services/api.service';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { LoadingStateComponent } from '../../../shared/components/loading-state/loading-state.component';
import { DataTableColumn, DataTableComponent } from '../../../shared/components/data-table/data-table.component';

@Component({
  selector: 'app-audit-logs',
  imports: [PageHeaderComponent, EmptyStateComponent, LoadingStateComponent, DataTableComponent],
  template: `
    <app-page-header title="Audit logs" description="Traçabilité des mutations administratives." />
    @if (loading()) {
      <app-loading-state label="Chargement de l'audit" />
    } @else if (logs().length === 0) {
      <app-empty-state title="Aucun audit" message="Aucune action administrative n'est disponible." />
    } @else {
      <section class="panel">
        <app-data-table [columns]="columns" [rows]="rows()" />
      </section>
    }
  `
})
export class AuditLogsComponent implements OnInit {
  readonly columns: DataTableColumn[] = [
    { key: 'occurredAt', label: 'Date' },
    { key: 'action', label: 'Action' },
    { key: 'targetType', label: 'Cible' },
    { key: 'targetId', label: 'UUID cible' }
  ];
  readonly loading = signal(true);
  readonly logs = signal<AuditLog[]>([]);
  readonly rows = () => this.logs().map((log) => ({
    id: log.id,
    occurredAt: new Date(log.occurredAt).toLocaleString(),
    action: log.action,
    targetType: log.targetType,
    targetId: log.targetId ?? '-'
  }));
  constructor(private readonly api: ApiService) {}
  ngOnInit(): void {
    this.api.listAuditLogs().subscribe({
      next: (logs) => {
        this.logs.set(logs);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }
}
