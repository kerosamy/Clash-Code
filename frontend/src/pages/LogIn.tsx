import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import InputField from '../components/authentication/InputField';
import PasswordField from '../components/authentication/PasswordField';
import { loginUser, type LoginRequest } from '../services/AuthService';
import { getUsername } from '../utils/jwtDecoder';
import { validateEmailOrUsername, validatePassword } from '../utils/validation';
import { getOnGoingMatch } from '../services/MatchService';
import AnimatedBackground from "../components/AnimatedBackground";
import { API_BASE } from '../services/api';

export default function LogIn() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  
  const [errors, setErrors] = useState({ email: '', password: '' });
  const [apiError, setApiError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();

    // 1. Reset Errors
    const newErrors = { email: '', password: '' };
    setApiError('');
    let formValid = true;

    // 2. Validate Email/Username using provided validation util
    const emailValidation = validateEmailOrUsername(email);
    if (!emailValidation.isValid) {
      newErrors.email = emailValidation.error!;
      formValid = false;
    }

    // 3. Validate Password using provided validation util
    const passwordValidation = validatePassword(password);
    if (!passwordValidation.isValid) {
      newErrors.password = passwordValidation.error!;
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

      await loginUser(credentials);
    
      const activeMatch = await getOnGoingMatch();

      if(activeMatch) {
        navigate(`/play-game/${activeMatch}`, { replace: true });
      } else {
        navigate(`/profile/${getUsername()}/overview`, { replace: true });
      }

    } catch (err: any) {
      setApiError(err || 'Login failed. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleGoogleLogin = () => {
    sessionStorage.setItem('oauth_flow', 'login');
    window.location.href = `${API_BASE}/oauth2/authorization/google`;
  };

  // Basic check for button disable state (UX optimization)
  const isFormValid = email.trim().length >= 4 && password.trim().length >= 8;

  return (
    <div className="min-h-screen flex items-center justify-center bg-background font-anta relative overflow-hidden">
            <AnimatedBackground />
      <div className="bg-container p-8 rounded-button shadow-lg w-full max-w-md text-white relative z-10">
        <div className="flex justify-center mb-6">
          <img src="/src/assets/logo.svg" alt="App Logo" className="w-48" />
        </div>

        {apiError && (
          <div className="mb-4 p-3 bg-red-500/20 border border-red-500 text-red-100 text-sm rounded text-center">
            {apiError}
          </div>
        )}

        <form onSubmit={handleLogin} className="flex flex-col gap-4">
          <InputField
            placeholder="Email"
            value={email}
            onChange={(e) => {
              setEmail(e.target.value);
              if (errors.email) setErrors(prev => ({ ...prev, email: '' }));
            }}
            error={errors.email}
          />

          <PasswordField
            placeholder="Password"
            value={password}
            onChange={(e) => {
              setPassword(e.target.value);
              if (errors.password) setErrors(prev => ({ ...prev, password: '' }));
            }}
            error={errors.password}
          />

          <div className="text-right text-sm">
            <Link to="/password-recovery" className="text-orange hover:underline">
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
            {isLoading ? <span>Logging in...</span> : "Log In"}
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
          <span>Don't have an account? </span>
          <Link to="/sign-up" className="text-orange hover:underline">
            Sign up
          </Link>
        </div>
      </div>
    </div>
  );
}
