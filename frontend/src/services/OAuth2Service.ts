import axios from "axios";

const API_BASE = "http://localhost:8080/users";

export const authService = {
  getGoogleUser: async () => {
    const res = await axios.get(
      `${API_BASE}/OAuthCallback`,
      { withCredentials: true });
      console.log(res.data);
    return res.data;
  },

  completeRegistration: async (username: string) => {
    const res = await axios.post(
      `${API_BASE}/GoogleSignUp/completeRegistration`,
      { username },
      { withCredentials: true }
    );
          console.log(res.data);
    return res.data; 
  },
<<<<<<< HEAD:frontend/src/services/authService.ts
=======

  

>>>>>>> 545c25c ([CLASHCODE-5] integrated the frontend to user authentication):frontend/src/services/OAuth2Service.ts
};
