import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/submissions';

export interface SubmissionRequest {
  userId: number;
  problemId: number;
  code: string;
  codeLanguage: string;
}

export interface SubmissionResponse {
  submissionStatus: string;
  submissionId: number;
  timeTaken: number;
  memoryTaken: number;
  submittedAt: string;
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
    userId: 1, // Hardcoded for now
    problemId,
    code,
    codeLanguage,
  };

  try {
    await axios.post(`${API_BASE_URL}/submit`, submissionRequest);
  } catch (error) {
    console.error('Failed to submit code:', error);
    throw error;
  }
}

export async function getUserSubmissions(userId: number): Promise<SubmissionResponse[]> {
  try {
    const response = await axios.get(`${API_BASE_URL}/user/${userId}`);
    return response.data.reverse(); 
  } catch (error) {
    console.error('Failed to fetch user submissions:', error);
    throw error;
  }
}

export async function getSubmissionStatus(submissionId: number): Promise<SubmissionResponse> {
  try {
    const response = await axios.get(`${API_BASE_URL}/status/${submissionId}`);
    return response.data;
  } catch (error) {
    console.error('Failed to fetch submission status:', error);
    throw error;
  }
}