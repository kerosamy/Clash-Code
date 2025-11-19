import { NavLink } from "react-router-dom";
import { routes } from '../routes/routes.config';


export default function Sidebar() {
  return (
    <aside className="bg-gray-900 text-white w-64 min-h-screen p-4">
      {/* App Logo/Title */}
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-center py-4">My App</h1>
      </div>
      
      {/* Navigation */}
      <nav className="flex flex-col gap-2" aria-label="Main navigation">
        {routes.map(({ name, path, icon }) => (
          <NavLink
            key={path}
            to={path}
            className={({ isActive }) =>
              `flex items-center gap-3 px-4 py-3 rounded-lg transition-colors ${
                isActive 
                  ? 'bg-gray-800 text-white font-semibold' 
                  : 'text-gray-300 hover:bg-gray-800 hover:text-white'
              }`
            }
          >
            <img src={icon} alt="" className="w-6 h-6 flex-shrink-0" />
            <span>{name}</span>
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}