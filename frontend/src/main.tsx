import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';
import Layout from './Layout';
import { routes, pages } from './routes/routes.config';

import "@fontsource/anta/400.css";
import RoleProtectedRoute from './routes/RoleProtectedRoute';
import AuthProtectedRoute from './routes/AuthProtectedRoute';
import { WebSocketProvider } from './contexts/WebSocketContext';
import { ConnectionStatus, GlobalNotificationToasts } 
from './components/notifications/GlobalNotificationComponents';

function mapRouteConfig() {
  return routes.map(({ path, component: Component, children, requiredRoles }) => {
    const base: any = { 
        path, 
        element: requiredRoles ? (
        <RoleProtectedRoute allowedRoles={requiredRoles}>
          <Component />
        </RoleProtectedRoute>
      ) : (
        <Component />
      ) 
    }; 

    if (children && children.length > 0) {
      base.children = children.map(child => {
        if (child.index) {
          return { index: true, element: <child.component /> };
        }
        return { path: child.path!, element: <child.component /> };
      });
    }

    return base;
  });
}

const router = createBrowserRouter([
  { index: true, element: <Navigate to="/sign-up" replace /> },

  // Public pages (no auth required)
  ...pages.map(({ path, component: Component }) => ({
    path,
    element: <Component />
  })),

  // Protected routes (wrapped with AuthProtectedRoute)
  {
    path: '/',
    element: (
      <AuthProtectedRoute>
        <Layout />
      </AuthProtectedRoute>
    ),
    children: mapRouteConfig()
  },

  { path: '*', element: <Navigate to="/not-found" replace /> }
]);

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <WebSocketProvider>
      <RouterProvider router={router} />
      <ConnectionStatus />
      <GlobalNotificationToasts />
    </WebSocketProvider>
  </StrictMode>
);