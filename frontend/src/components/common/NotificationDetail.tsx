import { useState, useEffect } from "react";
import type { NotificationRowProps } from "./NotificationRow";
import { acceptMatchInvite, markNotificationAsRead } from "../../services/NotificationService";
import { useNavigate } from "react-router";

interface NotificationDetailProps {
  notification: NotificationRowProps;
  onReturn: () => void;
}

const getTypeColor = (type: string): string => {
  const typeColors: Record<string, string> = {
    MATCH_INVITE: "text-blue-400 border-blue-400/30 bg-blue-400/10",
    MATCH_STARTED: "text-green-400 border-green-400/30 bg-green-400/10",
    MATCH_ENDED: "text-purple-400 border-purple-400/30 bg-purple-400/10",
    SUBMISSION_RECEIVED: "text-yellow-400 border-yellow-400/30 bg-yellow-400/10",
    SUBMISSION_RESULT: "text-cyan-400 border-cyan-400/30 bg-cyan-400/10",
    OPPONENT_RESIGNED: "text-red-400 border-red-400/30 bg-red-400/10",
    FRIEND_REQUEST: "text-pink-400 border-pink-400/30 bg-pink-400/10",
    FRIEND_ACCEPTED: "text-emerald-400 border-emerald-400/30 bg-emerald-400/10",
  };
  return typeColors[type] || "text-text/70 border-white/10 bg-white/5";
};

const formatTypeLabel = (type: string): string => {
  return type.split("_").map(word => 
    word.charAt(0) + word.slice(1).toLowerCase()
  ).join(" ");
};

