import { Navigate } from 'react-router-dom';
import { type ReactNode } from 'react';
import { hasAnyRole } from '../utils/rolesChecker';
import type { UserRole } from '../enums/UserRole';

interface ProtectedRouteProps {
  children: ReactNode;
  allowedRoles: UserRole[];
  redirectTo?: string;
}

const ProtectedRoute = ({ 
  children, 
  allowedRoles, 
  redirectTo = '/not-found' 
}: ProtectedRouteProps) => {
  if (!hasAnyRole(allowedRoles)) {
    return <Navigate to={redirectTo} replace />;
  }
  
  return <>{children}</>;
};

export default ProtectedRoute;