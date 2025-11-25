import axios from "axios";

const API_BASE = "http://localhost:8080/users";

export const authService = {
  getGoogleUser: async () => {
    const res = await axios.get(
      `${API_BASE}/GoogleSignUp`,
      { withCredentials: true });
    return res.data;
  },

  completeRegistration: async (username: string) => {
    const res = await axios.post(
      `${API_BASE}/GoogleSignUp/completeRegistration`,
      { username },
      { withCredentials: true }
    );
    return res.data; 
  },

};
