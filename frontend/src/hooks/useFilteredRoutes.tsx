import { useMemo } from 'react';
import { type RouteConfig } from '../routes/routes.config';
import { hasAnyRole } from '../utils/rolesChecker';

export const useFilteredRoutes = (routes: RouteConfig[]): RouteConfig[] => {
  return useMemo(() => {
    return routes.filter(route => {
      if (route.hideFromNav) return false;
      if (!route.requiredRoles || route.requiredRoles.length === 0) return true;
      return hasAnyRole(route.requiredRoles);
    });
  }, [routes]);
};