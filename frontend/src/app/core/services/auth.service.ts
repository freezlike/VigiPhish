import { Injectable, signal } from '@angular/core';
import { UserRole } from '../models/api.models';

const STORAGE_KEY = 'dssi.roles';
const DEFAULT_ROLES: UserRole[] = [
  'ROLE_DSSI_ADMIN',
  'ROLE_CAMPAIGN_MANAGER',
  'ROLE_CAMPAIGN_VALIDATOR',
  'ROLE_REPORT_VIEWER',
  'ROLE_AUDITOR'
];

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly roleState = signal<UserRole[]>(this.loadRoles());
  readonly roles = this.roleState.asReadonly();

  hasAnyRole(requiredRoles: readonly UserRole[] | undefined): boolean {
    if (!requiredRoles || requiredRoles.length === 0) {
      return true;
    }

    const currentRoles = this.roleState();
    return requiredRoles.some((role) => currentRoles.includes(role));
  }

  token(): string | null {
    return localStorage.getItem('dssi.authToken');
  }

  setRoles(roles: UserRole[]): void {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(roles));
    this.roleState.set(roles);
  }

  private loadRoles(): UserRole[] {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      return DEFAULT_ROLES;
    }

    try {
      const roles = JSON.parse(raw) as UserRole[];
      return Array.isArray(roles) ? roles : DEFAULT_ROLES;
    } catch {
      return DEFAULT_ROLES;
    }
  }
}
