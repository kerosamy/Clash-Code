import { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import InputField from "../components/authentication/InputField.tsx";
import { completeRegistration } from "../services/AuthService.ts";
import { decodeToken } from "../utils/jwtDecoder.tsx";

export default function CompleteRegistration() {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [errors, setErrors] = useState<{ [key: string]: string }>({});
  const [loading, setLoading] = useState(false);
  
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    if (location.state?.token) {
      const decoded = decodeToken(location.state.token);
      const email = decoded?.sub || "";
      setEmail(email);
    } 
    else {
      const token = localStorage.getItem("token");
      if (token) {
        const decoded = decodeToken(token);
        if (decoded?.sub) setEmail(decoded.sub);
      }
    }
    sessionStorage.setItem("oauth_processed", "true");
  }, [location.state]);

  const validateInputs = () => {
    const newErrors: { [key: string]: string } = {};

    if (!/^[a-zA-Z0-9_-]{4,32}$/.test(username)) {
      newErrors.username =
        "Username must be 4-32 characters and contain only letters, numbers, '_' or '-'.";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async () => {
    if (!validateInputs()) return;

    setLoading(true);
    setErrors({});

    try {
      await completeRegistration(username,email);
      sessionStorage.removeItem("oauth_flow");
      await new Promise((resolve) => setTimeout(resolve, 1000));

      navigate(`/profile/${username}/overview`, { replace: true });
    } catch (err: any) {
      setErrors({
        username:
          err.response?.data?.message ||
          "Username already taken or an error occurred.",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background font-anta">
      <div className="bg-container p-8 rounded-button shadow-lg w-full max-w-md text-white">
        <div className="flex justify-center mb-6">
          <img src="/src/assets/logo.svg" alt="App Logo" className="w-48" />
        </div>
      
        <h2 className="text-2xl mb-4 text-center">Complete Your Registration</h2>

        <p className="text-gray-400 mb-6 text-center text-sm">
          Email: <span className="text-white">{email || "Not found"}</span>
        </p>

        <InputField
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          error={errors.username}
        />
   
        <div className="flex items-center my-4"></div>
       
        <button
          onClick={handleSubmit}
          className="bg-orange hover:opacity-90 py-2 px-8 rounded-lg text-white mt-2 block mx-auto"
        >
          {loading ? "Finalizing..." : "Complete Registration"}
        </button>
      </div>
    </div>
  );
}