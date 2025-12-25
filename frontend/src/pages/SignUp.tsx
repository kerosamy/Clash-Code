import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { registerUser, type RegisterRequest } from "../services/AuthService";
import InputField from "../components/authentication/InputField";
import PasswordField from "../components/authentication/PasswordField";
import RecoveryQuestionModal from "../components/authentication/RecoveryQuestionModal";
import AnimatedBackground from "../components/AnimatedBackground";
import {
  validateUsername,
  validateEmail,
  validatePassword,
  validatePasswordMatch,
  validateRecoveryQuestion,
  validateRecoveryAnswer
} from "../utils/validation";
import { API_BASE } from '../services/api';

export default function SignUp() {
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
    recoveryQuestion: "",
    recoveryAnswer: ""
  });

  const [isRecoveryModalOpen, setIsRecoveryModalOpen] = useState(false);
  const [errors, setErrors] = useState<{ [key: string]: string }>({});
  const [isLoading, setIsLoading] = useState(false);
  
  const navigate = useNavigate();

  const handleInputChange = (field: keyof typeof formData) => (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    setFormData(prev => ({ ...prev, [field]: e.target.value }));
    // Clear error for this field when user starts typing
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: "" }));
    }
  };

  const handleGoogleSignUp = () => {
    sessionStorage.setItem('oauth_flow', 'signup');
    window.location.href = `${API_BASE}/oauth2/authorization/google`;
  };

  const validateAllInputs = () => {
    const newErrors: { [key: string]: string } = {};

    // Execute all validations from validation.ts
    const usernameVal = validateUsername(formData.username);
    if (!usernameVal.isValid) newErrors.username = usernameVal.error!;

    const emailVal = validateEmail(formData.email);
    if (!emailVal.isValid) newErrors.email = emailVal.error!;

    const passVal = validatePassword(formData.password);
    if (!passVal.isValid) newErrors.password = passVal.error!;

    const matchVal = validatePasswordMatch(formData.password, formData.confirmPassword);
    if (!matchVal.isValid) newErrors.confirmPassword = matchVal.error!;

    const qVal = validateRecoveryQuestion(formData.recoveryQuestion);
    if (!qVal.isValid) newErrors.recoveryQuestion = qVal.error!;

    const aVal = validateRecoveryAnswer(formData.recoveryAnswer);
    if (!aVal.isValid) newErrors.recoveryAnswer = aVal.error!;

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSignUp = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrors({});

    if (!validateAllInputs()) return;

    setIsLoading(true);

    const requestData: RegisterRequest = {
      username: formData.username,
      email: formData.email,
      password: formData.password,
      recoveryQuestion: formData.recoveryQuestion,
      recoveryAnswer: formData.recoveryAnswer,
    };

    try {
      await registerUser(requestData);

      navigate(`/profile/${formData.username}/overview`, { replace: true });
    } catch (err: any) {
      const errorMessage = err || "Registration failed";
      setErrors({ global: errorMessage });
    } finally {
      setIsLoading(false);
    }
  };

  const updateRecoveryData = (question: string, answer: string) => {
    setFormData(prev => ({
      ...prev,
      recoveryQuestion: question,
      recoveryAnswer: answer
    }));
    
    // Clear errors if valid
    if(errors.recoveryQuestion && validateRecoveryQuestion(question).isValid) {
        setErrors(prev => ({ ...prev, recoveryQuestion: "" }));
    }
    if(errors.recoveryAnswer && validateRecoveryAnswer(answer).isValid) {
        setErrors(prev => ({ ...prev, recoveryAnswer: "" }));
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background font-anta relative overflow-hidden">
      <AnimatedBackground />

      <div className="bg-container p-8 rounded-button shadow-lg w-full max-w-md text-white relative z-10">
        <div className="flex justify-center mb-6">
          <img src="/src/assets/logo.svg" alt="App Logo" className="w-48" />
        </div>

        {/* Global error banner (only shows if error isn't specific to username/email) */}
        {errors.global && (
          <div className="mb-4 p-3 bg-red-500/20 border border-red-500 text-red-100 text-sm rounded text-center">
            {errors.global}
          </div>
        )}

        <form onSubmit={handleSignUp} className="flex flex-col gap-4">
          <InputField
            placeholder="Username"
            value={formData.username}
            onChange={handleInputChange('username')}
            error={errors.username}
          />

          <InputField
            type="email"
            placeholder="Email"
            value={formData.email}
            onChange={handleInputChange('email')}
            error={errors.email}
          />

          <PasswordField
            placeholder="Password"
            value={formData.password}
            onChange={handleInputChange('password')}
            error={errors.password}
          />

          <PasswordField
            placeholder="Confirm Password"
            value={formData.confirmPassword}
            onChange={handleInputChange('confirmPassword')}
            error={errors.confirmPassword}
          />

          <button
            type="button"
            onClick={() => setIsRecoveryModalOpen(true)}
            className={`bg-background border p-2 rounded-button hover:opacity-90 transition-colors
              ${(errors.recoveryQuestion || errors.recoveryAnswer) ? 'border-red-500 text-red-100' : 'border-gray-600'}
            `}
          >
            {formData.recoveryQuestion ? '✓ Recovery Question Set' : 'Set Recovery Question'}
          </button>
          
          {(errors.recoveryQuestion || errors.recoveryAnswer) && (
            <p className="text-red-500 text-sm text-center">
              {errors.recoveryQuestion || errors.recoveryAnswer}
            </p>
          )}

          <button
            type="submit"
            disabled={isLoading}
            className={`bg-orange text-white py-2 rounded-button mt-2 transition-all flex justify-center items-center
              ${isLoading ? 'opacity-50 cursor-not-allowed' : 'hover:opacity-90'}
            `}
          >
            {isLoading ? <span>Creating account...</span> : "Sign Up"}
          </button>
        </form>

        <div className="flex items-center my-4">
          <div className="flex-grow border-t border-gray-600"></div>
          <span className="px-2 text-gray-400">OR</span>
          <div className="flex-grow border-t border-gray-600"></div>
        </div>

        <button
          onClick={handleGoogleSignUp}
          disabled={isLoading}
          className="flex items-center justify-center gap-2 bg-background border border-gray-600 py-2 rounded-button w-full hover:bg-gray-800 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
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
        recoveryQuestion={formData.recoveryQuestion}
        setRecoveryQuestion={(q) => updateRecoveryData(q, formData.recoveryAnswer)}
        recoveryAnswer={formData.recoveryAnswer}
        setRecoveryAnswer={(a) => updateRecoveryData(formData.recoveryQuestion, a)}
        errorQuestion={errors.recoveryQuestion}
        errorAnswer={errors.recoveryAnswer}
      />
    </div>
  );
}