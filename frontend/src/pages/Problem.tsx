import { Outlet } from 'react-router-dom';
import { ProblemSubRoutes } from '../routes/routes.config';
import TopNavigator from '../components/common/TopNavigators';

export default function Problem() {
  return (
    <div className="flex flex-col h-screen font-anta">
      <TopNavigator navigators={ProblemSubRoutes} />
      <div className="flex flex-1 flex-col overflow-y-auto custom-scroll">
        <Outlet />
      </div>
    </div>
  );
}