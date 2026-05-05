import { inject } from '@angular/core';
import { CanMatchFn, Route, Router, UrlSegment } from '@angular/router';
import { UserRole } from '../models/api.models';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanMatchFn = (route: Route, segments: UrlSegment[]) => {
  void segments;
  const requiredRoles = route.data?.['roles'] as UserRole[] | undefined;
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.hasAnyRole(requiredRoles) ? true : router.parseUrl('/admin');
};
