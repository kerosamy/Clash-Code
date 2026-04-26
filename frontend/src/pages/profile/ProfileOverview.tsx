import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import Categories from "../../components/profile/Categories";
import ProfileHeader from "../../components/profile/ProfileHeader";
import StatsOverview from "../../components/profile/StatsOverview";
import LogoLoader from "../../components/Loader/LogoLoader";
import { waitForLoader } from "../../components/Loader/WaitLoader";
import { fetchMyProfile, fetchUserProfile, splitUserData } from "../../services/UserService";
import type { UserProfileBasic, UserStats, CategoryItem } from "../../services/UserService";
import { rankColors } from "../../utils/colorMapper";
import { getUsername } from "../../utils/jwtDecoder";

export default function ProfileOverview() {
  const [profileBasic, setProfileBasic] = useState<UserProfileBasic | null>(null);
  const [stats, setStats] = useState<UserStats | null>(null);
  const [categories, setCategories] = useState<CategoryItem[]>([]);
  const [loading, setLoading] = useState(true);
  const { username: paramUsername } = useParams<{ username?: string }>();
  const loggedInUsername = getUsername();
  const username = paramUsername ?? loggedInUsername!;

  useEffect(() => {
    const fetchProfile = async () => {
      const startTime = Date.now();
      try {
        const data =
          username === loggedInUsername
            ? await fetchMyProfile()
            : await fetchUserProfile(username);

        await waitForLoader(startTime);

        const { profileBasic, stats, categories } = splitUserData(data);
        setProfileBasic(profileBasic);
        setStats(stats);
        setCategories(categories);
        console.log("here is the data");
        console.log(profileBasic);

      } catch (error) {
        console.error("Failed to fetch profile:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, [username, loggedInUsername]);

  const handleImageUpdated = (newImageUrl: string) => {
    // Update the profile basic data with the new image URL
    setProfileBasic(prev => {
      if (!prev) return prev;
      return {
        ...prev,
        avatarUrl: newImageUrl
      };
    });
  };

  if (loading) {
    return <LogoLoader loadingMessage="Loading Profile"/>;
  }

  if (!profileBasic || !stats) {
    return (
      <div className="p-8 text-center text-3xl text-orange">
        Failed to load profile data.
      </div>
    );
  }

  return (
    <div className="space-y-8 p-scroll-x">
      <ProfileHeader
        profile={profileBasic}
        setProfile={setProfileBasic}
        isPrivate={username === loggedInUsername}
        color={rankColors[profileBasic.rank]}
        onImageUpdated={handleImageUpdated}
      />
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <StatsOverview stats={stats} color={rankColors[profileBasic.rank]} />
        <Categories categories={categories} color={rankColors[profileBasic.rank]} />
      </div>
    </div>
  );
}