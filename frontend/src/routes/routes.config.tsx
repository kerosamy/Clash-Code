// Central configuration for all routes
import type { ComponentType } from 'react';
import Profile from '../pages/Profile';
import Friends from '../pages/Friends';
import Practice from '../pages/Practice';
import PlayGame from '../pages/PlayGame';
import LeaderBoard from '../pages/LeaderBoard';
import AddProblem from '../pages/AddProblem';
import Settings from '../pages/Settings';
import SignUp from '../pages/SignUp';
import LogIn from '../pages/LogIn';
import NotFound from '../pages/NotFound';
import CompleteRegistration from '../pages/CompleteRegistration';
import OAuthCallback from '../features/OAuthCallback';

// Profile sub-pages
import ProfileOverview from '../pages/profile/ProfileOverview';
import Submissions from '../pages/profile/Submissions';
import Matches from '../pages/profile/Matches';

// Import SVG icons from assets
import ProfileIcon from '../assets/icons/profile.svg';
import FriendsIcon from '../assets/icons/friends.svg';
import PracticeIcon from '../assets/icons/practice.svg';
import SwordIcon from '../assets/icons/sword.svg';
import LeaderboardIcon from '../assets/icons/leaderboard.svg';
import AddProblemIcon from '../assets/icons/add-problem.svg';
import SettingsIcon from '../assets/icons/settings.svg';
import LogoutIcon from '../assets/icons/logout.svg';

//Non-sidebar pages
export interface PageConfig {
  path: string;
  component: ComponentType;
}

export const pages: PageConfig[] = [
{ path: '/sign-up', component: SignUp },
{ path: '/not-found', component: NotFound },
{ path: '/log-in', component: LogIn },
{ path: '/auth/callback', component: OAuthCallback },
{ path: '/complete-registration', component: CompleteRegistration },
{ path: '/auth/callback', component: OAuthCallback }, 
{ path: '/complete-registration', component: CompleteRegistration },
];


//Sidebar pages
export interface RouteConfig {
  path: string;
  name: string;
  icon: string;
  component: ComponentType;
}

export const routes: RouteConfig[] = [
    { path: 'profile', name: 'Profile', icon: ProfileIcon, component: Profile },
    { path: 'friends', name: 'Friends', icon: FriendsIcon, component: Friends },
    { path: 'practice', name: 'Practice', icon: PracticeIcon, component: Practice },
    { path: 'play-game', name: 'Game Play', icon: SwordIcon, component: PlayGame },
    { path: 'leader-board', name: 'LeaderBoard', icon: LeaderboardIcon, component: LeaderBoard },
    { path: 'add-problem', name: 'Add Problem', icon: AddProblemIcon, component: AddProblem },
    { path: 'settings', name: 'Settings', icon: SettingsIcon, component: Settings },
    { path: 'log-out', name: 'Log Out', icon: LogoutIcon, component: Settings },
];