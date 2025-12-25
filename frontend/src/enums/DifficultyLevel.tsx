export const DifficultyLevel = {
  MIN: 100,
  EASY_MAX: 900,
  MEDIUM_MAX: 1600,
  HARD_MAX: 2000,
} as const;

export type DifficultyLevel = typeof DifficultyLevel[keyof typeof DifficultyLevel];