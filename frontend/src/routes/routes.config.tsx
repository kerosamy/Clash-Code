import type { ComponentType } from 'react';
import { Navigate } from 'react-router-dom';
import Profile from '../pages/Profile';
import Friends from '../pages/Friends';
import Practice from '../pages/Practice';
import Problem from '../pages/Problem';  // Import the Problem wrapper component
import PlayGameHome from "../pages/play-game/playGameHome";
import PlayGameMatch from  "../pages/play-game/playGameMatch";
import LeaderBoard from '../pages/LeaderBoard';
import AddProblem from '../pages/AddProblem';
import SignUp from '../pages/SignUp';
import LogIn from '../pages/LogIn';
import NotFound from '../pages/NotFound';
import CompleteRegistration from '../pages/CompleteRegistration';
import OAuthCallback from '../features/OAuthCallback';
import ProblemDetails from '../pages/problem/ProblemDetails';
import Submit from '../pages/problem/Submit';
import LogOut from '../pages/LogOut';
import Notifications from '../pages/Notifications';
import PasswordRecovery from '../pages/PasswordRecovery';

// Profile sub-pages
import ProfileOverview from '../pages/profile/ProfileOverview';
import Submissions from '../pages/profile/Submissions';
import Matches from '../pages/profile/Matches';

// Friends sub-pages
import MyFriends from '../pages/friends/MyFriends';
import Requested from '../pages/friends/Requested';
import Pending from '../pages/friends/Pending';
import Users from '../pages/friends/Users';

// Suggest Problem sub-pages
import ProblemInfo from '../pages/suggestProblem/ProblemInfo';
import ProblemStatment from '../pages/suggestProblem/ProblemStatment';
import TestCases from '../pages/suggestProblem/TestCases';
import MySuggestions from '../pages/suggestProblem/MySuggestions';

// match sub-pages
import MatchState from '../pages/match/MatchState' 

// Icons
import ProfileIcon from '../assets/icons/profile.svg';
import FriendsIcon from '../assets/icons/friends.svg';
import PracticeIcon from '../assets/icons/practice.svg';
import SwordIcon from '../assets/icons/sword.svg';
import LeaderboardIcon from '../assets/icons/leaderboard.svg';
import AddProblemIcon from '../assets/icons/add-problem.svg';
import LogoutIcon from '../assets/icons/logout.svg';
import ListIcon from '../assets/icons/list.svg';
import ScoreIcon from '../assets/icons/scoreboard.svg';
import friends from '../assets/icons/friends.svg';
import addUser from '../assets/icons/add-user.svg'; 
import ProblemDetailsIcon from '../assets/icons/problem-statement.svg';
import SubmitsIcon from '../assets/icons/subimt.svg';
import { UserRole } from '../enums/UserRole';
import UserManagement from '../pages/UserManagement';
import ReviewProblems from '../pages/review/ReviewProblems';
import WriteProblemStatmentIcon from '../assets/icons/writeProblemStatmentIcon.svg';
import ProblemInfoIcon from '../assets/icons/problemInfoIcon.svg';
import TestCasesIcon from '../assets/icons/testCasesIcon.svg';
import Suggestions from '../assets/icons/Suggestions.svg';
import NotificationsIcon from '../assets/icons/Bell.png';
import ReviewProblem from '../pages/review/ReviewProblem';



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
  { path: '/profile/:username/*', component: Profile, },
  { path: '/password-recovery', component: PasswordRecovery },
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
  requiredRoles?: UserRole[]; // field for rbac
  hideFromNav?: boolean; // hide from navigation menu
}


