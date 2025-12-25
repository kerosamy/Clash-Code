export const RANKS = {
  BRONZE: "BRONZE",
  SILVER: "SILVER",
  GOLD: "GOLD",
  DIAMOND: "DIAMOND",
  MASTER: "MASTER",
  CHAMPION: "CHAMPION",
  LEGEND: "LEGEND",
} as const;

export type RANKS = typeof RANKS[keyof typeof RANKS];
