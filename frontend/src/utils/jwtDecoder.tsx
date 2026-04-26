import { UserRole } from "../enums/UserRole";


export interface DecodedToken {
  role: string;
  username: string;
  sub: string; 
  iat: number;
  exp: number;
}

export function decodeToken(token: string): DecodedToken | null {
  try {
    if (!token) return null;

    const base64Url = token.split(".")[1];
    if (!base64Url) return null;

    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    );
    console.log(JSON.parse(jsonPayload))
    return JSON.parse(jsonPayload);
  } catch (err) {
    console.error("Failed to decode token:", err);
    return null;
  }
}


export function getStoredToken(): string | null {
  return localStorage.getItem("token");
}

export function getUserRole(): UserRole | null {
  const token = getStoredToken();
  if (!token) return null;

  const decoded = decodeToken(token);
  if (!decoded?.role) return null;

  return UserRole[decoded.role as keyof typeof UserRole] ?? null;
}

export function getUsername(): string | null {
  const token = getStoredToken();
  if (!token) return null;

  const decoded = decodeToken(token);
  return decoded?.username ?? null;
}

export function getTokenExpiry(): Date | null {
  const token = getStoredToken();
  if (!token) return null;

  const decoded = decodeToken(token);
  if (!decoded?.exp) return null;

  return new Date(decoded.exp * 1000);   // exp is in seconds, convert to milliseconds
}

export function isTokenExpired(): boolean {
  const expiry = getTokenExpiry();
  if (!expiry) return true;

  return expiry.getTime() < Date.now();
}

export function getEmail(): string | null {
  const token = getStoredToken();
  if (!token) return null;

  const decoded = decodeToken(token);
  return decoded?.sub ?? null;
}

export function clearToken(): void {
  localStorage.removeItem("token");
}