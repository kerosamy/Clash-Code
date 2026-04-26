export const formatTime = (isoString?: string) => {
  if (!isoString) return "--:--";
  return new Date(isoString).toLocaleTimeString("en-GB", {
    hour12: false,
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  });
};

export const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  return {
    date: date.toLocaleDateString('en-US', { month: 'short', day: '2-digit', year: 'numeric' }),
    time: date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  };
};

export const getStatusConfig = (rank: number) => {
  if (rank === 1) return { text: "WON", color: "text-winGreen" };
  if (rank === 0) return { text: "DRAW", color: "text-orange" };
  return { text: "LOST", color: "text-loseRed" };
};
