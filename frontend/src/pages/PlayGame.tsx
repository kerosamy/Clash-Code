import TopNavigator from "../components/common/TopNavigators";
import { matchSubRoutes } from '../routes/routes.config';
import { Outlet } from 'react-router-dom';

export default function PlayGame() {
    return (
        <div className="flex flex-col min-h-screen font-anta">
            <TopNavigator navigators={matchSubRoutes} />
            <Outlet />
        </div>
    );
}

