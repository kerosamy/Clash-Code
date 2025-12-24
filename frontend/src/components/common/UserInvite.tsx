import { rankColors } from '../../utils/colorMapper';
import { sendMatchInvite } from '../../services/NotificationService';

export interface UserInviteProps {
    order: number;
    username: string;
    rank: string;
    onInviteClick: (notificationId: number, username: string) => void; // Modified signature
    onUsernameClick?: () => void;
    className?: string;
}

export default function UserInvite({
    order,
    username,
    rank,
    onInviteClick,
    onUsernameClick,
    className = "",
}: UserInviteProps) {

    const handleInviteClick = async () => {
        try {
            // Get the notification ID from the backend
            const notificationId = await sendMatchInvite(username);
            console.log('Match invitation sent, notification ID:', notificationId);
            
            // Pass both the notification ID and username to parent
            onInviteClick(notificationId, username); 
        } catch (error) {
            console.error('Failed to send match invitation:', error);
            alert('Failed to send invitation. Please try again.');
        }
    };

    return (
        <div
            className={`grid grid-cols-[60px_1fr_120px] gap-4 px-6 py-3 items-center hover:bg-sidebar/20 transition-colors ${className}`}
        >
            <span className="text-text text-center font-anta text-sm">
                {order}
            </span>
            
            <button
                onClick={onUsernameClick}
                className="font-anta text-sm font-bold truncate text-left hover:opacity-80 transition-colors"
                style={{ color: rankColors[rank] }}
            >
                {username}
            </button>
           
            <button
                onClick={handleInviteClick}
                className="
                    border border-emerald-500/30 bg-emerald-500/5 text-emerald-400
                    hover:bg-emerald-500 hover:text-white hover:border-emerald-500 
                    hover:shadow-[0_0_15px_rgba(16,185,129,0.4)]
                    px-4 py-2 rounded-full 
                    font-anta text-xs uppercase tracking-widest 
                    transition-all duration-300
                "
            >
                Invite
            </button>
        </div>
    );
}