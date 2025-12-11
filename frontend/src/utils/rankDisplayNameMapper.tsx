export const rankDisplayNames: Record<string, string> = {
    BRONZE: "Bronze",
    SILVER: "Silver",
    GOLD: "Gold",
    DIAMOND: "Diamond",
    MASTER: "Master",
    CHAMPION: "Champion",
    LEGEND: "Legend",
};


export function getRankDisplayName(rank: string): string {
    return rankDisplayNames[rank] || rank;
}