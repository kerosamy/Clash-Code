import { Navigate, useLocation } from 'react-router-dom';
import { type ReactNode } from 'react';
import { getActiveMatch } from '../utils/matchState';

interface AuthProtectedRouteProps {
  children: ReactNode;
}

const AuthProtectedRoute = ({ children }: AuthProtectedRouteProps) => {
  const location = useLocation();
  const token = localStorage.getItem('token');

  if (!token || token === 'undefined') {
    const activeMatch = getActiveMatch();
    if (activeMatch) {
      sessionStorage.setItem('redirectAfterLogin', `/play-game/${activeMatch}`);
    } else {
      sessionStorage.setItem('redirectAfterLogin', location.pathname);
    }
    
    return <Navigate to="/sign-up" replace />;
  }

  return <>{children}</>;
};

export default AuthProtectedRoute;