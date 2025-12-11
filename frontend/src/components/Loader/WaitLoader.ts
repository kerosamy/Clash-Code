export const waitForLoader = (fetchTime: number): Promise<void> => {
  return new Promise(resolve => setTimeout(resolve, 2000 - (Date.now() - fetchTime)));
};