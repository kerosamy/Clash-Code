export const setActiveMatch = (matchId: string): void => {
  sessionStorage.setItem('activeMatchId', matchId);
};

export const getActiveMatch = (): string | null => {
  return sessionStorage.getItem('activeMatchId');
};

export const clearActiveMatch = (): void => {
  sessionStorage.removeItem('activeMatchId');
};

export const hasActiveMatch = (): boolean => {
  return sessionStorage.getItem('activeMatchId') !== null;
};