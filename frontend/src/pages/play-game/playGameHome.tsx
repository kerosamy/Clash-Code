import { useState, useEffect } from "react";
import { useNavigate } from 'react-router-dom';
import FriendMatchingPopUp from "../../components/common/FriendMatchingPopUp";
import LoadingMatch from "../../components/Loader/LoadingMatch";
import { fetchMyProfile } from "../../services/UserService";
import { calculateNextRate, getRankColor } from "../../utils/calculateNextRate";
import { RANKS } from "../../enums/Ranks";
import type { NextRankInfo } from "../../utils/calculateNextRate";

interface UserStats {
    currentRate: number;
    currentRank: RANKS;
    nextRankInfo: NextRankInfo;
}

export default function PlayGameHome() {
    const navigate = useNavigate();  // kept here for match navigation after matchmaking
    const [isFriendMatchingOpen, setIsFriendMatchingOpen] = useState<boolean>(false);
    const [isMatchmaking, setIsMatchmaking] = useState<boolean>(false);
    const [matchType, setMatchType] = useState<"opponent" | "friend">("opponent");
    const [invitedUser, setInvitedUser] = useState<string>("");
    const [userStats, setUserStats] = useState<UserStats>({
        currentRate: 0,
        currentRank: RANKS.BRONZE, 
        nextRankInfo: {
            nextRate: 0,
            nextRank: RANKS.BRONZE as RANKS | "MAX" | null,
            nextRankColor: "#6B7280",
            isMaxRank: false
        }
    });
    const [isLoading, setIsLoading] = useState<boolean>(true);

    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const profile = await fetchMyProfile();
                const nextRankInfo = calculateNextRate(profile.currentRate);
                
                setUserStats({
                    currentRate: profile.currentRate,
                    currentRank: profile.rank as RANKS,
                    nextRankInfo
                });
            } catch (error) {
                console.error("Failed to fetch user profile", error);
            } finally {
                setIsLoading(false);
            }
        };

        fetchUserData();
    }, []);

    // cleanup matchmaking state when component unmounts
    useEffect(() => {
        return () => {
            // This runs when user navigates away
            if (isMatchmaking) {
                handleCancelMatchmaking();
            }
        };
    }, [isMatchmaking]);

    const currentRankColor = getRankColor(userStats.currentRank);

    const handleOpponentMatching = () => {
        setMatchType("opponent");
        setInvitedUser("");
        setIsMatchmaking(true);
    };

    const handleFriendInvite = (username: string) => {
        setMatchType("friend");
        setInvitedUser(username);
        setIsMatchmaking(true);
        setIsFriendMatchingOpen(false);
    };

    const handleCancelMatchmaking = () => {
        setIsMatchmaking(false);
        setInvitedUser("");
    };


    if (isMatchmaking) {
        return (
            <LoadingMatch 
                matchType={matchType}
                invitedUser={invitedUser}
                onCancel={handleCancelMatchmaking}
            />
        );
    }

    return (
        <div className="flex flex-col h-screen font-anta relative bg-background">
            <div className="flex-1 flex flex-col">

                <div className="flex items-center justify-center gap-24 pt-8 pb-4 px-4">
                    <div className="text-center">
                        <div className="text-3xl text-gray-300 mb-3 font-anta">Current Rate</div>
                        <div 
                            className="text-6xl font-bold font-anta"
                            style={{ color: currentRankColor }}
                        >
                            {isLoading ? "..." : userStats.currentRate}
                        </div>
                    </div>
                    
                    <div className="text-center">
                        <div className="text-3xl text-gray-300 mb-3 font-anta">Current Rank</div>
                        <div 
                            className="text-6xl font-bold font-anta"
                            style={{ color: currentRankColor }}
                        >
                            {isLoading ? "..." : userStats.currentRank}
                        </div>
                    </div>
                    
                    <div className="text-center">
                        <div className="text-3xl text-gray-300 mb-3 font-anta">
                            {userStats.nextRankInfo.isMaxRank ? "Max Rank!" : "Points to Next Rank"}
                        </div>
                        <div 
                            className="text-6xl font-bold font-anta"
                            style={{ color: userStats.nextRankInfo.nextRankColor }}
                        >
                            {isLoading 
                                ? "..." 
                                : userStats.nextRankInfo.isMaxRank 
                                    ? "🏆" 
                                    : userStats.nextRankInfo.nextRate
                            }
                        </div>
                    </div>
                </div>


                <div className="px-4 py-20">
                    <div className="flex justify-center">
                        <img src="/src/assets/logo.svg" alt="App Logo" className="w-[900px] h-auto" />
                    </div>
                </div>

                <div className="flex flex-col items-center justify-center px-15">
                    <div className="flex flex-col gap-8">
                        <button
                            onClick={handleOpponentMatching}
                            className="
                                flex items-center justify-center
                                border border-cyan-500/30 bg-cyan-500/5 text-cyan-500
                                hover:bg-cyan-500 hover:text-white hover:border-cyan-500 hover:shadow-[0_0_15px_rgba(6,182,212,0.4)]
                                px-10 py-6 rounded-full 
                                font-anta text-2xl uppercase tracking-widest 
                                transition-all duration-300

                            "
                        >
                            Opponent Matching
                        </button>
                        
                        <button
                            onClick={() => setIsFriendMatchingOpen(true)}
                            className="
                                flex items-center justify-center
                                border border-emerald-500/30 bg-emerald-500/5 text-emerald-500
                                hover:bg-emerald-500 hover:text-white hover:border-emerald-500 hover:shadow-[0_0_15px_rgba(16,185,129,0.4)]
                                px-10 py-6 rounded-full 
                                font-anta text-2xl uppercase tracking-widest 
                                transition-all duration-300

                            "
                        >
                            Friend Matching
                        </button>
                    </div>
                </div>
            </div>

            <FriendMatchingPopUp 
                isOpen={isFriendMatchingOpen}
                onClose={() => setIsFriendMatchingOpen(false)}
                onInvite={handleFriendInvite}
            />
        </div>
    );
}