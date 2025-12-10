import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import InputField from "../components/authentication/InputField.tsx";
import PasswordField from "../components/authentication/PasswordField.tsx";
import { 
  fetchRecoveryQuestion, 
  verifyRecoveryAnswer, 
  resetPassword 
} from "../services/AuthService.ts"; 
import {
  validateEmail,
  validateRecoveryAnswer,
  validatePassword,
  validatePasswordMatch
} from "../utils/validation";
import { getQuestionDisplayText } from "../utils/recoveryQuestions";

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

  // ---------------- STEP 1: Fetch question ----------------
  const handleEmailSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrors({});
    setIsGoogleUser(false);

    // Validation using Utils
    const emailValidation = validateEmail(email);
    if (!emailValidation.isValid) {
      setErrors({ email: emailValidation.error || "Invalid email" });
      return;
    }

    try {
      const result = await fetchRecoveryQuestion(email);
      setRecoveryQuestion(result);
      setStep(2);
    } catch (err: any) {
      const msg = err.message || "";

      // Handle Google Account Check
      if (msg.includes("Google")) {
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

    // Validation using Utils
    const answerValidation = validateRecoveryAnswer(recoveryAnswer);
    if (!answerValidation.isValid) {
      setErrors({ recoveryAnswer: answerValidation.error || "Invalid answer" });
      return;
    }

    try {
      await verifyRecoveryAnswer({ email, answer: recoveryAnswer });
      setStep(3);
    } catch (err: any) {
      setErrors({ recoveryAnswer: err.message || "Incorrect answer." });
    }
  };

  // ---------------- STEP 3: Reset password ----------------
  const handlePasswordReset = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrors({});

    const newErrors: { [key: string]: string } = {};

    // Validate Password format
    const passwordValidation = validatePassword(newPassword);
    if (!passwordValidation.isValid) {
      newErrors.newPassword = passwordValidation.error!;
    }

    // Validate Password Match
    const matchValidation = validatePasswordMatch(newPassword, confirmPassword);
    if (!matchValidation.isValid) {
      newErrors.confirmPassword = matchValidation.error!;
    }

    // If there are validation errors, stop here
    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    try {
      await resetPassword({ email, newPassword });
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
                  {getQuestionDisplayText(recoveryQuestion)}
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