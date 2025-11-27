import { Outlet } from 'react-router-dom';
import { ProblemSubRoutes } from '../routes/routes.config';
import TopNavigator from '../components/common/TopNavigators';

export default function Problem() {
  return (
    <div className="flex flex-col min-h-screen font-anta">
      <TopNavigator navigators={ProblemSubRoutes} />
      <Outlet />
    </div>
  );
}