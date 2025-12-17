import { useState, useEffect, useRef } from "react";
import { Outlet, useParams } from 'react-router-dom';
import { isFinalStatus } from "../utils/matchUtils";
import { Client, type IMessage } from "@stomp/stompjs";

import TopNavigator from "../components/common/TopNavigators";
import ConfirmationModal from "../components/common/ConfirmationModal";
import DraggableTimer from "../components/match/Timer";

import ToastFeed from '../components/common/PopNotification';
import type { ToastNotification } from '../components/common/PopNotification';

import { matchSubRoutes } from '../routes/routes.config';
import { resignMatch, getMatchDetails, getMatchSubmissionLog } from "../services/MatchService";
import { getUsername } from "../utils/jwtDecoder";


interface MatchData {
    startAt: string;
    duration: number;
    state: string;
}

export interface LiveLog {
    username: string;
    status: string;
    passedCases: number;
    totalCases: number;
    timestamp: string;
    rawTime?: number;
}

interface WebSocketPayload {
    notificationType: 'SUBMISSION_RECEIVED' | 'SUBMISSION_RESULT' | 'MATCH_COMPLETED'; 
    senderUsername: string;
    submissionStatus?: string;
    passedCases?: number;
    totalCases?: number;
    message?: string;
    title?: string;
}

export default function PlayGame() {
    const { id } = useParams<{ id: string }>();
    const [isResignModalOpen, setIsResignModalOpen] = useState(false);
    const [isProcessing, setIsProcessing] = useState(false);
    const [matchData, setMatchData] = useState<MatchData | null>(null);
    const [liveLogs, setLiveLogs] = useState<LiveLog[]>([]);
    const [notifications, setNotifications] = useState<ToastNotification[]>([]);

    const clientRef = useRef<Client | null>(null);

    useEffect(() => {
        if (!id) return;

        const fetchData = async () => {
            try {
                const details = await getMatchDetails(Number(id));
                setMatchData({
                    startAt: details.startAt,
                    duration: details.duration,
                    state: details.matchState
                });

                const historyData = await getMatchSubmissionLog(Number(id));
                const allLogs: LiveLog[] = [];

                historyData.forEach((userRecord) => {
                    const username = userRecord.profile.username;
                    
                    userRecord.submissions.forEach((sub) => {
                        if (isFinalStatus(sub.status)) {
                            allLogs.push({
                                username: username,
                                status: sub.status,
                                passedCases: sub.numberOfPassedTestCases,
                                totalCases: sub.numberOfTotalTestCases,
                                timestamp: new Date(sub.submittedAt).toLocaleTimeString(),
                                rawTime: new Date(sub.submittedAt).getTime()
                            });
                        }
                    });
                });

                allLogs.sort((a, b) => (b.rawTime || 0) - (a.rawTime || 0));
                setLiveLogs(allLogs);

            } catch (error) {
                console.error("Could not fetch match data", error);
            }
        };

        fetchData();
        
    }, [id]);

    useEffect(() => {
        const currentUser = getUsername();
        if (!currentUser) return;

        const client = new Client({
            brokerURL: "ws://localhost:8080/ws", 
            reconnectDelay: 5000,
        });

        client.onConnect = () => {
            client.subscribe(`/topic/match-pop/${currentUser}`, (message: IMessage) => {
                try {
                    const payload: WebSocketPayload = JSON.parse(message.body);
                    handleWebSocketMessage(payload);
                } catch (err) {
                }
            });
        };

        client.activate();
        clientRef.current = client;
        return () => { client.deactivate(); };
    }, []);

    const handleWebSocketMessage = (payload: WebSocketPayload) => {
        const notifId = Date.now();
        let newNotification: ToastNotification | null = null;

        if(payload.notificationType === 'MATCH_COMPLETED'){
            setMatchData(prev => prev ? { ...prev, state: "COMPLETED" } : null);
            //navigate to Match Results page 
        }
        else if (payload.notificationType === 'SUBMISSION_RECEIVED') {
            newNotification = {
                id: notifId,
                title: "Code Submitted",
                message: `${payload.senderUsername} submitted a solution...`,
                sender: payload.senderUsername,
                type: 'info'
            };
        } 
        
        else if (payload.notificationType === 'SUBMISSION_RESULT') {
            const isSuccess = payload.submissionStatus === 'ACCEPTED';
            const passFilter = isFinalStatus(payload.submissionStatus);

            if (passFilter) {
                const newLog: LiveLog = {
                    username: payload.senderUsername,
                    status: payload.submissionStatus || "UNKNOWN",
                    passedCases: payload.passedCases || 0,
                    totalCases: payload.totalCases || 0,
                    timestamp: new Date().toLocaleTimeString(),
                    rawTime: Date.now()
                };
                setLiveLogs(prev => [newLog, ...prev]);

                newNotification = {
                    id: notifId,
                    title: "Submission Result",
                    message: `${payload.senderUsername} got ${payload.submissionStatus}\n` +
                   `passed ${payload.passedCases}/${payload.totalCases} test cases.`,
                    sender: payload.senderUsername,
                    type: isSuccess ? 'success' : 'error'
                };
            }
        } 

        if (newNotification) {
            setNotifications((prev) => [...prev, newNotification!]);
            setTimeout(() => {
                setNotifications((prev) => prev.filter(n => n.id !== notifId));
            }, 5000);
        }
    };

    const handleResignClick = () => setIsResignModalOpen(true);
    const handleConfirmResign = async () => {
        if (!id) return;
        setIsProcessing(true);
        try {
            await resignMatch(Number(id));
            setMatchData(prev => prev ? { ...prev, state: "COMPLETED" } : null);
            setIsResignModalOpen(false);
            //todo navigate to Match Results page
        } catch (error) {
            console.error("Resignation failed", error);
            alert("Failed to resign.");
        } finally {
            setIsProcessing(false);
        }
    };

    const isMatchOngoing = matchData?.state === "ONGOING";

    return (
        <div className="flex flex-col h-screen font-anta relative bg-background">
            
            <div className="relative w-full">
                <TopNavigator navigators={matchSubRoutes} />
                {isMatchOngoing && (
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
                <Outlet context={{ liveLogs }} />
            </div>

            {matchData && (
                <DraggableTimer 
                    startAt={matchData.startAt} 
                    durationMinutes={matchData.duration} 
                    isMatchOver={!isMatchOngoing} 
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

            <ToastFeed notifications={notifications} />
        </div>
    );
}