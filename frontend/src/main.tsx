import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';
import Layout from "./Layout"
import { routes } from './routes/routes.config';
import NotFound from './pages/NotFound';
import "@fontsource/anta/400.css"; 

const router = createBrowserRouter([
  {
    path: '/',
    element: <Layout />,
    children: [
      { index: true, element: <Navigate to="profile" replace /> },
      
      ...routes.map(({ path, component: Component }) => ({
        path,
        element: <Component />
      })),
      
      { path: '*', element: <NotFound /> }
    ]
  }
]);

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>
);