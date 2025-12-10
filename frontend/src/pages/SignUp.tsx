import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { registerUser } from "../services/AuthService.ts";
import InputField from "../components/authentication/InputField.tsx";
import PasswordField from "../components/authentication/PasswordField.tsx";
import RecoveryQuestionModal from "../components/authentication/RecoveryQuestionModal.tsx";
import {
  validateUsername,
  validateEmail,
  validatePassword,
  validatePasswordMatch,
  validateRecoveryQuestion,
  validateRecoveryAnswer
} from "../utils/validation";

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
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  const validateAllInputs = () => {
    const newErrors: { [key: string]: string } = {};

    const validations = [
      { fn: validateUsername(formData.username), field: 'username' },
      { fn: validateEmail(formData.email), field: 'email' },
      { fn: validatePassword(formData.password), field: 'password' },
      { fn: validatePasswordMatch(formData.password, formData.confirmPassword), field: 'confirmPassword' },
      { fn: validateRecoveryQuestion(formData.recoveryQuestion), field: 'recoveryQuestion' },
      { fn: validateRecoveryAnswer(formData.recoveryAnswer), field: 'recoveryAnswer' }
    ];

    validations.forEach(({ fn, field }) => {
      if (!fn.isValid) {
        newErrors[field] = fn.error!;
      }
    });

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSignUp = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrors({});

    // Client-side validation
    if (!validateAllInputs()) return;

    setIsLoading(true);

    try {
      const requestData = {
        username: formData.username,
        email: formData.email,
        password: formData.password,
        recoveryQuestion: formData.recoveryQuestion,
        recoveryAnswer: formData.recoveryAnswer,
      };

      await registerUser(requestData);
      navigate("/profile/1/overview");
    } catch (err: any) {
      // --- IMPROVED ERROR HANDLING ---
      
      // 1. Extract the deep backend message (ignoring generic 404/500 wrappers if possible)
      // Checks: err.response.data.message -> err.response.data -> err.message
      const backendError = 
        err.response?.data?.message || 
        err.response?.data || 
        err.message || 
        "Unknown error";

      const errorString = String(backendError).toLowerCase();

      // 2. Map the error to the specific field based on keywords
      if (errorString.includes("email")) {
        setErrors({ email: "This email is already registered." });
      } 
      else if (errorString.includes("username")) {
        setErrors({ username: "This username is already taken." });
      } 
      else {
        // Fallback for generic errors (e.g. server down)
        setErrors({ global: typeof backendError === 'string' ? backendError : "Registration failed. Please try again." });
      }
    } finally {
      setIsLoading(false);
    }
  };

  try {
    // Call backend and receive token
    await registerUser(requestData);

    // Redirect
    navigate(`/profile/${username}/overview`);
    
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

  const updateRecoveryData = (question: string, answer: string) => {
    setFormData(prev => ({
      ...prev,
      recoveryQuestion: question,
      recoveryAnswer: answer
    }));
    if(errors.recoveryQuestion) setErrors(prev => ({ ...prev, recoveryQuestion: "" }));
    if(errors.recoveryAnswer) setErrors(prev => ({ ...prev, recoveryAnswer: "" }));
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background font-anta">
      <div className="bg-container p-8 rounded-button shadow-lg w-full max-w-md text-white">
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
            error={errors.username} // Error appears here
          />

          <InputField
            type="email"
            placeholder="Email"
            value={formData.email}
            onChange={handleInputChange('email')}
            error={errors.email} // Error appears here
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
            className="bg-background border border-gray-600 p-2 rounded-button hover:opacity-90 transition-colors"
          >
            {formData.recoveryQuestion ? '✓ Recovery Question Set' : 'Set Recovery Question'}
          </button>
          
          {(errors.recoveryQuestion || errors.recoveryAnswer) && (
            <p className="text-red-500 text-sm">
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
