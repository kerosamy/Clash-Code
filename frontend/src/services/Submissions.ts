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
  timeTaken: number;
  memoryTaken: number;
  submittedAt: string;
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
    const response = await axios.get(`${API_BASE_URL}/${userId}`);
    return response.data.reverse(); 
  } catch (error) {
    console.error('Failed to fetch user submissions:', error);
    throw error;
  }
}