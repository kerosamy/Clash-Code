import { useState } from 'react';
import { Camera, UserPlus, Upload } from 'lucide-react';

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
    onImageUpdated?: (newImageUrl: string) => void;
}

export default function ProfileHeader({
    profile,
    isPrivate,
    color,
    onAddFriend,
    onImageUpdated,
}: ProfileHeaderProps) {
    const [uploading, setUploading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [preview, setPreview] = useState<string | null>(null);

    const handleFileSelect = async (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        if (!file) return;

        // Validate file type
        if (!file.type.startsWith('image/')) {
            setError('Please select an image file');
            setTimeout(() => setError(null), 3000);
            return;
        }

        // Validate file size (5MB)
        if (file.size > 10 * 1024 * 1024) {
            setError('Image must be less than 10MB');
            setTimeout(() => setError(null), 3000);
            return;
        }

        setError(null);

        // Show preview
        const reader = new FileReader();
        reader.onloadend = () => {
            setPreview(reader.result as string);
        };
        reader.readAsDataURL(file);

        // Upload to server
        await uploadImage(file);
    };

    const uploadImage = async (file: File) => {
        setUploading(true);
        setError(null);

        try {
            const formData = new FormData();
            formData.append('file', file);

            // Adjust the authorization header based on your auth implementation
            const token = localStorage.getItem('token'); // or however you store your token
            
            const response = await fetch('http://localhost:8080/users/profile/image', {
                method: 'POST',
                headers: {
                    ...(token && { 'Authorization': `Bearer ${token}` })
                },
                body: formData
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || 'Failed to upload image');
            }

            const data = await response.json();
            setPreview(null);

            // Notify parent component of the new image URL
            if (onImageUpdated) {
                onImageUpdated(data.imageUrl);
            }


        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to upload image');
            setPreview(null);
            setTimeout(() => setError(null), 3000);
        } finally {
            setUploading(false);
        }
    };


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
                    <div className="w-80 h-80 rounded-full overflow-hidden border-4 relative" style={{ borderColor: color }}>
                        <img
                            src={preview || profile.avatarUrl || '/default-avatar.png'}
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
                                        uploading ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'
                                    }`}
                                    style={{ border: `3px solid ${color}` }}
                                    aria-label="Change profile image"
                                >
                                    <Camera className="w-8 h-8" style={{ color }} />
                                </label>


                            </div>
                        </>
                    ) : (
                        <button
                            onClick={onAddFriend}
                            className="absolute top-5 right-5 bg-background rounded-full p-4 hover:bg-gray-700 transition-colors"
                            style={{ border: `3px solid ${color}` }}
                            aria-label="Add friend"
                        >
                            <UserPlus className="w-6 h-6" style={{ color }} />
                        </button>
                    )}

                    {error && (
                        <div className="absolute -bottom-10 left-0 right-0 bg-red-500 bg-opacity-90 text-white text-sm px-4 py-2 rounded text-center">
                            {error}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}