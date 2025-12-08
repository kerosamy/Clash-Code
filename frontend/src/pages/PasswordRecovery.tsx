import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import InputField from "../components/authentication/InputField.tsx";
import PasswordField from "../components/authentication/PasswordField.tsx";

// ===== Backend calls =====

async function handleResponse(res: Response) {
  // If the response is NOT ok (e.g., 400 or 500), parse the error message
  if (!res.ok) {
    const errorText = await res.text();
    throw new Error(errorText || "An unexpected error occurred.");
  }
  
  // If res.ok is true (Status 200), we return nothing because 
  // the backend sends an empty body for success.
}

async function fetchRecoveryQuestion(email: string) {
  const res = await fetch("http://localhost:8080/auth/recovery-question", {
    method: "POST",
    headers: { "Content-Type": "text/plain" },
    body: email,
  });

  if (!res.ok) {
    throw new Error(await res.text());
  }

  // This one still returns a string (the question), so we parse it.
  const questionString = await res.text();
  return questionString;
}

async function verifyRecoveryAnswer(email: string, answer: string) {
  const res = await fetch("http://localhost:8080/auth/verify-recovery", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    // Keys match VerifyRecoveryDto fields
    body: JSON.stringify({ email, answer }),
  });
  
  await handleResponse(res); // Will throw if 400, otherwise succeeds silently
}

async function resetPassword(email: string, newPassword: string) {
  const res = await fetch("http://localhost:8080/auth/reset-password", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    // Keys match PasswordResetDto fields
    body: JSON.stringify({ email, newPassword }),
  });
  
  await handleResponse(res); // Will throw if 400, otherwise succeeds silently
}
// ==========================

