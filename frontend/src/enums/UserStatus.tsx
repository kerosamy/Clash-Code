export const UserStatus = {
  ONLINE: "ONLINE",
  OFFLINE: "OFFLINE",
  IN_MATCH: "IN_MATCH",
} as const;

export type UserStatus = typeof UserStatus[keyof typeof UserStatus];
