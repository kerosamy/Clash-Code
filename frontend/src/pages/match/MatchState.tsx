import { useEffect, useState, useMemo } from "react";
import { useParams } from "react-router-dom";

import Board from "../../components/common/Board";
import LogoLoader from "../../components/Loader/LogoLoader";
import SubmissionDetails from "../../components/common/SubmissionDetails";

import { getMatchSubmissionLog } from "../../services/MatchService";
import type { MatchSubmissionLogDto, SubmissionLogEntryDto } from "../../services/MatchService";

import { rankColors } from "../../utils/colorMapper";
import { getUsername } from "../../utils/jwtDecoder"; 
import { getSubmissionStatusColor } from "../../utils/getSubmissionStatusColor"; 
import { SubmissionStatus } from "../../enums/SubmissionStatus"; 
import { getSubmissionStatusDisplay } from "../../utils/getSubmissionStatusDisplay";
import { formatTime } from "../../utils/matchUtils";

export default function MatchStatePage() {
  const { id: matchIdString } = useParams<{ id: string }>();
  const [logs, setLogs] = useState<MatchSubmissionLogDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [myUsername, setMyUsername] = useState<string | null>(null);

  const [selectedSubmissionId, setSelectedSubmissionId] = useState<number | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  useEffect(() => {
    const user = getUsername();
    setMyUsername(user);
  }, []);

  useEffect(() => {
    const numericId = Number(matchIdString);
    if (!matchIdString || isNaN(numericId) || numericId === 0) {
      setLoading(false);
      return;
    }

   const fetchLogs = () => {
      getMatchSubmissionLog(numericId)
        .then((data) => {
            if (Array.isArray(data)) {
                const sortedData = data.map(playerLog => ({
                    ...playerLog,
                    submissions: playerLog.submissions.sort((a, b) => 
                        new Date(b.submittedAt).getTime() - new Date(a.submittedAt).getTime()
                    )
                }));
                setLogs(sortedData); 
            }
        })
        .catch((err) => {
            console.error("Failed to fetch match logs", err);
        })
        .finally(() => {
            setLoading(false);
        });
    };
    fetchLogs(); 
    const intervalId = setInterval(fetchLogs, 3000); 
    return () => clearInterval(intervalId);
  }, [matchIdString]);

  const [leftPlayer, rightPlayer] = useMemo(() => {
    if (logs.length === 0) return [undefined, undefined];

    if (myUsername) {
      const cleanMe = myUsername.trim().toLowerCase();
      const myLog = logs.find(log => log.username?.trim().toLowerCase() === cleanMe);
      const opponentLog = logs.find(log => log.username?.trim().toLowerCase() !== cleanMe);

      if (myLog) {
        return [myLog, opponentLog];
      }
    }
    return [logs[0], logs[1]];
  }, [logs, myUsername]);

  const renderSubmissionRow = (submission: SubmissionLogEntryDto, onClick?: () => void) => {
    const status = submission.status as SubmissionStatus;
    const statusText = status === SubmissionStatus.RUNNING_ON_TEST
        ? `Running on Test ${submission.numberOfCurrentTestCase || 0} / ${submission.numberOfTotalTestCases || 0}`
        : getSubmissionStatusDisplay(status);

    return (
        <div 
          key={submission.submissionId} 
          onClick={onClick}
          className={`grid grid-cols-[100px_1.5fr_100px] gap-4 px-6 py-3 items-center border-b border-sidebar text-text font-anta transition-colors 
            ${onClick ? "cursor-pointer hover:bg-white/5" : ""}`} 
        >
          <span className="text-sm text-center">{formatTime(submission.submittedAt)}</span>
          <span className={`text-sm text-center font-bold ${getSubmissionStatusColor(status)}`}>
            {statusText}
          </span>
          <span className="text-sm text-center text-text/80">{submission.numberOfPassedTestCases}/{submission.numberOfTotalTestCases}</span>
        </div>
    );
  };

  const PlayerHeader = ({ player, label }: { player?: MatchSubmissionLogDto, label: string }) => {
    if (!player) {
      
        return (
            <div className="flex flex-col items-center gap-4 opacity-50">
                <div className="w-40 h-40 rounded-full bg-sidebar border-4 border-dashed border-text/20 flex items-center justify-center">
                    <span className="text-4xl text-text font-anta">?</span>
                </div>
                <div className="text-center">
                    <h2 className="text-3xl font-anta font-bold tracking-wide text-text">{label}</h2>
                    <p className="text-sm text-text/60 mt-1 font-anta">Waiting...</p>
                </div>
            </div>
        );
    }

    const rankKey = player.rank?.toUpperCase() || "BRONZE";
    const userRankColor = rankColors[rankKey] || "#FFFFFF";

    return (
        <div className="flex flex-col items-center gap-4">
            <div className="relative group">
                <img
                    src={player.avatarUrl} 
                    className="relative w-56 h-56 rounded-full object-cover border-4 border-container shadow-2xl"
                />
            </div>
            <div className="text-center">
                <h2 
                    className="text-4xl font-anta font-bold tracking-wide drop-shadow-md"
                    style={{ color: userRankColor }}
                >
                    {player.username}
                </h2>
            </div>
        </div>
    );
  };

  const VersusBadge = () => (
    <div className="flex flex-col items-center justify-center select-none">
      <div className="font-black italic flex items-end leading-none font-anta">
        <span className="text-orange text-[6rem] md:text-[8rem] transform -translate-y-2 -translate-x-2 drop-shadow-lg z-10">
          V
        </span>
        <span className="text-white/40 text-[6rem] md:text-[8rem] transform translate-y-2 translate-x-2 drop-shadow-lg z-0">
          S
        </span>
      </div>
    </div>
  );
  
  if (loading) return <LogoLoader loadingMessage="Loading Match State" />;

  const columns = ["Time", "Verdict", "Passed"];
  const gridConfig = "grid-cols-[100px_1.5fr_100px]";

  return (
    <div className="w-full min-h-full flex flex-col gap-6 p-6 bg-background">
      <div className="flex flex-col md:flex-row items-center justify-between w-full max-w-[950px] mx-auto mb-4 px-8 md:px-12">
        <PlayerHeader player={leftPlayer} label={leftPlayer?.username || "Player 1"} />
        <VersusBadge />
        <PlayerHeader player={rightPlayer} label={rightPlayer?.username || "Player 2"} />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-12 w-full max-w-[1400px] mx-auto">

        <div className="flex flex-col gap-2">
            <Board
                data={leftPlayer?.submissions || []}
                columns={columns}
                gridCols={gridConfig}
                renderRow={renderSubmissionRow}
                onRowClick={(sub) => {
                    if (sub.submissionId) {
                        setSelectedSubmissionId(sub.submissionId);
                        setIsModalOpen(true);
                    }
                }} 
            />
        </div>

        <div className="flex flex-col gap-2">
            <Board
                data={rightPlayer?.submissions || []}
                columns={columns}
                gridCols={gridConfig}
                renderRow={renderSubmissionRow}
              
            />
        </div>
      </div>

      {selectedSubmissionId !== null && (
        <SubmissionDetails
            submissionId={selectedSubmissionId}
            isOpen={isModalOpen}
            onClose={() => {
                setIsModalOpen(false);
                setSelectedSubmissionId(null);
            }}
        />
      )}
    </div>
  );
}