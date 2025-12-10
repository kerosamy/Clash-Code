import { apiRequest } from "./api";

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

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

export const registerUser = async (data: RegisterRequest): Promise<LoginResponse> => {
  const response = await apiRequest<LoginResponse>({
    method: "POST",
    url: `/auth/signup`,
    data,
  });
  localStorage.setItem("token", response.token);
  return response;
};

export const loginUser = async (credentials: LoginRequest): Promise<LoginResponse> => {
  const response = await apiRequest<LoginResponse>({
    method: "POST",
    url: `/auth/login`,
    data: credentials,
  });
  localStorage.setItem("token", response.token);
  return response;
};

export const getGoogleToken = async () => {
  return apiRequest<{ token: string }>({
    method: "GET",
    url: `/auth/OAuthCallback`,
    withCredentials: true,
  });
};

export const getAuthenticatedUser = async (): Promise<UserResponse> => {
  return apiRequest<UserResponse>({
    method: "GET",
    url: `/auth/me`,
  });
};

export const completeRegistration = async (username: string) => {
  const response = await apiRequest<LoginResponse>({
    method: "POST",
    url: `/auth/GoogleSignUp/completeRegistration`,
    data: { username },
    withCredentials: true,
  });

  localStorage.setItem("token", response.token);
  return response;
};

export const logout = () => {
  localStorage.removeItem("token");
};