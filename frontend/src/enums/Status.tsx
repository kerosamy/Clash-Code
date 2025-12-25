export const Status = {
  Solved: "solved",
  Attempted: "attempted",
  Unsolved: "unsolved",
} as const;

export type Status = typeof Status[keyof typeof Status];