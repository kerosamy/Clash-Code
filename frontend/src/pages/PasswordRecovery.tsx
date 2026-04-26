import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import InputField from "../components/authentication/InputField";
import PasswordField from "../components/authentication/PasswordField";
import { getUsername } from "../utils/jwtDecoder";
import { loginUser } from "../services/AuthService";
import { 
  fetchRecoveryQuestion, 
  verifyRecoveryAnswer, 
  resetPassword 
} from "../services/AuthService"; 
import {
  validateEmail,
  validateRecoveryAnswer,
  validatePassword,
  validatePasswordMatch
} from "../utils/validation";
import { getQuestionDisplayText } from "../utils/recoveryQuestions";
import AnimatedBackground from "../components/AnimatedBackground";
import { API_BASE } from '../services/api';

export default function PasswordRecovery() {
  const [step, setStep] = useState<1 | 2 | 3>(1);
  const [isGoogleUser, setIsGoogleUser] = useState(false);

  // Form State
  const [email, setEmail] = useState("");
  const [recoveryQuestion, setRecoveryQuestion] = useState("");
  const [recoveryAnswer, setRecoveryAnswer] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  
  // UI State
  const [errors, setErrors] = useState<{ [key: string]: string }>({});
  const [isLoading, setIsLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const navigate = useNavigate();

  // Helper to safely extract error message
  const getErrorMessage = (err: any): string => {
    return err.message || String(err) || "An unexpected error occurred.";
  };

  // ---------------- HANDLER: Google Login ----------------
  const handleGoogleLogin = () => {
    sessionStorage.setItem("oauth_flow", "login");
    window.location.href = `${API_BASE}/oauth2/authorization/google`;
  };

  // ---------------- STEP 1: Fetch question ----------------
  const handleEmailSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrors({});
    setIsGoogleUser(false);

    // 1. Validation
    const emailValidation = validateEmail(email);
    if (!emailValidation.isValid) {
      setErrors({ email: emailValidation.error! });
      return;
    }

    setIsLoading(true);

    try {
      const question = await fetchRecoveryQuestion(email);
      setRecoveryQuestion(question);
      setStep(2);
    } catch (err: any) {
      const msg = getErrorMessage(err);

      // Check for Google account specific error
      if (msg.toLowerCase().includes("google")) {
        setIsGoogleUser(true);
      } else {
        setErrors({ email: msg });
      }
    } finally {
      setIsLoading(false);
    }
  };

  // ---------------- STEP 2: Validate answer ----------------
  const handleAnswerSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrors({});

    // 1. Validation
    const answerValidation = validateRecoveryAnswer(recoveryAnswer);
    if (!answerValidation.isValid) {
      setErrors({ recoveryAnswer: answerValidation.error! });
      return;
    }

    setIsLoading(true);

    try {
      await verifyRecoveryAnswer({ email, answer: recoveryAnswer });
      setStep(3);
    } catch (err: any) {
      setErrors({ recoveryAnswer: getErrorMessage(err) });
    } finally {
      setIsLoading(false);
    }
  };

  // ---------------- STEP 3: Reset password ----------------
  const handlePasswordReset = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrors({});

    const newErrors: { [key: string]: string } = {};

    // 1. Validate Password format
    const passwordValidation = validatePassword(newPassword);
    if (!passwordValidation.isValid) {
      newErrors.newPassword = passwordValidation.error!;
    }

    // 2. Validate Password Match
    const matchValidation = validatePasswordMatch(newPassword, confirmPassword);
    if (!matchValidation.isValid) {
      newErrors.confirmPassword = matchValidation.error!;
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setIsLoading(true);

    try {
      await resetPassword({ email, newPassword });
      setSuccessMessage("Password reset successful! Logging you in...");

        await loginUser({
        email,
        password: newPassword,
      });

      setTimeout(() => {
        navigate(`/profile/${getUsername()}/overview`);
      }, 1200);

    } catch (err: any) {
      setErrors({ global: getErrorMessage(err) });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background font-anta relative overflow-hidden">
                  <AnimatedBackground />
      <div className="bg-container p-8 rounded-button shadow-lg w-full max-w-md text-white relative z-10">
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
                setErrors({});
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
                  onChange={(e) => {
                    setEmail(e.target.value);
                    if (errors.email) setErrors(prev => ({ ...prev, email: "" }));
                  }}
                  error={errors.email}
                />

                <button
                  type="submit"
                  disabled={isLoading}
                  className={`bg-orange text-white py-2 rounded-button transition-all 
                    ${isLoading ? 'opacity-50 cursor-not-allowed' : 'hover:opacity-90'}
                  `}
                >
                  {isLoading ? "Checking..." : "Continue"}
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
                <div className="bg-background p-3 rounded-button border border-gray-600 text-center font-semibold text-orange">
                  {getQuestionDisplayText(recoveryQuestion)}
                </div>

                <InputField
                  placeholder="Your Answer"
                  value={recoveryAnswer}
                  onChange={(e) => {
                    setRecoveryAnswer(e.target.value);
                    if (errors.recoveryAnswer) setErrors(prev => ({ ...prev, recoveryAnswer: "" }));
                  }}
                  error={errors.recoveryAnswer}
                />

                <button
                  type="submit"
                  disabled={isLoading}
                  className={`bg-orange text-white py-2 rounded-button transition-all 
                    ${isLoading ? 'opacity-50 cursor-not-allowed' : 'hover:opacity-90'}
                  `}
                >
                  {isLoading ? "Verifying..." : "Verify Answer"}
                </button>

                <div className="text-sm mt-2 text-center">
                  <button
                    type="button"
                    onClick={() => {
                        setStep(1);
                        setErrors({});
                    }}
                    className="text-gray-400 hover:underline"
                    disabled={isLoading}
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
                  <div className="p-3 bg-red-500/20 border border-red-500 text-red-100 text-sm rounded text-center">
                    {errors.global}
                  </div>
                )}

                {successMessage && (
                  <div className="p-3 bg-green-500/20 border border-green-500 text-green-100 text-sm rounded text-center">
                    {successMessage}
                  </div>
                )}

                <PasswordField
                  placeholder="New Password"
                  value={newPassword}
                  onChange={(e) => {
                    setNewPassword(e.target.value);
                    if (errors.newPassword) setErrors(prev => ({ ...prev, newPassword: "" }));
                  }}
                  error={errors.newPassword}
                />

                <PasswordField
                  placeholder="Confirm Password"
                  value={confirmPassword}
                  onChange={(e) => {
                    setConfirmPassword(e.target.value);
                    if (errors.confirmPassword) setErrors(prev => ({ ...prev, confirmPassword: "" }));
                  }}
                  error={errors.confirmPassword}
                />

                <button
                  type="submit"
                  disabled={isLoading}
                  className={`bg-orange text-white py-2 rounded-button transition-all 
                    ${isLoading ? 'opacity-50 cursor-not-allowed' : 'hover:opacity-90'}
                  `}
                >
                  {isLoading ? "Resetting..." : "Reset Password"}
                </button>
              </form>
            )}
          </>
        )}
      </div>
    </div>
  );
}