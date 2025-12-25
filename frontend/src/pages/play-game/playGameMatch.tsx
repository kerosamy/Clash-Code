import { useState, useEffect } from "react";
import { Outlet, useParams } from 'react-router-dom';

import TopNavigator from "../../components/common/TopNavigators";
import ConfirmationModal from "../../components/common/ConfirmationModal";
import DraggableTimer from "../../components/match/Timer";
import MatchResults from "../../components/match/MatchResults";


import { matchSubRoutes } from '../../routes/routes.config';
import { resignMatch, getMatchDetails, getMatchResults } from "../../services/MatchService"; 
import type { MatchResultDto } from "../../services/MatchService";
import { useWebSocket } from "../../contexts/WebSocketContext";
import { useMatchGuard } from "../../hooks/useMatchGuard";
import { setActiveMatch, clearActiveMatch } from "../../utils/matchState";
import { updateStatusToOnline , updateStatusToInMatch} from "../../services/UserService";

interface MatchData {
    startAt: string;
    duration: number;
    state: string;
    problemId?: number;
}

export default function PlayGame() {
    const { id } = useParams<{ id: string }>();
    const { notifications } = useWebSocket();
    const [isResignModalOpen, setIsResignModalOpen] = useState(false);
    const [isProcessing, setIsProcessing] = useState(false);
    const [matchData, setMatchData] = useState<MatchData | null>(null);
    
    const [matchResults, setMatchResults] = useState<MatchResultDto | null>(null);
    const [showResultOverlay, setShowResultOverlay] = useState(false);

    const isMatchActive = matchData?.state === "ONGOING";

    useEffect(() => {
        if (id) {
            updateStatusToInMatch();
            setActiveMatch(id);
        }

        return () => {
            if (!isMatchActive) {
                clearActiveMatch();
            }
        };
    }, [id, isMatchActive]);

    const fetchAndShowResults = async () => {
        if (!id) return;
        try {
            const results = await getMatchResults(Number(id));
            setMatchResults(results);
            setShowResultOverlay(true);
            setMatchData(prev => prev ? { ...prev, state: "COMPLETED" } : null);
            clearActiveMatch();
            await updateStatusToOnline();
        } catch (error) {
            console.error("Failed to load match results", error);
        }
    };

    useEffect(() => {
        if (!id) return;

        const fetchInitialData = async () => {
            try {
                const details = await getMatchDetails(Number(id));
                setMatchData({
                    startAt: details.startAt,
                    duration: details.duration,
                    state: details.matchState,
                    problemId: details.problemId
                });

                if (details.matchState === "COMPLETED" || details.matchState === "RESIGNED") {
                    fetchAndShowResults();
                }

            } catch (error) {
                console.error("Could not fetch match data", error);
            }
        };

        fetchInitialData();
    }, [id]); 

    useEffect(() => {
        const matchNotifications = notifications.filter(n => 
            n.metadata?.matchId === Number(id) && 
            !n.read &&
            ['MATCH_COMPLETED', 'USER_RESIGNED'].includes(n.metadata?.notificationType)
        );

        matchNotifications.forEach(notification => {
            const payload = notification.metadata;

            if (payload.notificationType === 'MATCH_COMPLETED') {
                setMatchData(prev => prev ? { ...prev, state: "COMPLETED" } : null);
                setTimeout(() => {
                    fetchAndShowResults();
                }, 1000); 
            }
            else if (payload.notificationType === 'USER_RESIGNED') {
                setMatchData(prev => prev ? { ...prev, state: "COMPLETED" } : null);
            }
        });
    }, [notifications, id]);

    const handleResignFromMatch = async () => {
        if (!id) return;
        
        try {
            await resignMatch(Number(id));
            setMatchData(prev => prev ? { ...prev, state: "COMPLETED" } : null);
            clearActiveMatch();
        } catch (error) {
            console.error("Resignation failed", error);
            throw error;
        }
    };

    const {
        showResignModal: showNavBlockModal,
        handleResignConfirm: handleNavBlockResign,
        handleResignCancel: handleNavBlockCancel
    } = useMatchGuard({
        matchId: id || '',
        onResign: handleResignFromMatch,
        enabled: isMatchActive
    });

    const handleResignClick = () => setIsResignModalOpen(true);
    
    const handleConfirmResign = async () => {
        if (!id) return;
        setIsProcessing(true);
        try {
            await resignMatch(Number(id));
            setMatchData(prev => prev ? { ...prev, state: "COMPLETED" } : null);
            clearActiveMatch();
            setIsResignModalOpen(false);
        } catch (error) {
            console.error("Resignation failed", error);
            alert("Failed to resign.");
        } finally {
            setIsProcessing(false);
        }
    };

    return (
        <div className="flex flex-col h-screen font-anta relative bg-background">
            
            <div className="relative w-full">
                <TopNavigator navigators={matchSubRoutes} />
                {isMatchActive && (
                    <div className="absolute top-3 right-4 h-full flex items-center pr-2 pointer-events-none">
                        <button
                            onClick={handleResignClick}
                            className="pointer-events-auto bg-orange hover:bg-orange/90 text-white 
                                       px-6 py-2 rounded-button text-sm font-bold tracking-wide font-anta 
                                       shadow-md transition-all duration-200"
                        >
                            Resign
                        </button>
                    </div>
                )}
            </div>

            <div className="flex-1 flex flex-col overflow-y-auto custom-scroll">
                <Outlet context={{ problemId: matchData?.problemId }} />
            </div>

            {matchData && (
                <DraggableTimer 
                    startAt={matchData.startAt} 
                    durationMinutes={matchData.duration} 
                    isMatchOver={!isMatchActive} 
                />
            )}

            {showResultOverlay && matchResults && (
                <MatchResults
                    result={matchResults} 
                />
            )}

            <ConfirmationModal
                isOpen={isResignModalOpen}
                onClose={() => setIsResignModalOpen(false)}
                onConfirm={handleConfirmResign}
                title="Resign?"
                message="Are you sure you want to resign?"
                confirmText="Yes, Resign"
                cancelText="No, Keep Playing"
                isLoading={isProcessing}
            />

            <ConfirmationModal
                isOpen={showNavBlockModal}
                onClose={handleNavBlockCancel}
                onConfirm={handleNavBlockResign}
                title="Leave Match"
                message="You must resign from the match to leave. This will count as a loss. Are you sure?"
                confirmText="Resign & Leave"
                cancelText="Stay in Match"
            />
        </div>
    );
}