const formatFullDate = (timestamp: string): string => {
  const date = new Date(timestamp);
  return date.toLocaleString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

export default function NotificationDetail({ notification, onReturn }: NotificationDetailProps) {
  const typeColorClass = getTypeColor(notification.type);
  const [isAccepting, setIsAccepting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();

  const isMatchInvite = notification.type === "MATCH_INVITATION";
  const isFriendRequest = notification.type === "FRIEND_REQUEST_RECEIVED" || notification.type === "FRIEND_REQUEST_ACCEPTED";

  // Mark notification as read when component mounts
  useEffect(() => {
    const markAsRead = async () => {
      if (!notification.read) {
        try {
          await markNotificationAsRead(notification.id);
        } catch (err) {
          console.error("Failed to mark notification as read:", err);
        }
      }
    };

    markAsRead();
  }, [notification.id, notification.read]);

  const handleAcceptInvite = async () => {
    
    try {
      setIsAccepting(true);
      setError(null);
      await acceptMatchInvite(notification.id);
      navigate(`../play-game`, { replace: true });

      setSuccess(true);
    } catch (err) {
      console.error("Failed to accept match invite:", err);
      setError(err instanceof Error ? err.message : "Failed to accept invite");
      setIsAccepting(false);
    }
  };

  const handleRedirectToProfile = async () => {
    
    try {
      setError(null);
      navigate(`../profile/${notification.senderUsername}`, { replace: true });

      setSuccess(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to redirect to user profile");
      setIsAccepting(false);
    }
  };

  return (
    <div className="flex flex-col h-full p-8">
      {/* Header */}
      <div className="flex items-start justify-between mb-8">
        <div className="flex-1">
          <div className="flex items-center gap-3 mb-4">
            <span className={`px-4 py-2 rounded-xl text-sm font-anta border ${typeColorClass}`}>
              {formatTypeLabel(notification.type)}
            </span>
          </div>
          <h1 className="text-3xl font-anta text-text mb-2">
            {notification.title}
          </h1>
          <div className="flex items-center gap-4 text-text/60 text-sm font-anta">
            <span>From: <span className="text-text/90">{notification.senderUsername}</span></span>
            <span>•</span>
            <span>{formatFullDate(notification.createdAt)}</span>
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 mb-8">
        <div className="bg-container rounded-xl border border-white/5 p-6">
          <p className="text-text/80 text-base leading-relaxed font-anta">
            {notification.message}
          </p>

          {/* Match Information */}
          {notification.matchId && (
            <div className="mt-6 pt-6 border-t border-white/5">
              <h3 className="text-orange font-anta text-sm mb-3">Match Details</h3>
              <div className="flex items-center gap-2">
                <span className="text-text/60 text-sm">Match ID:</span>
                <span className="text-text font-mono text-sm">#{notification.matchId}</span>
              </div>
            </div>
          )}

          {/* Submission Results */}
          {notification.submissionStatus && (
            <div className="mt-6 pt-6 border-t border-white/5">
              <h3 className="text-orange font-anta text-sm mb-3">Submission Results</h3>
              <div className="flex flex-col gap-3">
                <div className="flex items-center gap-2">
                  <span className="text-text/60 text-sm">Status:</span>
                  <span
                    className={`text-xs font-anta px-3 py-1 rounded-lg ${
                      notification.submissionStatus === "ACCEPTED"
                        ? "bg-green-500/20 text-green-400 border border-green-500/30"
                        : notification.submissionStatus === "WRONG_ANSWER"
                        ? "bg-red-500/20 text-red-400 border border-red-500/30"
                        : "bg-yellow-500/20 text-yellow-400 border border-yellow-500/30"
                    }`}
                  >
                    {notification.submissionStatus}
                  </span>
                </div>
                {notification.passedCases !== undefined && notification.totalCases !== undefined && (
                  <div className="flex items-center gap-2">
                    <span className="text-text/60 text-sm">Test Cases:</span>
                    <span className="text-text text-sm">
                      {notification.passedCases} / {notification.totalCases} passed
                    </span>
                    <div className="flex-1 max-w-xs">
                      <div className="h-2 bg-white/5 rounded-full overflow-hidden">
                        <div
                          className={`h-full rounded-full ${
                            notification.submissionStatus === "ACCEPTED"
                              ? "bg-green-500"
                              : "bg-orange"
                          }`}
                          style={{
                            width: `${(notification.passedCases / notification.totalCases) * 100}%`
                          }}
                        />
                      </div>
                    </div>
                  </div>
                )}
              </div>
            </div>
          )}

          {/* Success Message */}
          {success && (
            <div className="mt-6 p-4 bg-green-500/10 border border-green-500/30 rounded-lg">
              <p className="text-green-400 text-sm font-anta">✓ Match invite accepted successfully!</p>
            </div>
          )}

          {/* Error Message */}
          {error && (
            <div className="mt-6 p-4 bg-red-500/10 border border-red-500/30 rounded-lg">
              <p className="text-red-400 text-sm font-anta">{error}</p>
            </div>
          )}
        </div>
      </div>

      {/* Action Buttons */}
      <div className="flex justify-center gap-4">
        {isMatchInvite && !success && (
          <button
            type="button"
            onClick={handleAcceptInvite}
            disabled={isAccepting}
            className="
              flex items-center justify-center
              border border-emerald-500/30 bg-emerald-500/5 text-emerald-500
              hover:bg-emerald-500 hover:text-white hover:border-emerald-500 hover:shadow-[0_0_15px_rgba(16,185,129,0.4)]
              px-10 py-3 rounded-full 
              font-anta text-sm uppercase tracking-widest 
              transition-all duration-300
              disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:bg-emerald-500/5 disabled:hover:text-emerald-500 disabled:hover:shadow-none
            "
          >
            {isAccepting ? (
              <>
                <span className="animate-spin mr-2">⏳</span>
                Accepting...
              </>
            ) : (
              "Accept Invite"
            )}
          </button>
        )}

        {isFriendRequest && (
          <button
            type="button"
            onClick={handleRedirectToProfile}
            className="
              flex items-center justify-center
              border border-emerald-500/30 bg-emerald-500/5 text-emerald-500
              hover:bg-emerald-500 hover:text-white hover:border-emerald-500 hover:shadow-[0_0_15px_rgba(16,185,129,0.4)]
              px-10 py-3 rounded-full 
              font-anta text-sm uppercase tracking-widest 
              transition-all duration-300
              disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:bg-emerald-500/5 disabled:hover:text-emerald-500 disabled:hover:shadow-none
            "
          >
            {notification.senderUsername}'s Profile
          </button>
        )}
        
        <button
          type="button"
          onClick={onReturn}
          className="
            flex items-center justify-center
            border border-orange/30 bg-orange/5 text-orange
            hover:bg-orange hover:text-white hover:border-orange hover:shadow-[0_0_15px_rgba(249,115,22,0.4)]
            px-10 py-3 rounded-full 
            font-anta text-sm uppercase tracking-widest 
            transition-all duration-300
            disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:bg-orange/5 disabled:hover:text-orange disabled:hover:shadow-none
          "
        >
          Return to Notifications
        </button>
      </div>
    </div>
  );
}