import type { NotificationType } from "../../enums/NotificationType";
import {formatTimeAgo, getTypeColor} from "../../utils/notificationFormatDetailsMapper";

export interface NotificationRowProps {
  id: number;
  type: NotificationType;
  senderId: number;
  senderUsername: string;
  recipientId: number;
  title: string;
  message: string;
  createdAt: string;
  read: boolean;
  matchId?: number;
  submissionStatus?: string;
  passedCases?: number;
  totalCases?: number;
  onClick?: () => void;
  className?: string;
}

const getNotificationCategory = (type: NotificationType): "Match" | "Friend" => {
  const typeStr = String(type);
  
  // Check if it's a friend-related notification
  if (typeStr.includes("FRIEND")) {
    return "Friend";
  }
  
  // Everything else is match-related (MATCH_*, SUBMISSION_*, OPPONENT_*)
  return "Match";
};

export default function NotificationRow({
  type,
  senderUsername,
  title,
  message,
  createdAt,
  read,
  matchId,
  submissionStatus,
  passedCases,
  totalCases,
  onClick,
  className = "",
}: NotificationRowProps) {
  const category = getNotificationCategory(type);
  const typeColor = getTypeColor(type);

  return (
    <div
      onClick={onClick}
      className={`
        grid grid-cols-[150px_150px_1fr_120px_120px] gap-4 px-6 py-4
        border-b border-white/5 transition-all duration-200 items-center
        hover:bg-white/5 
        ${!read ? "shadow-[inset_3px_0_0_0_rgba(255,127,80,0.4)] bg-white/[0.02]" : ""}        
        ${className}
      `}
    >
      {/* Type Badge */}
      <div className="flex items-center justify-center gap-2">
        <span
          className={`px-3 py-1 rounded-full text-xs font-anta border ${
            category === "Match"
              ? "border-orange/30 text-orange"
              : "border-pink-500/30 text-pink-400"
          }`}
        >
          {category}
        </span>
        <span className="w-2 h-2 flex-shrink-0">
          {!read && (
            <span className="block w-2 h-2 rounded-full bg-orange animate-pulse" title="Unread" />
          )}
        </span>
      </div>

      {/* From */}
      <div className="text-center">
        <span className="font-anta text-text/90">{senderUsername}</span>
      </div>

      {/* Message */}
      <div className="flex flex-col gap-1 text-center">
        <span className={`font-anta text-sm font-semibold ${typeColor}`}>
          {title}
        </span>
        <span className="text-text/70 text-sm">{message}</span>
        {/* Additional Info for Submission Results */}
        {submissionStatus && (
          <div className="flex items-center justify-center gap-2 mt-1">
            <span
              className={`text-xs font-anta px-2 py-0.5 rounded ${
                submissionStatus === "ACCEPTED"
                  ? "bg-green-500/20 text-green-400"
                  : submissionStatus === "WRONG_ANSWER"
                  ? "bg-red-500/20 text-red-400"
                  : "bg-yellow-500/20 text-yellow-400"
              }`}
            >
              {submissionStatus}
            </span>
            {passedCases !== undefined && totalCases !== undefined && (
              <span className="text-xs text-text/60">
                {passedCases}/{totalCases} cases
              </span>
            )}
          </div>
        )}

        {/* Match ID if applicable */}
        {matchId && (
          <span className="text-xs text-text/50 font-mono">
            Match #{matchId}
          </span>
        )}
      </div>

      {/* Time */}
      <div className="text-center">
        <span className="text-text/60 text-sm font-anta">
          {formatTimeAgo(createdAt)}
        </span>
      </div>

      {/* Status */}
      <div className="text-center">
        <span className={!read ? "text-orange text-xs font-anta" : "text-text/40 text-xs font-anta"}>
          {!read ? "Unread" : "Read"}
        </span>
      </div>
    </div>
  );
}