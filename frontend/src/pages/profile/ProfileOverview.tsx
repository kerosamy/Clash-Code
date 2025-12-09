import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import Categories from "../../components/profile/Categories";
import ProfileHeader from "../../components/profile/ProfileHeader";
import StatsOverview from "../../components/profile/StatsOverview";
import  { fetchMyProfile, splitUserData } from "../../services/UserService";
import type {
  UserProfileBasic,
  UserStats,
  CategoryItem,
} from "../../services/UserService";
import { rankColors } from "../../utils/colorMapper";

export default function ProfileOverview() {
  const { id } = useParams<{ id: string }>();
  const [profileBasic, setProfileBasic] = useState<UserProfileBasic | null>(null);
  const [stats, setStats] = useState<UserStats | null>(null);
  const [categories, setCategories] = useState<CategoryItem[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const data = await fetchMyProfile();
        const { profileBasic, stats, categories } = splitUserData(data);
        setProfileBasic(profileBasic);
        setStats(stats);
        setCategories(categories);
      } 
      catch (error) {
        console.error("Failed to fetch profile:", error);
      } 
      finally {
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
      <ProfileHeader profile={profileBasic} isPrivate={true} color={rankColors[profileBasic.rank]} />
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <StatsOverview stats={stats} color={rankColors[profileBasic.rank]} />
        <Categories categories={categories} color={rankColors[profileBasic.rank]} />
      </div>
    </div>
  );
}