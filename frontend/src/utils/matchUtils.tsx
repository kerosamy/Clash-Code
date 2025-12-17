export const formatTime = (isoString?: string) => {
  if (!isoString) return "--:--";
  return new Date(isoString).toLocaleTimeString("en-GB", {
    hour12: false,
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  });
};

// Checks if submission is finalized (not running/test/processing)
export const isFinalStatus = (status?: string) => {
  if (!status) return false;
  const s = status.toUpperCase();
  return !["RUNNING", "TEST", "PROCESSING"].some(keyword => s.includes(keyword));
};
