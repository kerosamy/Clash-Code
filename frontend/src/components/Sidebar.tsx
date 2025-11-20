import { NavLink } from "react-router-dom";
import { routes } from '../routes/routes.config';


export default function Sidebar() {
  return (
    <aside className="bg-sidebar w-sidebar min-w-sidebar min-h-screen p-sideBar-pad font-anta">
      <div className="mb-6 mt-4">
        <img src="/src/assets/logo.svg" alt="App Logo" className="w-full" />
      </div>
      
      <nav className="flex flex-col gap-2" aria-label="Main navigation">
        {routes.map(({ name, path, icon }) => (
          <NavLink
            key={path}
            to={path}
            className={({ isActive }) =>
              `${
                isActive 
                  ? 'sidebar-list-active' 
                  : 'sidebar-list'
              }`
            }
          >
            <img src={icon} className="w-icon h-icon flex-shrink-0" />
            <span>{name}</span>
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}