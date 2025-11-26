import { Camera, UserPlus } from 'lucide-react';

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
    isPrivate: boolean;
    color: string;
    onAddFriend?: () => void;
    onChangeImage?: () => void;
}

export default function ProfileHeader({
    profile,
    isPrivate,
    color,
    onAddFriend,
    onChangeImage,
}: ProfileHeaderProps) {
    return (
        <div className="bg-container rounded-lg p-8 mb-8">
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
                    <div className="w-80 h-80 rounded-full overflow-hidden border-4" style={{ borderColor: color }}>
                        <img
                            src={profile.avatarUrl}
                            alt={profile.username}
                            className="w-full h-full object-cover"
                        />
                    </div>
                    {isPrivate ? (
                        <button
                            onClick={onChangeImage}
                            className="absolute top-5 right-5 bg-background rounded-full p-3 hover:bg-gray-700 transition-colors"
                            aria-label="Change profile image"
                        >
                            <Camera className="w-8 h-8" style={{ color }} />
                        </button>
                    ) : (
                        <button
                            onClick={onAddFriend}
                            className="absolute top-2 right-2 bg-gray-900 rounded-full p-2 hover:bg-gray-700 transition-colors"
                            aria-label="Add friend"
                        >
                            <UserPlus className="w-6 h-6" style={{ color }} />
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
}