export const routes: RouteConfig[] = [
  {
    path: 'profile/:username',
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
  {
    path: 'friends',
    name: 'Friends',
    icon: FriendsIcon,
    component: Friends,
    children: [
      { index: true, component: () => <Navigate to="my-friends" replace /> },
      { path: 'my-friends', component: MyFriends },
      { path: 'requested', component: Requested },
      { path: 'pending', component: Pending },
      { path: 'users', component: Users }
    ]
  },

  { path: 'practice', 
    name: 'Practice', 
    icon: PracticeIcon, 
    component: Practice 
  },

  {
    path: '/practice/problem/:id',
    name: 'Problem',
    icon: ProblemDetailsIcon,
    component: Problem,
    hideFromNav: true,
    children: [
      { index: true, component: ProblemDetails },
      { path: 'submit', component: Submit }
    ]
  },
  { 
    path: 'play-game', 
    name: 'Play Game', 
    icon: SwordIcon, 
    component: PlayGameHome, 
    hideFromNav: false
  },
  { 
    path: 'play-game/:id', 
    name: 'Game Match', 
    icon: SwordIcon, 
    component: PlayGameMatch,  
    hideFromNav: true,
    children: [
      { index: true, component: ProblemDetails }, 
      { path: 'submit', component: Submit },
      { path: 'match-state', component: MatchState }
    ]
  },
  
  { path: 'leader-board', name: 'LeaderBoard', icon: LeaderboardIcon, component: LeaderBoard },
  
  {
    path: 'add-problem',
    name: 'Add Problem',
    icon: AddProblemIcon,
    component: AddProblem,
    children: [
      { index: true, component: () => <Navigate to="info" replace /> },
      { path: 'info' , component: ProblemInfo },
      { path: 'statement' , component: ProblemStatment },
      { path: 'test-cases', component: TestCases },
      { path: 'my-suggestions', component: MySuggestions }

    ]
  },
  // admin and super admin 
  {
    path: 'review-problems',
    name: 'Review Problems',
    icon: ListIcon,  
    component: ReviewProblems,
    requiredRoles: [UserRole.ADMIN, UserRole.SUPER_ADMIN]
  },
  {
    path: 'review-problems/:id',
    name: 'Review Problem Detail',
    icon: ListIcon,  
    component: ReviewProblem,
    hideFromNav: true,
    requiredRoles: [UserRole.ADMIN, UserRole.SUPER_ADMIN]
  },
  // super admin only
  { 
    path: 'user-management', 
    name: 'User Management', 
    icon: addUser, 
    component: UserManagement, 
    requiredRoles: [UserRole.SUPER_ADMIN]
  },

  { 
    path: 'notifications', 
    name: 'Notifications', 
    icon: NotificationsIcon, 
    component: Notifications
  },
  { 
    path: 'notifications/:notificationId', 
    name: 'Notification Detail', 
    icon: NotificationsIcon, 
    component: Notifications,
    hideFromNav: true
  },
  { path: 'log-out', name: 'Log Out', icon: LogoutIcon, component: LogOut },


];

// Used by TopNavigator to render tabs
export const profileSubRoutes: RouteConfig[] = [
  { path: 'overview', name: 'Overview', icon: ProfileIcon, component: ProfileOverview },
  { path: 'submissions', name: 'Submissions', icon: ListIcon, component: Submissions },
  { path: 'matches', name: 'Matches', icon: ScoreIcon, component: Matches },
];

export const friendsSubRoutes: RouteConfig[] = [
  { path: 'my-friends', name: 'My Friends', icon: friends, component: MyFriends },
  { path: 'requested', name: 'Requested', icon: addUser, component: Requested },
  { path: 'pending', name: 'Pending', icon: addUser, component: Pending },
  { path: 'users', name: 'Users', icon: friends, component: Users },
];

export const ProblemSubRoutes: RouteConfig[] = [
  { path: '', name: 'Problem Statment', icon: ProblemDetailsIcon, component: ProblemDetails },
  { path: 'submit', name: 'Submit Solution', icon: SubmitsIcon, component: Submit },
];
export const SuggestProblemSubRoutes: RouteConfig[] = [
  {path: 'info', name: 'Problem Info', icon: ProblemInfoIcon, component: ProblemInfo },
  {path: 'statement', name: 'Problem Statment', icon: WriteProblemStatmentIcon, component: ProblemStatment },
  {path: 'test-cases', name: 'Test Cases', icon: TestCasesIcon, component: TestCases },
  {path: 'my-suggestions', name: 'My Suggestions', icon: Suggestions, component: MySuggestions },

];
export const matchSubRoutes: RouteConfig[] = [
  { path: '', name: 'Problem Statment', icon: ProblemDetailsIcon, component: ProblemDetails },
  { path: 'submit', name: 'Submit Solution', icon: SubmitsIcon, component: Submit },
  { path: 'match-state', name: 'Match State', icon: SwordIcon, component: MatchState },
];