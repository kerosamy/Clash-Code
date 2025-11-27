import axios from "axios";
import type { ProblemTags } from "../enums/ProblemTags";

const API_BASE = "http://localhost:8080/problem";

export async function fetchProblems(page = 0, size = 20) {
  const response = await axios.get(`${API_BASE}/browse`, {
    params: { page, size },
  });

  return response.data; // returned Page<ProblemListDto>
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

  const response = await axios.post(`${API_BASE}/browse/filter`, payload, {
    params: { page, size },
  });

  return response.data; // returned Page<ProblemListDto>
}

export async function searchProblemsByTitle(
  keyword: string,
  page = 0,
  size = 20
) {
  const response = await axios.get(`${API_BASE}/search`, {
    params: { keyword, page, size },
  });

  return response.data; // returned Page<ProblemListDto>
}