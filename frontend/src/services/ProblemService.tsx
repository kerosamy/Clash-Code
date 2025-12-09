import type { ProblemTags } from "../enums/ProblemTags";
import { apiRequest } from "./api";

// Pagination wrapper 
interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number; // current page index
  first: boolean;
  last: boolean;
}

interface ProblemListDto {
  id: number;
  title: string;
  rate: number;
  tags: ProblemTags[];
}

interface ProblemDto {
  id: number;
  title: string;
  description: string;
  rate: number;
  tags: ProblemTags[];
}

export async function fetchProblems(
  page = 0,
  size = 20
): Promise<Page<ProblemListDto>> {
  return apiRequest<Page<ProblemListDto>>({
    method: "GET",
    url: "/problem/browse",
    params: { page, size },
  });
}

export async function fetchFilteredProblems(
  tags: ProblemTags[] = [],
  minRate?: number,
  maxRate?: number,
  page = 0,
  size = 20
): Promise<Page<ProblemListDto>> {
  const payload: Record<string, unknown> = { tags };
  if (minRate !== undefined) payload.minRate = minRate;
  if (maxRate !== undefined) payload.maxRate = maxRate;

  return apiRequest<Page<ProblemListDto>>({
    method: "POST",
    url: "/problem/browse/filter",
    data: payload,
    params: { page, size },
  });
}

export async function searchProblemsByTitle(
  keyword: string,
  page = 0,
  size = 20
): Promise<Page<ProblemListDto>> {
  return apiRequest<Page<ProblemListDto>>({
    method: "GET",
    url: "/problem/search",
    params: { keyword, page, size },
  });
}

export async function fetchProblemById(id: number): Promise<ProblemDto> {
  return apiRequest<ProblemDto>({
    method: "GET",
    url: `/problem/${id}`,
  });
}
