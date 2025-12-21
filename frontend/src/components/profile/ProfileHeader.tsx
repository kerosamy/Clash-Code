import { useState, useEffect } from 'react';
import { Camera, UserPlus, Upload } from 'lucide-react';
import { uploadProfileImage } from "../../services/UserService";
import { 
    fetchFriendStatus, 
    sendFriendRequest, 
    acceptFriendRequest, 
    rejectFriendRequest, 
    type FriendStatus, 
} from "../../services/FriendService";

interface UserProfile {
    username: string;
    rank: string;
    currentRate: number;
    maxRate: number;
    friendCount: number;
    avatarUrl: string;
}

interface ProfileHeaderProps {
    profile: UserProfile;
    setProfile: React.Dispatch<React.SetStateAction<UserProfile | null>>;
    isPrivate: boolean;
    color: string;
    onImageUpdated?: (newImageUrl: string) => void;
}

export default function ProfileHeader({
    profile,
    setProfile,
    isPrivate,
    color,
    onImageUpdated,
}: ProfileHeaderProps) {
    const [uploading, setUploading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [preview, setPreview] = useState<string | null>(null);
    const [friendStatus, setFriendStatus] = useState<FriendStatus>("NONE");

    const handleFileSelect = async (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        if (!file) return;

        if (!file.type.startsWith('image/')) {
            setError('Please select an image file');
            setTimeout(() => setError(null), 3000);
            return;
        }

        if (file.size > 10 * 1024 * 1024) {
            setError('Image must be less than 10MB');
            setTimeout(() => setError(null), 3000);
            return;
        }

        setError(null);

        const reader = new FileReader();
        reader.onloadend = () => {
            setPreview(reader.result as string);
        };
        reader.readAsDataURL(file);

        await uploadImage(file);
    };

    const uploadImage = async (file: File) => {
        setUploading(true);
        setError(null);

        try {
            const response = await uploadProfileImage(file);
            setPreview(null);

            if (onImageUpdated) {
                onImageUpdated(response.imageUrl);
            }
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to upload image');
            setPreview(null);
            setTimeout(() => setError(null), 3000);
        } finally {
            setUploading(false);
        }
    };

    useEffect(() => {
        const loadStatus = async () => {
            try {
                const status = await fetchFriendStatus(profile.username);
                setFriendStatus(status);
            } catch {
                setFriendStatus("NONE");
            }
        };
        loadStatus();
    }, [profile.username]);

    const handleAddFriend = async () => {
        try {
            await sendFriendRequest(profile.username);
            setFriendStatus("PENDING_SENT");
        } catch (err) {
            setError(err instanceof Error ? err.message : "Failed to send friend request");
            setTimeout(() => setError(null), 3000);
        }
    };

    const handleAcceptFriend = async () => {
        try {
            await acceptFriendRequest(profile.username);
            setFriendStatus("FRIENDS");
            setProfile(prev => prev ? { ...prev, friendCount: prev.friendCount + 1 } : prev );
        } catch (err) {
            setError(err instanceof Error ? err.message : "Failed to accept friend request");
            setTimeout(() => setError(null), 3000);
        }
    };

    const handleRejectFriend = async () => {
        try {
            await rejectFriendRequest(profile.username);
            setFriendStatus("NONE");
        } catch (err) {
            setError(err instanceof Error ? err.message : "Failed to reject friend request");
            setTimeout(() => setError(null), 3000);
        }
    };

    return (
  <div className="bg-container rounded-lg p-8 mb-8 relative">
    <div className="flex justify-between items-start">
      <div className="flex-1">
        <div className="text-xl mb-2" style={{ color }}>{profile.rank}</div>
        <h1 className="text-3xl font-bold mb-6" style={{ color }}>
          {profile.username}
        </h1>

        <div className="space-y-2 text-text">
          <div className="flex items-center gap-2">
            <span className="text-xl text-text">Current Rate :</span>
            <span className="text-2xl font-semibold" style={{ color }}>
              {profile.currentRate}
            </span>
          </div>
          <div className="flex items-center gap-2">
            <span className="text-xl text-text">Max Rate :</span>
            <span className="text-2xl font-semibold" style={{ color }}>
              {profile.maxRate}
            </span>
          </div>
          <div className="text-text text-xl">
            Friend of : <span className="text-text">{profile.friendCount} users</span>
          </div>
        </div>
      </div>

      <div className="relative">
        <div
          className="w-80 h-80 rounded-full overflow-hidden border-4 relative"
          style={{ borderColor: color }}
        >
          <img
            src={preview || profile.avatarUrl || "/default-avatar.png"}
            alt={profile.username}
            className="w-full h-full object-cover"
          />
          {uploading && (
            <div className="absolute inset-0 bg-black bg-opacity-60 flex items-center justify-center">
              <div className="flex flex-col items-center gap-2">
                <Upload className="w-8 h-8 text-white animate-bounce" />
                <span className="text-white font-medium">Uploading...</span>
              </div>
            </div>
          )}
        </div>

        {isPrivate ? (
          <>
            <input
              type="file"
              id="profile-image-input"
              accept="image/*"
              onChange={handleFileSelect}
              className="hidden"
              disabled={uploading}
            />

            <div className="absolute top-5 right-5 flex gap-2">
              <label
                htmlFor="profile-image-input"
                className={`bg-background rounded-full p-3 hover:bg-gray-700 transition-colors ${
                  uploading ? "opacity-50 cursor-not-allowed" : "cursor-pointer"
                }`}
                style={{ border: `3px solid ${color}` }}
                aria-label="Change profile image"
              >
                <Camera className="w-8 h-8" style={{ color }} />
              </label>
            </div>
          </>
        ) : (
          friendStatus === "NONE" && (
            <button
              onClick={handleAddFriend}
              className="absolute top-5 right-5 bg-background rounded-full p-4 hover:bg-gray-700 transition-colors"
              style={{ border: `3px solid ${color}` }}
              aria-label="Add friend"
            >
              <UserPlus className="w-6 h-6" style={{ color }} />
            </button>
          )
        )}

        {error && (
          <div className="absolute -bottom-10 left-0 right-0 bg-red-500 bg-opacity-90 text-white text-sm px-4 py-2 rounded text-center">
            {error}
          </div>
        )}
      </div>
    </div>

    {!isPrivate && (
      <div className="absolute bottom-9 left-8">
        {friendStatus === "PENDING_SENT" && (
          <span
            className="px-4 py-2 rounded-full text-lg font-medium"
            style={{ border: `2px solid ${color}`, color }}
          >
            Request Pending
          </span>
        )}

        {friendStatus === "FRIENDS" && (
          <span
            className="px-4 py-2 rounded-full text-lg font-medium"
            style={{ border: `2px solid ${color}`, color }}
          >
            Friends
          </span>
        )}

        {friendStatus === "PENDING_RECEIVED" && (
            <div className="flex gap-2">
              <button
                onClick={handleAcceptFriend}
                className="
                  flex items-center justify-center
                  border border-emerald-500/30 bg-emerald-500/5 text-emerald-400
                  hover:bg-emerald-500 hover:text-white hover:border-emerald-500 hover:shadow-[0_0_10px_rgba(16,185,129,0.3)]
                  px-4 py-1 rounded-full 
                  font-anta text-lg uppercase tracking-widest 
                  transition-all duration-300
                "
              >
                Accept
              </button>
              <button
                onClick={handleRejectFriend}
                className="
                  flex items-center justify-center
                  border border-rose-500/30 bg-rose-500/5 text-rose-400
                  hover:bg-rose-600 hover:text-white hover:border-rose-600 hover:shadow-[0_0_10px_rgba(225,29,72,0.3)]
                  px-4 py-1 rounded-full 
                  font-anta text-lg uppercase tracking-widest 
                  transition-all duration-300
                "
              >
                Reject
              </button>
          </div>
        )}
      </div>
    )}
  </div>
);

}