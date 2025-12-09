import axios from "axios";

const API_BASE = "http://localhost:8080/auth";

// --- Types ---

// Backend Error Shape
interface BackendErrorResponse {
  error: string;
  timestamp: string;
}

// Register Types
export interface RegisterRequest {
  username: string;
  email: string;
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
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  expiresIn: number;
}

// --- API Functions ---

export const registerUser = async (data: RegisterRequest) => {
  const response = await axios.post(`${API_BASE}/signup`, data);
  return response.data;  // Contains { token, expiresIn, username }
};

export const loginUser = async (credentials: LoginRequest): Promise<LoginResponse> => {
  try {
    const response = await axios.post<LoginResponse>(`${API_BASE}/login`, credentials);
    return response.data;
  } catch (err) {
    handleApiError(err);
    throw err;
  }
};

export const getGoogleToken = async () => {
  try {
    const response = await axios.get(`${API_BASE}/OAuthCallback`, { withCredentials: true });
    return response.data;
  } catch (err) {
    handleApiError(err);
    throw err;
  }
};

export const getAuthenticatedUser = async () => {
    const token = localStorage.getItem("token");
    try {
    const response = await axios.get(`${API_BASE}/me`,
      { headers: { Authorization: ` Bearer ${token}` } }
    );
    return response.data;
  } catch (err) {
    handleApiError(err);
    throw err;
  }
};

export const completeRegistration = async (username: string) => {
  try {
    const response = await axios.post(
      `${API_BASE}/GoogleSignUp/completeRegistration`,
      { username },
      { withCredentials: true }
    );
    localStorage.setItem("token", response.data.token);
    return response.data;
  } catch (err) {
    handleApiError(err);
    throw err;
  }
};

// --- Helper for Consistent Error Handling ---
const handleApiError = (err: unknown) => {
  if (axios.isAxiosError(err) && err.response) {
    const errorData = err.response.data as BackendErrorResponse;
    const errorMessage = errorData.error || "An unexpected error occurred";
    throw new Error(errorMessage);
  } else {
    throw new Error("Network error or server is unreachable");
  }
};