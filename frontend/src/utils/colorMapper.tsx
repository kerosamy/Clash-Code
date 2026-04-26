const RANK_CONFIG = [
  { name: "BRONZE", color: "#CD7F32" },
  { name: "SILVER", color: "#C0C0C0" },
  { name: "GOLD", color: "#FFD700" },
  { name: "DIAMOND", color: "#00FFFF" },
  { name: "MASTER", color: "#8A2BE2" },
  { name: "CHAMPION", color: "#4169E1" },
  { name: "LEGEND", color: "#80FFA1" },
] as const;

const RATING_PER_RANK = 300;

export const getRankName = (rating: number): string => {
  let index = Math.floor(rating / RATING_PER_RANK);
  if (index >= RANK_CONFIG.length) {
    index = RANK_CONFIG.length - 1;
  }
  if (index < 0) index = 0;
  return RANK_CONFIG[index].name;
};

export const getRankColor = (rating: number): string => {
  const index = Math.min(
    Math.floor(Math.max(0, rating) / RATING_PER_RANK),
    RANK_CONFIG.length - 1
  );
  return RANK_CONFIG[index].color;
};

export const rankColors = Object.fromEntries(
  RANK_CONFIG.map(r => [r.name, r.color])
);

export const enumColors: Record<string, string> = {
  IMPLEMENTATION: "#6B7280",
  MATH: "#10B981",
  GREEDY: "#EF4444",
  TWO_POINTERS: "#F97316",
  STRINGS: "#EC4899",
  SORTING: "#F59E0B",
  DATA_STRUCTURES: "#6366F1",
  GRAPH_THEORY: "#3B82F6",
  DP: "#22D3EE",
  BRUTE_FORCE: "#8B5CF6",
  BINARY_SEARCH: "#14B8A6",
  TREES: "#A855F7",
  DFS_AND_SIMILAR: "#4ADE80",
  BFS: "#0EA5E9",
  COMBINATORICS: "#D97706",
  GEOMETRY: "#F87171",
  HASHING: "#9CA3AF",
  DSU: "#2563EB",
  HEAPS: "#F43F5E",
};
