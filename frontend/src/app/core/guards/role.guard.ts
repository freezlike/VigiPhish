import { CanMatchFn, Route, UrlSegment } from '@angular/router';

export const roleGuard: CanMatchFn = (route: Route, segments: UrlSegment[]) => {
  void segments;
  const requiredRoles = route.data?.['roles'] as string[] | undefined;
  return !requiredRoles || requiredRoles.length === 0;
};
