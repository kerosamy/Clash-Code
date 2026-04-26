import { useState, useEffect, useRef } from "react";
import { useNavigate } from 'react-router-dom';
import FriendMatchingPopUp from "../../components/common/FriendMatchingPopUp";
import LoadingMatch from "../../components/Loader/LoadingMatch";
import MatchIntroAnimation from "../../components/match/MatchIntroAnimation";
import { fetchMyProfile } from "../../services/UserService";
import { calculateNextRate, getRankColor } from "../../utils/calculateNextRate";
import { RANKS } from "../../enums/Ranks";
import type { NextRankInfo } from "../../utils/calculateNextRate";
import { searchOpponent, cancelOpponentSearch, getMatchSubmissionLog, getOnGoingMatch, getMatchDetails } from "../../services/MatchService";
import { getUsername } from "../../utils/jwtDecoder";
import { setActiveMatch } from "../../utils/matchState";
import { useWebSocket } from "../../contexts/WebSocketContext";

interface UserStats {
    currentRate: number;
    currentRank: RANKS;
    nextRankInfo: NextRankInfo;
}

interface MatchData {
    matchId: number;
    problemId: number;
    player1: {
        username: string;
        avatarUrl: string;
        rank: string;
    };
    player2: {
        username: string;
        avatarUrl: string;
        rank: string;
    };
}

export default function PlayGameHome() {
    const navigate = useNavigate();
    const { notifications } = useWebSocket(); // CHANGED: Only get notifications, don't subscribe
    const [isFriendMatchingOpen, setIsFriendMatchingOpen] = useState<boolean>(false);
    const [isMatchmaking, setIsMatchmaking] = useState<boolean>(false);
    const [matchType, setMatchType] = useState<"opponent" | "friend">("opponent");
    const [invitedUser, setInvitedUser] = useState<string>("");
    const [showIntroAnimation, setShowIntroAnimation] = useState<boolean>(false);
    const [matchData, setMatchData] = useState<MatchData | null>(null);
    const [pendingNotificationId, setPendingNotificationId] = useState<number | undefined>();
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
    
    const isMatchmakingRef = useRef(false);
    
    useEffect(() => {
        isMatchmakingRef.current = isMatchmaking;
    }, [isMatchmaking]);

    // Combined initial data fetch - Check ongoing match FIRST, then fetch user data
    useEffect(() => {
        const initializeData = async () => {
            try {
                // Check for ongoing match first
                const currentUser = getUsername();
                if (!currentUser) {
                    setIsLoading(false);
                    return;
                }

                const matchId = await getOnGoingMatch();
                console.log('Ongoing match check:', matchId);
                
                if (matchId) {
                    console.log('Found ongoing match, fetching details...');
                    const [data, match] = await Promise.all([
                        getMatchSubmissionLog(matchId),
                        getMatchDetails(matchId)
                    ]);

                    const opponent = data.find(player => player.username !== currentUser);
                    if (!opponent) {
                        console.error("Opponent not found in submission log");
                        setIsLoading(false);
                        return;
                    }

                    setMatchData({
                        matchId: matchId,
                        problemId: match.problemId,
                        player1: {
                            username: currentUser,
                            avatarUrl: data.find(p => p.username === currentUser)?.avatarUrl || "",
                            rank: data.find(p => p.username === currentUser)?.rank || "BRONZE",
                        },
                        player2: {
                            username: opponent.username,
                            avatarUrl: opponent.avatarUrl || "/default-avatar.png",
                            rank: opponent.rank,
                        },
                    });

                    setShowIntroAnimation(true);
                    return; // Don't load other data if we're showing animation
                }

                // No ongoing match, fetch user profile
                const profile = await fetchMyProfile();
                const nextRankInfo = calculateNextRate(profile.currentRate);
                
                setUserStats({
                    currentRate: profile.currentRate,
                    currentRank: profile.rank as RANKS,
                    nextRankInfo
                });
            } catch (error) {
                console.error("Failed to initialize data", error);
            } finally {
                setIsLoading(false);
            }
        };

        initializeData();
    }, []);

    // CHANGED: Listen to notifications from context instead of subscribing directly
    useEffect(() => {
        const matchStartedNotification = notifications.find(
            n => n.metadata?.notificationType === 'MATCH_STARTED' && !n.read
        );

        if (matchStartedNotification) {
            const handleMatchStarted = async () => {
                try {
                    const payload = matchStartedNotification.metadata;
                    console.log('Match started notification:', payload);
                    const data = await getMatchSubmissionLog(payload.matchId);

                    const currentUser = getUsername();
                    if (!currentUser) return;

                    const opponent = data.find(player => player.username !== currentUser);
                    if (!opponent) {
                        console.error("Opponent not found in submission log");
                        return;
                    }

                    setMatchData({
                        matchId: payload.matchId,
                        problemId: payload.problemId,
                        player1: {
                            username: currentUser,
                            avatarUrl: data.find(p => p.username === currentUser)?.avatarUrl || "",
                            rank: data.find(p => p.username === currentUser)?.rank || "BRONZE",
                        },
                        player2: {
                            username: opponent.username,
                            avatarUrl: opponent.avatarUrl || "/default-avatar.png",
                            rank: opponent.rank,
                        },
                    });

                    setIsMatchmaking(false);
                    setShowIntroAnimation(true);
                } catch (err) {
                    console.error("Failed to process match started", err);
                }
            };

            handleMatchStarted();
        }
    }, [notifications]);

    // Cleanup on unmount
    useEffect(() => {
        return () => {
            if (isMatchmakingRef.current) {
                cancelOpponentSearch().catch(console.error);
            }
        };
    }, []);

    const handleAnimationComplete = () => {
        if (matchData) {
            setActiveMatch(matchData.matchId.toString());
            
            navigate(`/play-game/${matchData.matchId}`, {
                state: { problemId: matchData.problemId }
            });
        }
    };

    const currentRankColor = getRankColor(userStats.currentRank);

    const handleOpponentMatching = async () => {
        setMatchType("opponent");
        setInvitedUser("");
        setPendingNotificationId(undefined);
        setIsMatchmaking(true);
        try {
            await searchOpponent();
            console.log('Started searching for opponent');
        } catch (error) {
            console.error("Failed to start opponent search", error);
            setIsMatchmaking(false);
        }
    };

    const handleCancelMatchmaking = async () => {
        setIsMatchmaking(false);
        setInvitedUser("");
        setPendingNotificationId(undefined);
        try {
            await cancelOpponentSearch();
            console.log('Cancelled opponent search');
        } catch (error) {
            console.error("Failed to cancel opponent search", error);
        }
    };

    const handleFriendInvite = (notificationId: number, username: string) => {
        setMatchType("friend");
        setInvitedUser(username);
        setPendingNotificationId(notificationId);
        setIsMatchmaking(true);
        setIsFriendMatchingOpen(false);
    };

    if (showIntroAnimation && matchData) {
        return (
            <MatchIntroAnimation
                player1={matchData.player1}
                player2={matchData.player2}
                onComplete={handleAnimationComplete}
            />
        );
    }

    if (isMatchmaking) {
        return (
            <LoadingMatch 
                matchType={matchType}
                invitedUser={invitedUser}
                notificationId={pendingNotificationId}
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
                        <div className="text-3xl text-gray-300 mb-2 font-anta">
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

                <div className="px-4 py-16">
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
                                px-8 py-4 rounded-full 
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
                                px-8 py-4 rounded-full 
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