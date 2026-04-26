import { Outlet, useParams, Navigate } from 'react-router-dom';
import TopNavigator from '../components/common/TopNavigators';
import { profileSubRoutes } from '../routes/routes.config';
import { getUsername } from '../utils/jwtDecoder';

export default function Profile() {
  const { username } = useParams();
  const currentUser = getUsername();

  if (username !== currentUser) {
    const overviewPath = `/profile/${username}/overview`;
    return (
      <div className="flex flex-col h-screen font-anta">
        <Navigate to={overviewPath} replace />
        <div className="flex-1 flex flex-col overflow-y-auto custom-scroll">
          <Outlet />
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col h-screen font-anta">
      <TopNavigator navigators={profileSubRoutes} />
      <div className="flex-1 flex flex-col overflow-y-auto custom-scroll">
        <Outlet />
      </div>
    </div>
  );
}