import { RANKS } from '../enums/Ranks';
import { rankColors } from './colorMapper';

export const RATING_PER_RANK = 300;

export interface NextRankInfo {
  nextRate: number;
  nextRank: RANKS | "MAX" | null;
  nextRankColor: string;
  isMaxRank: boolean;
}

export function calculateNextRate(currentRate: number): NextRankInfo {

  const rankArray = Object.values(RANKS);

  const currentRankIndex = Math.floor(currentRate / RATING_PER_RANK);

  if (currentRankIndex >= rankArray.length - 1) {
    return {
      nextRate: 0,
      nextRank: "MAX",
      nextRankColor: rankColors.LEGEND,
      isMaxRank: true
    };
  }

  const nextRankIndex = currentRankIndex + 1;
  const nextRankThreshold = nextRankIndex * RATING_PER_RANK;
  const remainingPoints = nextRankThreshold - currentRate;
  const nextRank = rankArray[nextRankIndex] as RANKS;
  const nextRankColor = rankColors[nextRank];

  return {
    nextRate: remainingPoints,
    nextRank,
    nextRankColor,
    isMaxRank: false
  };
}

export function getCurrentRank(rate: number): RANKS {
  const rankArray = Object.values(RANKS);
  let index = Math.floor(rate / RATING_PER_RANK);
  if (index >= rankArray.length) {
    index = rankArray.length - 1;
  }
  return rankArray[index] as RANKS;
}

export function getRankColor(rank: RANKS | "MAX"): string {
  if (rank === "MAX") return rankColors.LEGEND;
  return rankColors[rank] || "#6B7280";
}