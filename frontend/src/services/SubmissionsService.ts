import { apiRequest } from "./api";

export interface SubmissionRequest {
  problemId: number;
  code: string;
  codeLanguage: string;
  matchId: number | null;
}

export interface SubmissionResponse {
  submissionStatus: string;
  submissionId: number;
  problemId: number;
  timeTaken: number;
  memoryTaken: number;
  submittedAt: string;
  problemTitle: string;
  numberOfPassedTestCases: number;
  numberOfTotalTestCases: number;
  numberOfCurrentTestCase: number;
  matchId?: number | null;
}

export interface SubmissionDetailsDto {
  submissionLang: string;
  submissionCode: string;
  problemTitle: string;
  username: string;
  submissionStatus: string;
}



export async function submitCode(
  problemId: number,
  code: string,
  codeLanguage: string
): Promise<void> {
  const body: SubmissionRequest = {
    problemId,
    code,
    codeLanguage,
    matchId: null, 
  };

  return apiRequest<void>({
    method: "POST",
    url: "/submissions/submit",
    data: body,
  });
}


export async function getUserSubmissions(): Promise<SubmissionResponse[]> {
  const response = await apiRequest<SubmissionResponse[]>({
    method: "GET",
    url: "/submissions/my-submissions",
  });
  
  return response;
}


export async function getSubmissionStatus(
  submissionId: number
): Promise<SubmissionResponse> {
  return apiRequest<SubmissionResponse>({
    method: "GET",
    url: `/submissions/status/${submissionId}`,
  });
}

export async function getProblemTitle(problemId: number): Promise<string> {
  return apiRequest<string>({
    method: "GET",
    url: `/submissions/problem-title/${problemId}`,
  });
}

export async function getSubmissionDetails(
  submissionId: number
): Promise<SubmissionDetailsDto> {
  return apiRequest<SubmissionDetailsDto>({
    method: "GET",
    url: `/submissions/details/${submissionId}`,
  });
}