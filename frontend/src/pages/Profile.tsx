import { Outlet } from 'react-router-dom';
import { profileSubRoutes } from '../routes/routes.config';
import TopNavigator from '../components/common/TopNavigators';

export default function Profile() {
  return (
    <div className="flex flex-col min-h-screen font-anta">
      <TopNavigator navigators={profileSubRoutes} />
      <Outlet />
    </div>
  );
}