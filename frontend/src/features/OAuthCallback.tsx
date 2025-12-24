import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getGoogleToken } from "../services/AuthService";
import { decodeToken } from "../utils/jwtDecoder";

export default function OAuthCallback() {
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    const processOAuth = async () => {
      try {
        const response = await getGoogleToken();
        const { token } = response;

        if (!token) throw new Error("Authentication failed: No token received.");
        
        localStorage.setItem("token", token);
        const decoded = decodeToken(token);

        const email = decoded?.sub || "";
        const username = decoded?.username || ""; 

        sessionStorage.removeItem("oauth_flow");

        if (username && username.trim() !== "") {
          navigate(`/profile/${username}/overview`, { replace: true });
        } else {
          navigate("/complete-registration", { 
            state: { email },
            replace: true 
          });
        }
      } catch (err: any) {
        setError(err.message || "Authentication failed.");
        setTimeout(() => navigate("/sign-up"), 2500);
      }
    };

    processOAuth();
  }, [navigate]);

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background font-anta text-white p-4">
        <div className="text-center">
          <p className="text-red-500 mb-4">{error}</p>
          <p>Redirecting to sign up...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-background font-anta text-white">
      <div className="text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-orange mx-auto mb-4"></div>
        <p>Finalizing authentication...</p>
      </div>
    </div>
  );
}