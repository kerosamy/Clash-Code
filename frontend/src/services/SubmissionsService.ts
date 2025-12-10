import { apiRequest } from "./api";

export interface SubmissionRequest {
  problemId: number;
  code: string;
  codeLanguage: string;
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
