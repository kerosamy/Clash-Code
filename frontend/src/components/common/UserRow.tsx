import Add from '../../assets/icons/Add.svg';
import { rankColors } from '../../utils/rankColors';

export interface UserRowProps {
    order: number;
    username: string;
    rank: string;
    onAddClick?: () => void;
    onUsernameClick?: () => void;
    className?: string;
}

export default function UserRow({
    order,
    username,
    rank,
    onAddClick,
    onUsernameClick,
    className = "",
}: UserRowProps) {

    return (
        <div
            className={`grid grid-cols-[60px_1fr_60px] gap-4 px-6 py-3 items-center hover:bg-sidebar/20 transition-colors ${className}`}
        >
            <span className="text-text text-center font-anta text-sm">
                {order}
            </span>
            
            <button
                onClick={onUsernameClick}
                className="font-anta text-sm font-bold truncate text-left hover transition-colors"
                style={{ color: rankColors[rank] }}
            >
                {username}
            </button>
           
            <button
                onClick={onAddClick}
                className="w-6 h-6 flex items-center justify-center hover:scale-110 transition-transform"
            >
                <img
                    src={Add}
                    alt="Add"
                    className="w-full h-full"
                />
            </button>
        </div>
    );
}
