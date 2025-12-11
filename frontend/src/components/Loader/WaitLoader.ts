export const waitForLoader = (fetchTime: number): Promise<void> => {
  return new Promise(resolve => setTimeout(resolve, 1000 - (Date.now() - fetchTime)));
};