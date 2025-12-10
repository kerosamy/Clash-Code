import { Navigate } from 'react-router-dom';
import { type ReactNode } from 'react';
import { hasAnyRole } from '../utils/rolesChecker';
import type { UserRole } from '../enums/UserRole';

interface RoleProtectedRouteProps {
  children: ReactNode;
  allowedRoles: UserRole[];
  redirectTo?: string;
}

const RoleProtectedRoute = ({ 
  children, 
  allowedRoles, 
  redirectTo = '/not-found' 
}: RoleProtectedRouteProps) => {
  if (!hasAnyRole(allowedRoles)) {
    return <Navigate to={redirectTo} replace />;
  }
  
  return <>{children}</>;
};

export default RoleProtectedRoute;