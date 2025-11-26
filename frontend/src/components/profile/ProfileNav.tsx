import TopNavigator from '../common/TopNavigators';
import { profileSubRoutes } from '../../routes/routes.config';

export default function ProfileNav() {
    const navigationItems = profileSubRoutes.map((route) => ({
        name: route.name,
        path: route.path === '' ? '/profile' : `/profile/${route.path}`,
        icon: route.icon,
    }));

    return <TopNavigator navigators={navigationItems} />
}