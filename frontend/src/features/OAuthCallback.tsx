import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getGoogleToken, getAuthenticatedUser } from "../services/AuthService";
import { getUsername } from "../utils/jwtDecoder";

export default function OAuthCallback() {
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);
  const username = getUsername();
  useEffect(() => {
    const processOAuth = async () => {
      try {
        const response = await getGoogleToken();
        const { token } = response;

        if (!token) throw new Error("Token not returned");
        localStorage.setItem("token", token);
        sessionStorage.getItem("oauth_flow");

        await new Promise((resolve) => setTimeout(resolve, 800));

        let userExists = false;
        try {
          const user = await getAuthenticatedUser();
          if (user?.email) userExists = true;
        } catch {
          userExists = false;
        }

        sessionStorage.removeItem("oauth_flow");

        if (userExists) {
          navigate(`/profile/${getUsername()}/overview`);
        } else {
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
