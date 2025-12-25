export const SubmissionStatus = {
  ACCEPTED: "ACCEPTED",
  WAITING: "WAITING",
  WRONG_ANSWER: "WRONG_ANSWER",
  TIME_LIMIT_EXCEEDED: "TIME_LIMIT_EXCEEDED",
  MEMORY_LIMIT_EXCEEDED: "MEMORY_LIMIT_EXCEEDED",
  COMPILATION_ERROR: "COMPILATION_ERROR",
  RUNNING_ON_TEST: "RUNNING_ON_TEST",
} as const;

export type SubmissionStatus = typeof SubmissionStatus[keyof typeof SubmissionStatus];
