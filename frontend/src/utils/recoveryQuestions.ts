// src/constants/recoveryQuestions.ts

export interface RecoveryOption {
  value: string;
  label: string;
}

export const RECOVERY_OPTIONS: RecoveryOption[] = [
  { value: "MOTHERS_FRIEND", label: "What is your mother's friend name?" },
  { value: "FIRST_PET", label: "What was the name of your first pet?" },
  { value: "FIRST_CITY", label: "What was the first city you lived in?" },
  { value: "FAVORITE_MOVIE", label: "What is your favorite movie?" },
];

// Create a map for easy lookup from enum value to display text
export const RECOVERY_QUESTIONS_MAP: { [key: string]: string } = 
  RECOVERY_OPTIONS.reduce((acc, option) => {
    acc[option.value] = option.label;
    return acc;
  }, {} as { [key: string]: string });

// Helper function to get display text for a recovery question enum
export const getQuestionDisplayText = (questionKey: string): string => {
  return RECOVERY_QUESTIONS_MAP[questionKey] || questionKey;
};