import Categories from "../../components/profile/Categories";
import ProfileHeader from "../../components/profile/ProfileHeader";
import StatsOverview from "../../components/profile/StatsOverview";

// Backend Response Interface
interface BackendUserResponse {
  username: string;
  rank: string;
  currentRate: number;
  maxRate: number;
  friendCount: number;
  avatarUrl: string;
  stats: {
    solvedProblems: number;
    attemptedProblems: number;
    matchesPlayed: number;
    matchesWon: number;
  };
  categories: {
    name: string;
    value: number;
    color: string;
  }[];
}

// Split Interfaces for different components
interface UserProfileBasic {
  username: string;
  rank: string;
  currentRate: number;
  maxRate: number;
  friendCount: number;
  avatarUrl: string;
}

interface UserStats {
  solvedProblems: number;
  attemptedProblems: number;
  matchesPlayed: number;
  matchesWon: number;
}

interface CategoryItem {
  name: string;
  value: number;
  color: string;
}

// Dummy backend response
const backendResponse: BackendUserResponse = {
  username: 'Kero_Samy_20',
  rank: 'Diamond',
  currentRate: 1604,
  maxRate: 1654,
  friendCount: 20,
  avatarUrl: 'https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=400&h=400&fit=crop',
  stats: {
    solvedProblems: 750,
    attemptedProblems: 850,
    matchesPlayed: 56,
    matchesWon: 37,
  },
  categories: [
    { name: 'Greedy', value: 20, color: '#ef4444' },        // red-500
    { name: 'Two Pointers', value: 20, color: '#f97316' },  // orange-500
    { name: 'DP', value: 20, color: '#22d3ee' },            // cyan-400
    { name: 'Graph', value: 20, color: '#3b82f6' },         // blue-500
    { name: 'Tree', value: 20, color: '#8b5cf6' },          // violet-500
    { name: 'Math', value: 20, color: '#10b981' },          // green-500
    { name: 'String', value: 20, color: '#ec4899' },        // pink-500
    { name: 'Binary Search', value: 20, color: '#6366f1' }, // indigo-500
    { name: 'Sorting', value: 20, color: '#f59e0b' },       // amber-500
    { name: 'Bit Manipulation', value: 20, color: '#14b8a6' }, // teal-500
  ],
};


// Helper function to split the data
const splitUserData = (response: BackendUserResponse) => {
  const profileBasic: UserProfileBasic = {
    username: response.username,
    rank: response.rank,
    currentRate: response.currentRate,
    maxRate: response.maxRate,
    friendCount: response.friendCount,
    avatarUrl: response.avatarUrl,
  };

  const stats: UserStats = response.stats;
  
  const categories: CategoryItem[] = response.categories;

  return { profileBasic, stats, categories };
};

export default function ProfileOverview() {
  // Split the backend data
  const { profileBasic, stats, categories } = splitUserData(backendResponse);

  return (
    <div className="space-y-8 p-scroll-x">
      <ProfileHeader profile={profileBasic} isPrivate={true} color="#00FFFF" />
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <StatsOverview stats={stats} color="#00FFFF" />
        <Categories categories={categories} color="#00FFFF" />
      </div>
    </div>
  );
}