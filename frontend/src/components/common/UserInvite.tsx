import { rankColors } from '../../utils/colorMapper';

export interface UserInviteProps {
    order: number;
    username: string;
    rank: string;
    onInviteClick: () => void;
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
                onClick={onInviteClick}
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