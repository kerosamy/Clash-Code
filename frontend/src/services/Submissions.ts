import api from './api';


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
  const submissionRequest: SubmissionRequest = {
    problemId,
    code,
    codeLanguage,
  };

  try {
    await api.post(`submissions/submit`, submissionRequest);
  } catch (error) {
    console.error('Failed to submit code:', error);
    throw error;
  }
}

export async function getUserSubmissions(): Promise<SubmissionResponse[]> {
  try {
    const response = await api.get(`submissions/my-submissions`);
    return response.data.reverse(); 
  } catch (error) {
    console.error('Failed to fetch user submissions:', error);
    throw error;
  }
}

export async function getSubmissionStatus(submissionId: number): Promise<SubmissionResponse> {
  try {
    const response = await api.get(`submissions/status/${submissionId}`);
    return response.data;
  } catch (error) {
    console.error('Failed to fetch submission status:', error);
    throw error;
  }
}