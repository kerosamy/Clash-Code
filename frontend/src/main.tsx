import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';
import Layout from './Layout';
import { routes, pages, layoutOnlyRoutes } from './routes/routes.config';

import "@fontsource/anta/400.css";

const router = createBrowserRouter([
  { index: true, element: <Navigate to="/sign-up" replace /> },

  // Non-sidebar pages
  ...pages.map(({ path, component: Component }) => ({
    path,
    element: <Component />,
  })),

  // Main app routes with sidebar layout
  {
    path: '/',
    element: <Layout />,
    children: [
      ...routes.map(({ path, component: Component }) => ({
        path,
        element: <Component />,
      })),
      ...layoutOnlyRoutes.map(({ path, element: LayoutComponent, children }) => ({
        path,
        element: <LayoutComponent />,
        children: children?.map(child => ({
          path: child.path,
          index: child.index,
          element: <child.element />,
        })),
      })),
    ],
  },

  { path: '*', element: <Navigate to="/not-found" replace /> },
]);

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>
);
