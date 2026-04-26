import { DifficultyLevel } from "../enums/DifficultyLevel";

export function getDifficultyColor(difficulty: number): string {
  if (difficulty <= DifficultyLevel.EASY_MAX) return "text-difficultyEasy";
  if (difficulty <= DifficultyLevel.MEDIUM_MAX) return "text-difficultyMedium";
  return "text-difficultyHard";
}
