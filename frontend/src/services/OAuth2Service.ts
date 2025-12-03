import axios from "axios";

const API_BASE = "http://localhost:8080/auth";

export const authService = {
  getGoogleToken: async () => {
    const res = await axios.get(`${API_BASE}/OAuthCallback`, { withCredentials: true });
    return res.data; // { token, expiresAt }
  },

  // Check if user exists using token
  getAuthenticatedUser: async () => {
    const token = localStorage.getItem("token");
    const res = await axios.get(`${API_BASE}/me`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    return res.data; // user object
  },

  completeRegistration: async (username: string) => {
    const res = await axios.post(
      `${API_BASE}/GoogleSignUp/completeRegistration`,
      { username },
      { withCredentials: true }
    );
    localStorage.setItem("token", res.data.token);
    return res.data;
  }
};
