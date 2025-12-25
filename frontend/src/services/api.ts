import axios from "axios";
import type { AxiosRequestConfig, AxiosResponse, AxiosError } from "axios";



export const API_BASE = "https://fugally-nonrepatriable-belle.ngrok-free.dev";
//https://fugally-nonrepatriable-belle.ngrok-free.dev
//http://localhost:8080

const api = axios.create({
  baseURL: API_BASE,
  timeout: 10000,
  headers: {
    'ngrok-skip-browser-warning': 'true' // Add this!
  }
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
  console.error("API Call Failed:", error);

 if (error.response) {
    const data = error.response.data;

    if (typeof data === "string") {
      throw data;
    }

    if (typeof data === "object" && data !== null) {
      if (typeof data.error === "string") {
        throw data.error;
      }

      if (typeof data.message === "string") {
        throw data.message;
      }
    }

    throw "Unknown server error";
  }

  if (error.request) {
    throw "Network error. Server is unreachable.";
  }

  throw error.message;
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
