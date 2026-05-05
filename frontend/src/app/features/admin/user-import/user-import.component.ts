import { Component, signal } from '@angular/core';
import { ReactiveFormsModule, NonNullableFormBuilder, Validators } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { UserRequest, UserRole } from '../../../core/models/api.models';
import { ApiService } from '../../../core/services/api.service';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';

interface ImportPreview extends UserRequest {
  line: number;
}

@Component({
  selector: 'app-user-import',
  imports: [ReactiveFormsModule, PageHeaderComponent],
  template: `
    <app-page-header title="Import utilisateurs CSV" description="Import interne minimal: email, nom d'affichage, rôle. Aucun mot de passe n'est accepté." />
    <form class="form-panel" [formGroup]="form" (ngSubmit)="parse()">
      <label>
        CSV
        <textarea rows="10" formControlName="csv" placeholder="email,displayName,role&#10;alice@example.internal,Alice,ROLE_USER"></textarea>
      </label>
      <div class="compliance-warning">Les colonnes de mot de passe ou secret sont ignorées et doivent être retirées du fichier source.</div>
      <div class="form-actions">
        <button class="button" type="submit" [disabled]="form.invalid">Prévisualiser</button>
        <button class="button button-ghost" type="button" [disabled]="preview().length === 0 || importing()" (click)="importUsers()">Importer</button>
      </div>
    </form>

    @if (preview().length > 0) {
      <section class="panel">
        <h2>Prévisualisation</h2>
        <div class="target-list">
          @for (user of preview(); track user.line) {
            <div class="target-row">
              <span>{{ user.email }} · {{ user.displayName }}</span>
              <small>{{ user.role }} · actif: {{ user.active ? 'oui' : 'non' }}</small>
            </div>
          }
        </div>
      </section>
    }

    @if (message()) {
      <section class="state-box"><strong>{{ message() }}</strong></section>
    }
  `
})
export class UserImportComponent {
  readonly roles: UserRole[] = ['ROLE_DSSI_ADMIN', 'ROLE_CAMPAIGN_MANAGER', 'ROLE_CAMPAIGN_VALIDATOR', 'ROLE_REPORT_VIEWER', 'ROLE_AUDITOR', 'ROLE_USER'];
  readonly form = this.fb.group({ csv: ['', Validators.required] });
  readonly preview = signal<ImportPreview[]>([]);
  readonly importing = signal(false);
  readonly message = signal<string | null>(null);

  constructor(private readonly fb: NonNullableFormBuilder, private readonly api: ApiService) {}

  parse(): void {
    const lines = this.form.getRawValue().csv.split(/\r?\n/).map((line) => line.trim()).filter(Boolean);
    const dataLines = lines[0]?.toLowerCase().startsWith('email,') ? lines.slice(1) : lines;
    const users = dataLines.map((line, index) => {
      const [email, displayName, role] = line.split(',').map((item) => item.trim());
      return { line: index + 1, email, displayName, role: (role || 'ROLE_USER') as UserRole, active: true };
    }).filter((user) => user.email && user.displayName && this.roles.includes(user.role));
    this.preview.set(users);
    this.message.set(users.length === 0 ? 'Aucune ligne valide détectée.' : null);
  }

  importUsers(): void {
    this.importing.set(true);
    forkJoin(
      this.preview().map(({ line, ...user }) =>
        this.api.createUser(user).pipe(catchError(() => of(null)))
      )
    ).subscribe((results) => {
      const success = results.filter(Boolean).length;
      this.message.set(`${success} utilisateur(s) importé(s).`);
      this.importing.set(false);
    });
  }
}
