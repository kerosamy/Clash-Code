import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import Categories from "../../components/profile/Categories";
import ProfileHeader from "../../components/profile/ProfileHeader";
import StatsOverview from "../../components/profile/StatsOverview";
import api from "../../services/api";
  const token = localStorage.getItem("token");
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
  const { id } = useParams<{ id: string }>();
  const [profileBasic, setProfileBasic] = useState<UserProfileBasic | null>(null);
  const [stats, setStats] = useState<UserStats | null>(null);
  const [categories, setCategories] = useState<CategoryItem[]>([]);
  const [loading, setLoading] = useState(true);

 useEffect(() => {
  const fetchProfile = async () => {
    try {

      // FIX: Stop if there is no token
      if (!token) {
        console.error("No token found. Please login first.");
        setLoading(false);
        return;
      }

      const { data } = await api.get<BackendUserResponse>(
        `/users/profile`
      );

      const { profileBasic, stats, categories } = splitUserData(data);
      setProfileBasic(profileBasic);
      setStats(stats);
      setCategories(categories);
    } catch (error) {
      console.error("Failed to fetch profile:", error);
    } finally {
      setLoading(false);
    }
  };

  fetchProfile();
}, [id]);


  if (loading) {
    return <div className="p-8 text-center text-3xl text-gray-500">Loading profile...</div>;
  }

  if (!profileBasic || !stats) {
    return <div className="p-8 text-center text-3xl text-orange">Failed to load profile data.</div>;
  }

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