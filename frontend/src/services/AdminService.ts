import { apiRequest } from "./api";

export interface Problem {
  id: number;
  name: string;
  author?: string | null;
  status: "PENDING_APPROVAL" | "APPROVED" | "REJECTED";
}

export interface PaginatedProblems {
  content: Problem[];
  totalPages: number;
  totalElements: number;
  page: number;
  size: number;
}

export const fetchPendingProblems = async (
  page = 0,
  size = 20
): Promise<PaginatedProblems> => {
  return apiRequest<PaginatedProblems>({
    method: "GET",
    url: `/admin/problems/pending?page=${page}&size=${size}`,
  });
};

export const approveProblem = async (id: number): Promise<void> => {
  return apiRequest<void>({
    method: "POST",
    url: `/admin/problems/${id}/accept`,
  });
};

export const rejectProblem = async (id: number, note: string): Promise<void> => {
  return apiRequest<void>({
    method: "POST",
    url: `/admin/problems/${id}/reject`,
    data: note,
    headers: {
      "Content-Type": "text/plain",
    },
  });
};


export const fetchAIReview = async (problemId: number): Promise<string> => {
  return apiRequest<string>({
    method: "POST",
    url: `/ai-review/${problemId}`,
  });
};