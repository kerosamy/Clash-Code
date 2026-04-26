export const getTypeColor = (type: string): string => {
  const typeColors: Record<string, string> = {
    MATCH_INVITE: "text-blue-400",
    MATCH_STARTED: "text-green-400",
    MATCH_ENDED: "text-purple-400",
    SUBMISSION_RECEIVED: "text-yellow-400",
    SUBMISSION_RESULT: "text-cyan-400",
    OPPONENT_RESIGNED: "text-red-400",
    FRIEND_REQUEST: "text-pink-400",
    FRIEND_ACCEPTED: "text-emerald-400",
  };
  return typeColors[type] || "text-text/70";
};

export const getTypeColorForDetail = (type: string): string => {
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

export const getTypeBadge = (type: string): string => {
  const badges: Record<string, string> = {
    MATCH_INVITE: "Match",
    MATCH_STARTED: "Match",
    MATCH_ENDED: "Match",
    SUBMISSION_RECEIVED: "Match",
    SUBMISSION_RESULT: "Match",
    OPPONENT_RESIGNED: "Match",
    FRIEND_REQUEST: "Friend",
    FRIEND_ACCEPTED: "Friend",
  };
  return badges[type] || "Other";
};

export const formatTypeLabel = (type: string): string => {
  return type.split("_").map(word => 
    word.charAt(0) + word.slice(1).toLowerCase()
  ).join(" ");
};

export const formatFullDate = (timestamp: string): string => {
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

export const formatTimeAgo = (timestamp: string): string => {
  const now = new Date();
  const time = new Date(timestamp);
  const diff = Math.floor((now.getTime() - time.getTime()) / 1000);

  if (diff < 60) return "Just now";
  if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
  if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;
  if (diff < 604800) return `${Math.floor(diff / 86400)}d ago`;
  return time.toLocaleDateString();
};
