import axios from "axios";
import type { AxiosRequestConfig, AxiosResponse, AxiosError } from "axios";

const API_BASE = "http://localhost:8080";

const api = axios.create({
  baseURL: API_BASE,
  timeout: 10000,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token && token !== "undefined" && config.headers && !config.url?.includes("/auth")){ 
    config.headers.Authorization = `Bearer ${token}`; 
  }
  return config;
});

interface ErrorResponse {
  message?: string;
  statusCode?: number;
  timestamp?: string;
  [key: string]: unknown;
}

const handleApiError = (error: AxiosError<ErrorResponse>): never => {
  if (error.response) {
    const data = error.response.data;
    const message = data?.message ?? `API Error: ${error.response.status}`;
    throw new Error(message);
  }
  else if (error.request) {
    throw new Error("Network error, please try again later");
  }
  else {
    throw new Error(error.message);
  }
};

export const apiRequest = async <T>(config: AxiosRequestConfig): Promise<T> => {
  try {
    const response: AxiosResponse<T> = await api.request<T>(config);
    return response.data;
  } 
  catch (err) {
    throw handleApiError(err as AxiosError<ErrorResponse>);
  }
};

export default api;