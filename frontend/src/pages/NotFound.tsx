import { NavLink } from 'react-router-dom';

export default function NotFound() {
  return (
    <div className="flex flex-col items-center justify-center h-full font-anta">
      <h1 className="text-9xl font-bold text-text mb-4">404</h1>
      <h2 className="text-3xl font-semibold text-text/50 mb-4">Page Not Found</h2>
      <p className="text-text mb-8 text-center max-w-lg">
        The page you're looking for doesn't exist or has been moved.
      </p>
      <NavLink 
        to="/profile" 
        className="px-6 py-3 bg-orange text-white rounded-lg hover:bg-orange/80 transition-colors"
      >
        Go to Profile
      </NavLink>
    </div>
  );
}