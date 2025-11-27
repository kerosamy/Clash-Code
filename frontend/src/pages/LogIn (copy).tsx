import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import InputField from '../components/authentication/InputField';
import PasswordField from '../components/authentication/PasswordField';
// ADJUST THIS PATH: Import the login function and type from your api file
import { loginUser, type LoginRequest } from '../services/AuthService';

export default function LogIn() {
  const [email, setEmail] = useState(''); // Acts as 'username' or 'email'
  const [password, setPassword] = useState('');
  
  // UI States
  const [errors, setErrors] = useState({ email: '', password: '' });
  const [apiError, setApiError] = useState(''); // For backend errors (e.g. "Bad credentials")
  const [isLoading, setIsLoading] = useState(false);
  
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();

    const newErrors = { email: '', password: '' };
    setApiError('');
    let formValid = true;

    if (!email.trim()) {
      newErrors.email = 'Email/Username is required';
      formValid = false;
    } else if (email.length < 4) {
      newErrors.email = 'Username/Email is too short';
      formValid = false;
    }

    if (!password.trim()) {
      newErrors.password = 'Password is required';
      formValid = false;
    } else if (password.length < 8 || password.length > 64) {
      newErrors.password = 'Password must be 8–64 characters long';
      formValid = false;
    }

    setErrors(newErrors);
    if (!formValid) return;

    setIsLoading(true);
    
    try {
      const credentials: LoginRequest = {
        email: email,
        password: password
      };

      const response = await loginUser(credentials);
      console.log(response);
      localStorage.setItem('token', response.token); 
    
      navigate('/profile/1/overview');
      
    } catch (err: any) {
      setApiError(err.message || 'Login failed. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleGoogleLogin = () => {
      sessionStorage.setItem('oauth_flow', 'login');
      window.location.href = "http://localhost:8080/oauth2/authorization/google";
    };
  // Basic check for button enable state
  const isFormValid = email.length >= 4 && password.length >= 8 && password.length <= 64;

  return (
    <div className="min-h-screen flex items-center justify-center bg-background font-anta">
      <div className="bg-container p-8 rounded-button shadow-lg w-full max-w-md text-white">
        <div className="flex justify-center mb-6">
          <img src="/src/assets/logo.svg" alt="App Logo" className="w-48" />
        </div>

        {/* Global API Error Message */}
        {apiError && (
          <div className="mb-4 p-3 bg-red-500/20 border border-red-500 text-red-100 text-sm rounded text-center">
            {apiError}
          </div>
        )}

        <form onSubmit={handleLogin} className="flex flex-col gap-4">
          <InputField
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            error={errors.email}
          />

          <PasswordField
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            error={errors.password}
          />

          <div className="text-right text-sm">
            <Link to="/forgot-password" className="text-orange hover:underline">
              Forgot password?
            </Link>
          </div>

          <button
            type="submit"
            disabled={!isFormValid || isLoading}
            className={`bg-orange text-white py-2 rounded-button mt-2 transition-all flex justify-center items-center
              ${(!isFormValid || isLoading) ? 'opacity-50 cursor-not-allowed' : 'hover:opacity-90'}
            `}
          >
            {isLoading ? (
               // Simple Loading Spinner or Text
               <span>Logging in...</span>
            ) : (
               "Log In"
            )}
          </button>
        </form>

        <div className="flex items-center my-4">
          <div className="flex-grow border-t border-gray-600"></div>
          <span className="px-2 text-gray-400">OR</span>
          <div className="flex-grow border-t border-gray-600"></div>
        </div>

        <button
          onClick={handleGoogleLogin}
          type="button" 
          disabled={isLoading}
          className="flex items-center justify-center gap-2 bg-background border border-gray-600 py-2 rounded-button w-full hover:bg-gray-800 transition-colors"
        >
          <img src="src/assets/google-icon-1.png" alt="Google Icon" className="w-7 h-7" />
          Log in with Google
        </button>

        <div className="text-center text-sm mt-4">
          <span>Don’t have an account? </span>
          <Link to="/sign-up" className="text-orange hover:underline">
            Sign up
          </Link>
        </div>
      </div>
    </div>
  );
}