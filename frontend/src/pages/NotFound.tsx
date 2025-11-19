import { NavLink } from 'react-router-dom';

export default function NotFound() {
  return (
    <div className="flex flex-col items-center justify-center h-full min-h-[500px]">
      <h1 className="text-9xl font-bold text-gray-300 mb-4">404</h1>
      <h2 className="text-3xl font-semibold text-gray-700 mb-4">Page Not Found</h2>
      <p className="text-gray-500 mb-8 text-center max-w-md">
        The page you're looking for doesn't exist or has been moved.
      </p>
      <NavLink 
        to="/profile" 
        className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-semibold"
      >
        Go to Profile
      </NavLink>
    </div>
  );
}