export default function PasswordRecovery() {
  const [step, setStep] = useState<1 | 2 | 3>(1);
  const [isGoogleUser, setIsGoogleUser] = useState(false);

  const [email, setEmail] = useState("");
  const [recoveryQuestion, setRecoveryQuestion] = useState("");
  const [recoveryAnswer, setRecoveryAnswer] = useState("");

  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const [errors, setErrors] = useState<{ [key: string]: string }>({});
  const navigate = useNavigate();

  // ---------------- HANDLER: Google Login ----------------
  const handleGoogleLogin = () => {
    sessionStorage.setItem("oauth_flow", "login");
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  // ---------------- VALIDATION ----------------
  const validateEmail = () => {
    const newErrors: { [key: string]: string } = {};
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      newErrors.email = "Please enter a valid email address.";
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const validateAnswer = () => {
    const newErrors: { [key: string]: string } = {};
    if (!recoveryAnswer.trim()) {
      newErrors.recoveryAnswer = "Please enter an answer.";
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const validateNewPassword = () => {
    const newErrors: { [key: string]: string } = {};
    if (newPassword.length < 8 || newPassword.length > 64) {
      newErrors.newPassword = "Password must be 8–64 characters.";
    }
    if (newPassword !== confirmPassword) {
      newErrors.confirmPassword = "Passwords do not match.";
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // ---------------- STEP 1: Fetch question ----------------
  const handleEmailSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrors({});
    setIsGoogleUser(false);

    if (!validateEmail()) return;

    try {
      const result = await fetchRecoveryQuestion(email);
      setRecoveryQuestion(result);
      setStep(2);
    } catch (err: any) {
      const msg = err.message || "";

      // Handle Google Account Check
      if (
        msg.includes("GOOGLE_USER_DETECTED") ||
        msg.includes("User does not have a recovery question")
      ) {
        setIsGoogleUser(true);
      } else {
        setErrors({ email: msg || "Email not found." });
      }
    }
  };

  // ---------------- STEP 2: Validate answer ----------------
  const handleAnswerSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrors({});

    if (!validateAnswer()) return;

    try {
      // Expecting empty 200 OK on success
      await verifyRecoveryAnswer(email, recoveryAnswer);
      setStep(3);
    } catch (err: any) {
      setErrors({ recoveryAnswer: err.message || "Incorrect answer." });
    }
  };

  // ---------------- STEP 3: Reset password ----------------
  const handlePasswordReset = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrors({});

    if (!validateNewPassword()) return;

    try {
      // Expecting empty 200 OK on success
      await resetPassword(email, newPassword);
      alert("Your password has been reset successfully!");
      navigate("/log-in");
    } catch (err: any) {
      setErrors({ global: err.message || "Something went wrong." });
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background font-anta">
      <div className="bg-container p-8 rounded-button shadow-lg w-full max-w-md text-white">
        {/* Logo */}
        <div className="flex justify-center mb-6">
          <img src="/src/assets/logo.svg" alt="App Logo" className="w-48" />
        </div>

        {/* --- GOOGLE USER PROMPT --- */}
        {isGoogleUser ? (
          <div className="text-center flex flex-col gap-4">
            <h3 className="text-xl text-orange font-bold">
              Google Account Detected
            </h3>
            <p className="text-gray-300">
              It looks like you signed up with Google. You don't need a password
              reset.
            </p>

            <button
              onClick={handleGoogleLogin}
              className="flex items-center justify-center gap-2 bg-background border border-gray-600 py-2 rounded-button w-full hover:bg-gray-700 transition-colors"
            >
              <img
                src="/src/assets/google-icon-1.png"
                alt="Google Icon"
                className="w-7 h-7"
              />
              Log in with Google
            </button>

            <button
              onClick={() => {
                setIsGoogleUser(false);
                setStep(1);
              }}
              className="text-sm text-gray-500 hover:underline mt-2"
            >
              Back to email entry
            </button>
          </div>
        ) : (
          <>
            {/* STEP 1 — Enter Email */}
            {step === 1 && (
              <form
                onSubmit={handleEmailSubmit}
                className="flex flex-col gap-4"
              >
                <InputField
                  type="email"
                  placeholder="Enter your email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  error={errors.email}
                />

                <button
                  type="submit"
                  className="bg-orange hover:opacity-90 py-2 rounded-button text-white"
                >
                  Continue
                </button>

                <div className="text-center text-sm mt-4">
                  <Link to="/log-in" className="text-orange hover:underline">
                    Back to Login
                  </Link>
                </div>
              </form>
            )}

            {/* STEP 2 — Show question & ask answer */}
            {step === 2 && (
              <form
                onSubmit={handleAnswerSubmit}
                className="flex flex-col gap-4"
              >
                <p className="text-gray-300 text-sm">Recovery Question:</p>
                <div className="bg-background p-3 rounded-button border border-gray-600 text-center font-semibold">
                  {recoveryQuestion}
                </div>

                <InputField
                  placeholder="Your Answer"
                  value={recoveryAnswer}
                  onChange={(e) => setRecoveryAnswer(e.target.value)}
                  error={errors.recoveryAnswer}
                />

                <button
                  type="submit"
                  className="bg-orange hover:opacity-90 py-2 rounded-button text-white"
                >
                  Verify Answer
                </button>

                <div className="text-sm mt-2 text-center">
                  <button
                    type="button"
                    onClick={() => setStep(1)}
                    className="text-gray-400 hover:underline"
                  >
                    Change email
                  </button>
                </div>
              </form>
            )}

            {/* STEP 3 — New password */}
            {step === 3 && (
              <form
                onSubmit={handlePasswordReset}
                className="flex flex-col gap-4"
              >
                {errors.global && (
                  <div className="text-red-500 text-center text-sm mb-2">
                    {errors.global}
                  </div>
                )}

                <PasswordField
                  placeholder="New Password"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  error={errors.newPassword}
                />

                <PasswordField
                  placeholder="Confirm Password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  error={errors.confirmPassword}
                />

                <button
                  type="submit"
                  className="bg-orange hover:opacity-90 py-2 rounded-button text-white"
                >
                  Reset Password
                </button>
              </form>
            )}
          </>
        )}
      </div>
    </div>
  );
}