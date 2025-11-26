import { NavLink } from 'react-router-dom';

interface NavigatorItem {
  name: string;
  path: string;
  icon: string;
}

interface TopNavigatorProps {
  navigators: NavigatorItem[];
  className?: string;
}

export default function TopNavigator({ navigators }: TopNavigatorProps) {
  return (
    <nav 
      className={"bg-slate-800 border-b border-slate-700"}
    >
      <div className="flex items-center justify-center gap-8 px-8 h-16">
        {navigators.map(({ name, path, icon }) => (
          <NavLink
            key={path}
            to={path}
            className={({ isActive }) =>
              `flex items-center gap-2 px-6 py-2 rounded-lg transition-all relative
              ${isActive 
                ? 'bg-slate-700 text-white' 
                : 'text-slate-400 hover:text-white hover:bg-slate-700/50'
              }`
            }
          >
            <img src={icon} className="w-icon h-icon flex-shrink-0" />
            <span className="font-medium">{name}</span>
          </NavLink>
        ))}
      </div>
    </nav>
  );
}