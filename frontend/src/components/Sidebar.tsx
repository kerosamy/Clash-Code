import { NavLink } from "react-router-dom";
import { routes } from '../routes/routes.config';
import { useFilteredRoutes } from '../hooks/useFilteredRoutes';
import { getUsername } from "../utils/jwtDecoder";

export default function Sidebar() {
  const sidebarRoutes = routes.filter(route => !route.path.includes('practice/prob'));
  const loggedInUsername = getUsername()
  const filteredRoutes = useFilteredRoutes(sidebarRoutes);

  return (
    <aside className="bg-sidebar w-sidebar min-w-sidebar min-h-screen p-sideBar-pad font-anta">
      <div className="mb-6 mt-4">
        <img src="/src/assets/logo.svg" alt="App Logo" className="w-full" />
      </div>
      
      <nav className="flex flex-col gap-2" aria-label="Main navigation">
        {filteredRoutes.map(({ name, path, icon }) => {
          const resolvedPath = path.includes(":username") && loggedInUsername
            ? path.replace(":username", loggedInUsername)
            : path;

          return (
            <NavLink
              key={path}
              to={resolvedPath}
              className={({ isActive }) =>
                isActive ? "sidebar-list-active" : "sidebar-list"
              }
            >
              <img src={icon} className="w-icon h-icon flex-shrink-0" alt={name} />
              <span className="text-xl font-medium">{name}</span>
            </NavLink>
          );
        })}
      </nav>
    </aside>
  );
}