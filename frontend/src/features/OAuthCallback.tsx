import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { authService } from "../services/OAuth2Service";
import axios from "axios";

export default function OAuthCallback() {
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const processOAuth = async () => {
      try {
        // Step 1: Get token from backend OAuthCallback
        const response = await authService.getGoogleToken();
        console.log("OAuth token response:", response);

        const { token } = response;
        if (!token) throw new Error("Token not returned");

        // Store JWT in localStorage
        localStorage.setItem("token", token);

        const flow = sessionStorage.getItem("oauth_flow");

        await new Promise((resolve) => setTimeout(resolve, 800));

        // Step 2: Check if the user exists using /me endpoint
        let userExists = false;
        try {
          const res = await axios.get("http://localhost:8080/auth/me", {
            headers: { Authorization: `Bearer ${token}` },
          });
          if (res.data?.email) userExists = true;
        } catch (e) {
          userExists = false;
        }

        sessionStorage.removeItem("oauth_flow");

        // ---- REDIRECT LOGIC ----
        if (userExists) {
          // User already exists → go to profile
          navigate("/profile/1/overview");
        } else {
          // User does not exist → go to complete registration
          navigate("/complete-registration");
        }

      } catch (err) {
        console.error(err);
        setError("Authentication failed. Please try again.");
        setTimeout(() => navigate("/sign-up"), 2500);
      }
    };

    processOAuth();
  }, [navigate]);

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <div className="text-white text-center">
          <p className="text-red-500 mb-4">{error}</p>
          <p>Redirecting to sign up...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-background">
      <div className="text-white text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-orange mx-auto mb-4"></div>
        <p>Please wait...</p>
      </div>
    </div>
  );
}
