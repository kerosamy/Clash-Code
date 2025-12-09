import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { registerUser } from "../services/AuthService.ts";
import InputField from "../components/authentication/InputField.tsx";
import PasswordField from "../components/authentication/PasswordField.tsx";
import RecoveryQuestionModal from "../components/authentication/RecoveryQuestionModal.tsx";

export default function SignUp() {
  const [username, setUsername] = useState("");
  const [emailOrHandle, setEmailOrHandle] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const [recoveryQuestion, setRecoveryQuestion] = useState("");
  const [recoveryAnswer, setRecoveryAnswer] = useState("");

  const [isRecoveryModalOpen, setIsRecoveryModalOpen] = useState(false);
  const [errors, setErrors] = useState<{ [key: string]: string }>({});
  const navigate = useNavigate();

  const handleGoogleSignUP = () => {
    sessionStorage.setItem('oauth_flow', 'signup');
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  const validateInputs = () => {
    const newErrors: { [key: string]: string } = {};

    if (!/^[a-zA-Z0-9_-]{4,32}$/.test(username)) {
      newErrors.username =
        "Username must be 4–32 characters and contain only letters, numbers, '_' or '-'.";
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailOrHandle)) {
      newErrors.email = "Please enter a valid email address.";
    }

    if (password.length < 8 || password.length > 64) {
      newErrors.password = "Password must be 8–64 characters long.";
    }

    if (password !== confirmPassword) {
      newErrors.confirmPassword = "Passwords do not match.";
    }

    if (!recoveryQuestion) {
      newErrors.recoveryQuestion = "Please select a recovery question.";
    }

    if (recoveryAnswer.length < 2 || recoveryAnswer.length > 100) {
      newErrors.recoveryAnswer = "Recovery answer must be 2–100 characters.";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

 const handleSignUp = async (e: React.FormEvent) => {
  e.preventDefault();

  // Clear previous errors
  setErrors({});

  if (!validateInputs()) return;

  const requestData = {
    username: username,
    email: emailOrHandle,
    password: password,
    recoveryQuestion: recoveryQuestion,
    recoveryAnswer: recoveryAnswer,
  };

  try {
    // Call backend and receive token
    await registerUser(requestData);

    // Redirect
    navigate("/profile/1/overview");
  } catch (err: any) {
    const errorMessage = err.message?.toLowerCase() || "";

    if (errorMessage.includes("email")) {
      setErrors({ email: err.message });
    } else if (errorMessage.includes("username")) {
      setErrors({ username: err.message });
    } else {
      alert(err.message || "Something went wrong. Try again later.");
    }
  }
};


  return (
    <div className="min-h-screen flex items-center justify-center bg-background font-anta">
      <div className="bg-container p-8 rounded-button shadow-lg w-full max-w-md text-white">
        <div className="flex justify-center mb-6">
          <img src="/src/assets/logo.svg" alt="App Logo" className="w-48" />
        </div>

        <form onSubmit={handleSignUp} className="flex flex-col gap-4">
          <InputField
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            error={errors.username}
          />

          <InputField
            type="email"
            placeholder="Email"
            value={emailOrHandle}
            onChange={(e) => setEmailOrHandle(e.target.value)}
            error={errors.email}
          />

          <PasswordField
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            error={errors.password}
          />

          <PasswordField
            placeholder="Confirm Password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            error={errors.confirmPassword}
          />

          <button
            type="button"
            onClick={() => setIsRecoveryModalOpen(true)}
            className="bg-background border border-gray-600 p-2 rounded-button hover:opacity-90"
          >
            Set Recovery Question
          </button>
          {errors.recoveryQuestion && (
            <p className="text-red-500 text-sm">{errors.recoveryQuestion}</p>
          )}
          {errors.recoveryAnswer && (
            <p className="text-red-500 text-sm">{errors.recoveryAnswer}</p>
          )}

          <button
            type="submit"
            className="bg-orange hover:opacity-90 py-2 rounded-button text-white mt-2"
          >
            Sign Up
          </button>
        </form>

        {/* Divider */}
        <div className="flex items-center my-4">
          <div className="flex-grow border-t border-gray-600"></div>
          <span className="px-2 text-gray-400">OR</span>
          <div className="flex-grow border-t border-gray-600"></div>
        </div>

        <button
          onClick=
            {handleGoogleSignUP}
  
          className="flex items-center justify-center gap-2 bg-background border border-gray-600 py-2 rounded-button w-full"
        >
          <img
            src="/src/assets/google-icon-1.png"
            alt="Google Icon"
            className="w-7 h-7"
          />
          Sign up with Google
        </button>

        <div className="text-center text-sm mt-4">
          <span>Already have an account? </span>
          <Link to="/log-in" className="text-orange hover:underline">
            Log in
          </Link>
        </div>
      </div>

      <RecoveryQuestionModal
        isOpen={isRecoveryModalOpen}
        onClose={() => setIsRecoveryModalOpen(false)}
        recoveryQuestion={recoveryQuestion}
        setRecoveryQuestion={setRecoveryQuestion}
        recoveryAnswer={recoveryAnswer}
        setRecoveryAnswer={setRecoveryAnswer}
        errorQuestion={errors.recoveryQuestion}
        errorAnswer={errors.recoveryAnswer}
      />
    </div>
  );
}