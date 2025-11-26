import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { authService } from "../services/authService";

export interface UserResponseDto {
  id: number;
  username?: string; 
  email: string;
}

export default function OAuthCallback() {
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const checkAuthStatus = async () => {
      try {
        const user: UserResponseDto = await authService.getGoogleUser();
        const flow = sessionStorage.getItem('oauth_flow');
        console.log("Flow :", flow);

        sessionStorage.removeItem('oauth_flow');

        await new Promise((resolve) => setTimeout(resolve, 1500));

        if (user.username) {
          navigate("/profile");
        } else {
            if (flow === "signup") {
            navigate("/complete-registration", { state: { email: user.email } });
          } else {
            setError("User not found. Please sign up first.");
            setTimeout(() => navigate("/sign-up"), 3000);
          }
        }
      } catch (err) {
        setError("Authentication failed. Please try again.");
        setTimeout(() => navigate("/sign-up"), 3000);
      }
    };

    checkAuthStatus();
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