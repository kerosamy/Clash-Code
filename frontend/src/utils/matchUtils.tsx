export const formatTime = (isoString?: string) => {
  if (!isoString) return "--:--";
  return new Date(isoString).toLocaleTimeString("en-GB", {
    hour12: false,
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  });
};
