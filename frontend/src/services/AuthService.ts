import axios from "axios";

const API_URL = "http://localhost:8080/auth";

// --- Types ---

// Backend Error Shape
interface BackendErrorResponse {
  error: string;
  timestamp: string;
}

// Register Types
export interface RegisterRequest {
  username: string;
  email: string; // Note: Backend maps this to 'email' field
  password: string;
  recoveryQuestion: string;
  recoveryAnswer: string;
}

export interface UserResponse {
  id?: number;
  username: string;
  email: string;
}

// Login Types
export interface LoginRequest {
  email: string; // The backend DTO expects this field to be named 'email'
  password: string;
}

export interface LoginResponse {
  token: string;
  expiresIn: number;
}

// --- API Functions ---

export const registerUser = async (data: RegisterRequest) => {
  const response = await axios.post(`${API_URL}/signup`, data);
  return response.data;  // Contains { token, expiresIn }
};


export const loginUser = async (credentials: LoginRequest): Promise<LoginResponse> => {
  try {
    const response = await axios.post<LoginResponse>(`${API_URL}/login`, credentials);
    return response.data;
  } catch (err) {
    handleApiError(err);
    throw err;
  }
};

// --- Helper for Consistent Error Handling ---
const handleApiError = (err: unknown) => {
  if (axios.isAxiosError(err) && err.response) {
    // Cast to backend error shape
    const errorData = err.response.data as BackendErrorResponse;
    // Extract 'error' field (e.g., "User not found")
    const errorMessage = errorData.error || "An unexpected error occurred";
    throw new Error(errorMessage);
  } else {
    throw new Error("Network error or server is unreachable");
  }
};