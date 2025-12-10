import { Outlet } from 'react-router-dom';
import { profileSubRoutes } from '../routes/routes.config';
import TopNavigator from '../components/common/TopNavigators';

export default function Profile() {
  return (
    <div className="flex flex-col h-screen font-anta">
      <TopNavigator navigators={profileSubRoutes} />
      <div className="flex-1 overflow-y-auto custom-scroll">
        <Outlet />
      </div>
    </div>
  );
}