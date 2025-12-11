import { rankColors } from '../../utils/colorMapper';
import { rankDisplayNames } from '../../utils/rankDisplayNameMapper';
import { roleDisplayNames } from '../../utils/roleDisplayNameMapper';

export interface UserManagementRowProps {
    order: number;
    id: number;
    username: string;
    email: string;
    role: string;
    rank: string;
    onPromoteClick?: () => void;
    onDemoteClick?: () => void;
    onUsernameClick?: () => void;
    className?: string;
}

export default function UserManagementRow({
    order,
    username,
    email,
    role,
    rank,
    onPromoteClick,
    onDemoteClick,
    onUsernameClick,
    className = "",
}: UserManagementRowProps) {

    const getRoleColor = (role: string) => {
        switch (role) {
            case 'ADMIN':
                return 'text-yellow-500';
            default:
                return 'text-text';
        }
    };

    const displayRank = rankDisplayNames[rank] || rank;
    const rankColor = rankColors[rank] || '#FFFFFF';

    return (
        <div
            onClick={onUsernameClick}
            className={`grid grid-cols-[60px_2fr_2fr_1fr_1fr_100px] gap-4 px-6 py-3 items-center hover:bg-sidebar/20 transition-colors cursor-pointer ${className}`}
        >
            <span className="text-text text-center font-anta text-sm">
                {order}
            </span>
            
            <span
                className="font-anta text-sm font-bold text-center"
                style={{ color: rankColor }}
            >
                {username}
            </span>

            <span 
                className="font-anta text-sm font-bold text-center truncate" 
                style={{ color: rankColor }}
                title={email}
            >
                {email}
            </span>

            <span 
                className="font-anta text-sm text-center font-bold"
                style={{ color: rankColor }}
            >
                {displayRank}
            </span>

            <span className={`font-anta text-sm text-center ${getRoleColor(role)}`}>
                {roleDisplayNames[role] || role}
            </span>
           
            <div 
                className="flex items-center justify-center"
                onClick={(e) => e.stopPropagation()}
            >
                {role === 'SUPER_ADMIN' ? (
                    <span></span>
                ) : role === 'ADMIN' ? (
                    <button
                        onClick={onDemoteClick}
                        className="
                            flex items-center justify-center
                            border border-rose-500/30 bg-rose-500/5 text-rose-400
                            hover:bg-rose-600 hover:text-white hover:border-rose-600 hover:shadow-[0_0_10px_rgba(225,29,72,0.3)]
                            px-4 py-1 rounded-full 
                            font-anta text-[10px] uppercase tracking-widest 
                            transition-all duration-300
                        "
                    >
                        Demote
                    </button>
                ) : (
                    <button
                        onClick={onPromoteClick}
                        className="
                            flex items-center justify-center
                            border border-emerald-500/30 bg-emerald-500/5 text-emerald-400
                            hover:bg-emerald-500 hover:text-white hover:border-emerald-500 hover:shadow-[0_0_10px_rgba(16,185,129,0.3)]
                            px-4 py-1 rounded-full 
                            font-anta text-[10px] uppercase tracking-widest 
                            transition-all duration-300
                        "
                    >
                        Promote
                    </button>
                )}
            </div>
        </div>
    );
}