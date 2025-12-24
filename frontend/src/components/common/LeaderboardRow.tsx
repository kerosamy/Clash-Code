import { rankDisplayNames } from '../../utils/rankDisplayNameMapper';

interface LeaderboardRowProps {
    position: number;
    username: string;
    rank: string;
    rankColor: string;
    rate: number;
    onUsernameClick?: () => void;
}

export default function LeaderboardRow({
    position,
    username,
    rank,
    rankColor,
    rate,
    onUsernameClick,
}: LeaderboardRowProps) {
    
    // Aesthetic mapping for Top 3
    const isTopThree = position <= 3;
    const podiumStyles = {
        1: "bg-gradient-to-r from-yellow-500/20 to-transparent border-l-4 border-yellow-500 shadow-[0_0_15px_rgba(234,179,8,0.1)]",
        2: "bg-gradient-to-r from-slate-400/15 to-transparent border-l-4 border-slate-400",
        3: "bg-gradient-to-r from-amber-700/15 to-transparent border-l-4 border-amber-700",
    }[position] || "border-l-4 border-transparent hover:bg-white/5";

    const textGlow = isTopThree ? "drop-shadow-[0_0_8px_rgba(255,255,255,0.3)]" : "";

    return (
        <div
            onClick={onUsernameClick}
            className={`
                grid grid-cols-[80px_1fr_1fr_120px] gap-4 px-6 py-4 items-center 
                transition-all duration-300 cursor-pointer mb-1 rounded-r-md
                ${podiumStyles}
            `}
        >
            {/* Rank Position with Medal Emoji */}
            <div className="flex justify-center items-center gap-1">
                {position === 1 && <span className="text-xl">👑</span>}
                <span className={`font-anta text-lg ${isTopThree ? 'font-bold' : 'text-text/40'}`}>
                    {position === 1 ? "" : `#${position}`}
                </span>
            </div>
            
            {/* Username with custom Rank Color */}
            <span 
                className={`font-anta text-base font-bold text-center tracking-wide ${textGlow}`}
                style={{ color: rankColor }}
            >
                {username}
            </span>

            {/* Rank Badge Style */}
            <div className="flex justify-center">
                <span 
                    className="font-anta text-[10px] px-3 py-1 rounded-full border uppercase tracking-widest"
                    style={{ borderColor: `${rankColor}40`, color: rankColor, backgroundColor: `${rankColor}10` }}
                >
                    {rankDisplayNames[rank] || rank}
                </span>
            </div>

            {/* Rate / Rating with high contrast */}
            <div className="flex flex-col items-center group">
                <span className="text-white font-anta text-xl font-bold group-hover:text-orange transition-colors">
                    {rate}
                </span>
                <span className="text-[9px] text-orange uppercase tracking-tighter opacity-70">Points</span>
            </div>
        </div>
    );
}