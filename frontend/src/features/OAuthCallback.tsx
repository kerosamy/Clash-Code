import { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { decodeToken } from "../utils/jwtDecoder";

export default function OAuthCallback() {
  const navigate = useNavigate();
  const location = useLocation();
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const token = params.get("token");

    if (!token) {
      setError("Authentication failed: No token received.");
      setTimeout(() => navigate("/sign-up"), 2500);
      return;
    }

    localStorage.setItem("token", token);
    const decoded = decodeToken(token);

    const username = decoded?.username || "";

    if (username && username.trim() !== "") {
      navigate(`/profile/${username}/overview`, { replace: true });
    } else {
      navigate("/complete-registration", { state: { token }, replace: true });
    }
  }, [location, navigate]);

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
