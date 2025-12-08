import type { ProblemTags } from "../enums/ProblemTags";
import api from "../services/api";

export async function fetchProblems(page = 0, size = 20) {
  const response = await api.get("/problem/browse", {
    params: { page, size },
  });

  return response.data; // Page<ProblemListDto>
}

export async function fetchFilteredProblems(
  tags: ProblemTags[] = [],
  minRate: number | null = null,
  maxRate: number | null = null,
  page = 0,
  size = 20
) {
  const payload = {
    tags,
    minRate,
    maxRate,
  };

  const response = await api.post(`problem/browse/filter`, payload, {
    params: { page, size },});

  return response.data; // returned Page<ProblemListDto>
}

export async function searchProblemsByTitle(
  keyword: string,
  page = 0,
  size = 20
) {
  const response = await api.get(`problem/search`, {
    params: { keyword, page, size },
  });

  return response.data; // returned Page<ProblemListDto>
}

export async function fetchProblemById(id: number) {
  try {
    const response = await api.get(`problem/${id}`
    );
    return response.data; // returned ProblemDto
  } catch (error) {
    console.error("Error fetching problem:", error);
    throw error;
  }
}