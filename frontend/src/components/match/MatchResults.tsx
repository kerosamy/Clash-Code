import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Confetti from "react-confetti";
import type { MatchResultDto } from "../../services/MatchService";
import { getUsername } from "../../utils/jwtDecoder";
import { getRankColor } from "../../utils/colorMapper";
import { FaArrowRight } from "react-icons/fa";

interface MatchResultsProps {
    result: MatchResultDto;
}

export default function MatchResults({ result }: MatchResultsProps) {
    const navigate = useNavigate();
    const [windowSize, setWindowSize] = useState({ width: window.innerWidth, height: window.innerHeight });

    useEffect(() => {
        const handleResize = () => setWindowSize({ width: window.innerWidth, height: window.innerHeight });
        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    if (!result) return null;

    let resultState: 'WIN' | 'DRAW' | 'LOSE' = 'LOSE';

    if (result.rank === 1) {
        resultState = 'WIN';
    } else if (result.rank === 0) {
        resultState = 'DRAW';
    } else {
        resultState = 'LOSE';
    }

    const configMap = {
        WIN: {
            title: "VICTORY",
            msg: "You dominated the arena!",
            color: "text-winGreen",
            border: "border-winGreen",
            glow: "bg-winGreen"
        },
        DRAW: {
            title: "DRAW",
            msg: "An evenly matched battle.",
            color: "text-orange",
            border: "border-orange",
            glow: "bg-orange"
        },
        LOSE: {
            title: "DEFEAT",
            msg: "Better luck next time.",
            color: "text-loseRed",
            border: "border-loseRed",
            glow: "bg-loseRed"
        }
    };

    const config = configMap[resultState];

    const renderRatingChange = () => {
        if (!result.rated) {
            return (
                <div className="px-3 py-1 rounded-full bg-white/10 text-s text-text/60 font-bold uppercase tracking-wider">
                    Friendly Match
                </div>
            );
        }

        const oldRating = result.newRating - result.rateChange;
        const oldRankColor = getRankColor(oldRating);
        const newRankColor = getRankColor(result.newRating);

        const sign = result.rateChange >= 0 ? "+" : "-";
        const value = Math.abs(result.rateChange);

        const color = result.rateChange >= 0 ? "text-winGreen" :
            result.rateChange < 0 ? "text-loseRed" : "text-text/60";

        return (
            <div className="flex flex-col items-center animate-fade-in w-full">
                <span className="px-3 py-1 rounded-full bg-white/10 text-s text-text/60 font-bold uppercase tracking-wider mb-4">
                    Rating Update
                </span>

                <div className="flex items-center justify-center gap-6 md:gap-8">
                    <div className="flex flex-col items-center scale-90 opacity-80">
                        <span
                            className="text-3xl md:text-4xl font-black font-anta transition-colors duration-500"
                            style={{ color: oldRankColor }}
                        >
                            {oldRating}
                        </span>
                    </div>

                    <div className="flex flex-col items-center justify-center -mt-1">
                        <span className={`text-lg font-bold ${color} mb-1`}>
                            {sign}{value}
                        </span>
                         <FaArrowRight className={`w-10 h-10 text-text/60 opacity-90`} />
                    </div>

                    <div className="flex flex-col items-center scale-110">
                        <span
                            className="text-4xl md:text-5xl font-black font-anta drop-shadow-lg transition-colors duration-500"
                            style={{ color: newRankColor }}
                        >
                            {result.newRating}
                        </span>
                    </div>
                </div>
            </div>
        );
    };

return (
        <div className="fixed inset-0 z-[100] flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 font-anta animate-fade-in">
            
            {resultState === 'WIN' && <Confetti width={windowSize.width} height={windowSize.height} recycle={false} numberOfPieces={500} />}
            
            <div className="relative flex flex-col items-center max-w-2xl w-full p-10 rounded-2xl bg-container border border-sidebar shadow-2xl overflow-hidden animate-scale-up">
                <div className={`absolute top-0 w-full h-3 ${config.glow} shadow-[0_0_25px_currentColor]`} />

                <h1 className={`text-7xl font-black tracking-widest italic mb-2 drop-shadow-lg ${config.color}`}>
                    {config.title}
                </h1>
                <p className="text-text/60 tracking-wide text-xl mb-10 uppercase text-center">{config.msg}</p>

                <div className="flex flex-col items-center gap-4 mb-8 w-full">
                    <img 
                        src={result.avatarUrl} 
                        className={`w-48 h-48 rounded-full border-4 object-cover shadow-2xl ${config.border}`} 
                    />
                    
                    <h2 
                        className="text-4xl font-bold tracking-wide drop-shadow-md"
                        style={{ color: getRankColor(result.newRating) }}
                    >
                        {result.username}
                    </h2>
                </div>

                <div className="flex flex-col items-center justify-center w-full py-8 border-t border-sidebar">
                    {renderRatingChange()}
                </div>

                <div className="flex gap-6 w-full mt-6">
                    <button 
                        onClick={() => navigate(`/profile/${getUsername()}`)} 
                        className="flex-1 py-4 text-lg rounded-button bg-white/40 hover:bg-white/50 text-white font-bold transition-all"
                    >
                        Profile
                    </button> 

                    {/* Navigate to Match Page on Click */}
                    <button 
                        onClick={() => navigate(`/play-game`)}
                        className="flex-1 py-4 text-lg rounded-button bg-orange hover:bg-orange/90 text-white font-bold transition-all"
                    >
                        Match Page
                    </button>
                </div>
            </div>
        </div>
    );
}