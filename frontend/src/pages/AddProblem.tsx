import { Outlet } from 'react-router-dom';
import { SuggestProblemSubRoutes } from '../routes/routes.config';
import TopNavigator from '../components/common/TopNavigators';

export default function AddProblem() {
  return (
     <div className="flex flex-col h-screen font-anta">
      <TopNavigator navigators={SuggestProblemSubRoutes} />
       <div className="flex-1 overflow-y-auto custom-scroll">
        <Outlet />
      </div>
    </div>
  );
}