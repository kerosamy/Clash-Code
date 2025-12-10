import { UserRole } from "../enums/UserRole";


export interface DecodedToken {
  role: string;
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

    return JSON.parse(jsonPayload);
  } catch (err) {
    console.error("Failed to decode token:", err);
    return null;
  }
}

export function getUserRole(): UserRole | null {
  const token = localStorage.getItem("token");
  if (!token) return null;

  const decoded = decodeToken(token);
  if (!decoded?.role) return null;

  return UserRole[decoded.role as keyof typeof UserRole] ?? null;
}