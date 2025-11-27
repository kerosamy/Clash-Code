import { Outlet } from 'react-router-dom';
import { friendsSubRoutes } from '../routes/routes.config';
import TopNavigator from '../components/common/TopNavigators';

export default function Friends() {
  return (
    <div className="flex flex-col min-h-screen font-anta">
      <TopNavigator navigators={friendsSubRoutes} />
      <Outlet />
    </div>
  );
}