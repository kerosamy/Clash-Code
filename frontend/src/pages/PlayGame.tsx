import { useState, useEffect } from "react";
import { Outlet, useParams, useNavigate } from 'react-router-dom';

import TopNavigator from "../components/common/TopNavigators";
import ConfirmationModal from "../components/common/ConfirmationModal";

import { matchSubRoutes } from '../routes/routes.config';
import { resignMatch, getMatchDetails } from "../services/MatchService";

export default function PlayGame() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();

    const [isResignModalOpen, setIsResignModalOpen] = useState(false);
    const [isProcessing, setIsProcessing] = useState(false);
   
    const [matchData, setMatchData] = useState<{ startAt: string, duration: number, state: string } | null>(null);

    useEffect(() => {
        if (!id) return;

        const fetchMatchInfo = async () => {
            try {
                const data = await getMatchDetails(Number(id));
                setMatchData({
                    startAt: data.startAt,
                    duration: data.duration,
                    state: data.matchState 
                });
            } catch (error) {
                console.error("Could not fetch match details", error);
            }
        };

        fetchMatchInfo(); 
        const interval = setInterval(fetchMatchInfo, 5000); 
        return () => clearInterval(interval);
    }, [id]);

    const handleResignClick = () => {
        setIsResignModalOpen(true);
    };

    const handleConfirmResign = async () => {
        if (!id) return;
        
        setIsProcessing(true);
        try {
            resignMatch(Number(id));
            setMatchData(prev => prev ? { ...prev, state: "COMPLETED" } : null);
            setIsResignModalOpen(false);
            navigate(`/play-game/${id}/match-state`);
        } catch (error) {
            console.error("Resignation failed", error);
            alert("Failed to resign. Please try again.");
        } finally {
            setIsProcessing(false);
        }
    };

    const isMatchOngoing = matchData?.state === "ONGOING";
    return (
        <div className="flex flex-col min-h-screen font-anta relative bg-background">
            <div className="relative w-full">
                <TopNavigator navigators={matchSubRoutes} />
                {isMatchOngoing && (
                    <div className="absolute top-3 right-0 h-full flex items-center pr-6 pointer-events-none">
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
            <Outlet />
            <ConfirmationModal
                isOpen={isResignModalOpen}
                onClose={() => setIsResignModalOpen(false)}
                onConfirm={handleConfirmResign}
                title="Resign?"
                message="Are you sure you want to resign? This will count as a loss."
                confirmText="Yes, Resign"
                cancelText="No, Keep Playing"
                isLoading={isProcessing}
            />
        </div>
    );
}