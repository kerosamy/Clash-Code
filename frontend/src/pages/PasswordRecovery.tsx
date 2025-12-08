import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import InputField from "../components/authentication/InputField.tsx";
import PasswordField from "../components/authentication/PasswordField.tsx";

// ===== Backend calls (replace URLs or service functions) =====
async function fetchRecoveryQuestion(email: string) {
  const res = await fetch("http://localhost:8080/auth/recovery-question", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email }),
  });

  if (!res.ok) {
    throw new Error(await res.text());
  }

  const data = await res.json();
  console.log(data.recoveryQuestion);
  return data.recoveryQuestion; // <-- Extract string from JSON object
}

async function verifyRecoveryAnswer(email: string, answer: string) {
  const res = await fetch("http://localhost:8080/auth/verify-recovery", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, answer }),
  });

  if (!res.ok) throw new Error(await res.text());
}

async function resetPassword(email: string, newPassword: string) {
  const res = await fetch("http://localhost:8080/auth/reset-password", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, newPassword }),
  });

  if (!res.ok) throw new Error(await res.text());
}
// =============================================================


export default function PasswordRecovery() {
  const [step, setStep] = useState<1 | 2 | 3>(1);

  const [email, setEmail] = useState("");
  const [recoveryQuestion, setRecoveryQuestion] = useState("");
  const [recoveryAnswer, setRecoveryAnswer] = useState("");

  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const [errors, setErrors] = useState<{ [key: string]: string }>({});
  const navigate = useNavigate();


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

    if (recoveryAnswer.length < 2 || recoveryAnswer.length > 100) {
      newErrors.recoveryAnswer = "Answer must be 2–100 characters long.";
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

    if (!validateEmail()) return;

    try {
      const result = await fetchRecoveryQuestion(email);
      setRecoveryQuestion(result);
      setStep(2);
    } catch (err: any) {
      setErrors({ email: err.message || "Email not found." });
    }
  };


  // ---------------- STEP 2: Validate answer ----------------
  const handleAnswerSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrors({});

    if (!validateAnswer()) return;

    try {
      await verifyRecoveryAnswer(email, recoveryAnswer);
      setStep(3);
    } catch (err: any) {
      setErrors({ recoveryAnswer: "Incorrect answer. Try again." });
    }
  };


  // ---------------- STEP 3: Reset password ----------------
  const handlePasswordReset = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrors({});

    if (!validateNewPassword()) return;

    try {
      await resetPassword(email, newPassword);
      alert("Your password has been reset successfully!");
      navigate("/log-in");
    } catch (err: any) {
      alert(err.message || "Something went wrong.");
    }
  };


  return (
    <div className="min-h-screen flex items-center justify-center bg-background font-anta">
      <div className="bg-container p-8 rounded-button shadow-lg w-full max-w-md text-white">
        
        {/* Logo */}
        <div className="flex justify-center mb-6">
          <img src="/src/assets/logo.svg" alt="App Logo" className="w-48" />
        </div>


        {/* STEP 1 — Enter Email */}
        {step === 1 && (
          <form onSubmit={handleEmailSubmit} className="flex flex-col gap-4">
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
          <form onSubmit={handleAnswerSubmit} className="flex flex-col gap-4">

            <p className="text-gray-300 text-sm">Recovery Question:</p>
            <p className="bg-background p-3 rounded-button border border-gray-600">
              {recoveryQuestion}
            </p>

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
          <form onSubmit={handlePasswordReset} className="flex flex-col gap-4">
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
      </div>
    </div>
  );
}
