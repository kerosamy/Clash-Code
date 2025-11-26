import { ProblemTags } from "../enums/ProblemTags";

export const ProblemTagDisplayMap: Record<ProblemTags, string> = {
    [ProblemTags.IMPLEMENTATION]: "Implementation",
    [ProblemTags.MATH]: "Math",
    [ProblemTags.GREEDY]: "Greedy",
    [ProblemTags.TWO_POINTERS]: "Two pointers",
    [ProblemTags.STRINGS]: "Strings",
    [ProblemTags.SORTING]: "Sorting",
    [ProblemTags.DATA_STRUCTURES]: "Data structures",
    [ProblemTags.GRAPH_THEORY]: "Graph theory",
    [ProblemTags.DP]: "Dp",
    [ProblemTags.BRUTE_FORCE]: "Brute force",
    [ProblemTags.BINARY_SEARCH]: "Binary search",
    [ProblemTags.TREES]: "Trees",
    [ProblemTags.DFS_AND_SIMILAR]: "Dfs and similar",
    [ProblemTags.BFS]: "Bfs",
    [ProblemTags.COMBINATORICS]: "Combinatorics",
    [ProblemTags.GEOMETRY]: "Geometry",
    [ProblemTags.HASHING]: "Hashing",
    [ProblemTags.DSU]: "Dsu",
    [ProblemTags.HEAPS]: "Heaps",
  };

export const TagsFrontendValues: string[] = Object.values(ProblemTags).map(
(tag) => ProblemTagDisplayMap[tag]
);