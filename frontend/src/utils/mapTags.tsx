import { ProblemTags } from "../enums/ProblemTags";

export const EnumToFrontendTagMap: Record<ProblemTags, string> = {
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

export const FrontendToEnumTagMap: Record<string, ProblemTags> = {
    "Implementation": ProblemTags.IMPLEMENTATION,
    "Math": ProblemTags.MATH,
    "Greedy": ProblemTags.GREEDY,
    "Two pointers": ProblemTags.TWO_POINTERS,
    "Strings": ProblemTags.STRINGS,
    "Sorting": ProblemTags.SORTING,
    "Data structures": ProblemTags.DATA_STRUCTURES,
    "Graph theory": ProblemTags.GRAPH_THEORY,
    "Dp": ProblemTags.DP,
    "Brute force": ProblemTags.BRUTE_FORCE,
    "Binary search": ProblemTags.BINARY_SEARCH,
    "Trees": ProblemTags.TREES,
    "Dfs and similar": ProblemTags.DFS_AND_SIMILAR,
    "Bfs": ProblemTags.BFS,
    "Combinatorics": ProblemTags.COMBINATORICS,
    "Geometry": ProblemTags.GEOMETRY,
    "Hashing": ProblemTags.HASHING,
    "Dsu": ProblemTags.DSU,
    "Heaps": ProblemTags.HEAPS,
  };
  

export const TagsFrontendValues: string[] = Object.values(ProblemTags).map(
(tag) => EnumToFrontendTagMap[tag]
);

export function mapFrontendTagsToEnum(selectedTags: string[]): ProblemTags[] {
  return selectedTags
      .map(tag => FrontendToEnumTagMap[tag])
      .filter(Boolean) as ProblemTags[];
}