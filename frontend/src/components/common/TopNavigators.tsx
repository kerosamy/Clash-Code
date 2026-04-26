import { NavLink } from 'react-router-dom';

interface NavigatorItem {
  name: string;
  path: string;
  icon: string;
}

interface TopNavigatorProps {
  navigators: NavigatorItem[];
}

export default function TopNavigator({ navigators }: TopNavigatorProps) {
  return (
    <nav className="w-full pt-5">
      <div className="flex items-center justify-evenly px-8 h-16">
        {navigators.map(({ name, path, icon }) => (
          <NavLink
            key={path || 'index'}
            to={path}
            end={path === ''}
            className={({ isActive }) =>
              ` flex items-center justify-center gap-3
                px-6 py-3
                rounded-full
                transition
                font-anta text-white
              ${isActive 
                ? 'bg-white/15'
                : 'bg-transparent hover:bg-white/10'
              }`
            }
          >
            <img src={icon} className="w-icon h-icon flex-shrink-0" alt={name} />
            <span className="font-medium text-xl">{name}</span>
          </NavLink>
        ))}
      </div>
    </nav>
  );
}