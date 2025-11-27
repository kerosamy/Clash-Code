import type { ComponentType } from 'react';
import { Navigate } from 'react-router-dom';
import Profile from '../pages/Profile';
import Friends from '../pages/Friends';
import Practice from '../pages/Practice';
import Problem from '../pages/Problem';  // Import the Problem wrapper component
import PlayGame from '../pages/PlayGame';
import LeaderBoard from '../pages/LeaderBoard';
import AddProblem from '../pages/AddProblem';
import Settings from '../pages/Settings';
import SignUp from '../pages/SignUp';
import LogIn from '../pages/LogIn';
import NotFound from '../pages/NotFound';
import CompleteRegistration from '../pages/CompleteRegistration';
import OAuthCallback from '../features/OAuthCallback';
import ProblemDetails from '../pages/problem/ProblemDetails';
import Submit from '../pages/problem/Submit';

// Profile sub-pages
import ProfileOverview from '../pages/profile/ProfileOverview';
import Submissions from '../pages/profile/Submissions';
import Matches from '../pages/profile/Matches';

// Friends sub-pages
import MyFriends from '../pages/friends/MyFriends';
import Requested from '../pages/friends/Requested';
import Pending from '../pages/friends/Pending';
import AddFriend from '../pages/friends/AddFriend';

// Icons
import ProfileIcon from '../assets/icons/profile.svg';
import FriendsIcon from '../assets/icons/friends.svg';
import PracticeIcon from '../assets/icons/practice.svg';
import SwordIcon from '../assets/icons/sword.svg';
import LeaderboardIcon from '../assets/icons/leaderboard.svg';
import AddProblemIcon from '../assets/icons/add-problem.svg';
import SettingsIcon from '../assets/icons/settings.svg';
import LogoutIcon from '../assets/icons/logout.svg';
import ListIcon from '../assets/icons/list.svg';
import ScoreIcon from '../assets/icons/scoreboard.svg';
import friends from '../assets/icons/friends.svg';
import addUser from '../assets/icons/add-user.svg'; 
import ProblemDetailsIcon from '../assets/icons/problem-statement.svg';
import SubmitsIcon from '../assets/icons/subimt.svg';

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
];

export interface ChildRouteConfig {
  path?: string;
  index?: boolean;
  component: ComponentType;
}

export interface RouteConfig {
  path: string;
  name: string;
  icon: string;
  component: ComponentType;
  children?: ChildRouteConfig[];
}

export const routes: RouteConfig[] = [
  {
    path: 'profile/:id',
    name: 'Profile',
    icon: ProfileIcon,
    component: Profile,
    children: [
      { index: true, component: () => <Navigate to="overview" replace /> },
      { path: 'overview', component: ProfileOverview },
      { path: 'submissions', component: Submissions },
      { path: 'matches', component: Matches }
    ]
  },
  { path: 'friends', 
    name: 'Friends', 
    icon: FriendsIcon, 
    component: Friends,
    children: [
      { index: true, component: () => <Navigate to="my-friends" replace /> },
      { path: 'my-friends', component: MyFriends }, 
      { path: 'requested', component: Requested },
      { path: 'pending', component: Pending },
      { path: 'add-friend', component: AddFriend }
    ]
   },

  { path: 'practice', name: 'Practice', icon: PracticeIcon, component: Practice },
  {
    path: '/practice/problem/:id',
    name: 'Problem',
    icon: ProblemDetailsIcon,
    component: Problem,
    children: [
      { index: true, component: ProblemDetails },
      { path: 'submit', component: Submit }
    ]
  },
  { path: 'play-game', name: 'Game Play', icon: SwordIcon, component: PlayGame },
  { path: 'leader-board', name: 'LeaderBoard', icon: LeaderboardIcon, component: LeaderBoard },
  { path: 'add-problem', name: 'Add Problem', icon: AddProblemIcon, component: AddProblem },
  { path: 'settings', name: 'Settings', icon: SettingsIcon, component: Settings },
  { path: 'log-out', name: 'Log Out', icon: LogoutIcon, component: Settings },
];

// Used by TopNavigator to render tabs
export const profileSubRoutes: RouteConfig[] = [
  { path: 'overview', name: 'Overview', icon: ProfileIcon, component: ProfileOverview },
  { path: 'submissions', name: 'Submissions', icon: ListIcon, component: Submissions },
  { path: 'matches', name: 'Matches', icon: ScoreIcon, component: Matches },
];

export const friendsSubRoutes: RouteConfig[] = [
  { path: 'my-friends', name: 'My Friends', icon: friends, component: MyFriends },
  { path: 'requested', name: 'Requested', icon: friends, component: Requested },
  { path: 'pending', name: 'Pending', icon: friends, component: Pending },
  { path: 'add-friend', name: 'Add Friend', icon: addUser, component: AddFriend },
];
export const ProblemSubRoutes: RouteConfig[] = [
  { path: '', name: 'Problem Statment', icon: ProblemDetailsIcon, component: ProblemDetails },
  { path: 'submit', name: 'Submit Solution', icon: SubmitsIcon, component: Submit },
];