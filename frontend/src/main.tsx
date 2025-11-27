import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';
import Layout from './Layout';
import { routes, pages } from './routes/routes.config';

import "@fontsource/anta/400.css";

function mapRouteConfig() {
  return routes.map(({ path, component: Component, children }) => {
    const base: any = { path, element: <Component /> };

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

  ...pages.map(({ path, component: Component }) => ({
    path,
    element: <Component />
  })),

  {
    path: '/',
    element: <Layout />,
    children: mapRouteConfig()
  },

  { path: '*', element: <Navigate to="/not-found" replace /> }
]);

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>
);
