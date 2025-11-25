import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import InputField from '../components/authentication/InputField';
import PasswordField from '../components/authentication/PasswordField';

export default function LogIn() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState({ email: '', password: '' });
  const navigate = useNavigate();

  const usernameRegex = /^[a-zA-Z0-9_-]{4,100}$/;

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();

    const newErrors = { email: '', password: '' };
    let formValid = true;

    // Validate email/username
    if (!email.trim()) {
      newErrors.email = 'Email/Username is required';
      formValid = false;
    } else if (!usernameRegex.test(email)) {
      newErrors.email =
        '4–32 characters, only letters, numbers, "_" or "-" allowed';
      formValid = false;
    }

    // Validate password
    if (!password.trim()) {
      newErrors.password = 'Password is required';
      formValid = false;
    } else if (password.length < 8 || password.length > 64) {
      newErrors.password = 'Password must be 8–64 characters long';
      formValid = false;
    }

    setErrors(newErrors);

    if (!formValid) return;

    // TODO: Implement login API
    navigate('/profile');
  };

  const handleGoogleLogin = () => {
    sessionStorage.setItem('oauth_flow', 'login');
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  const isFormValid =
    usernameRegex.test(email) && password.length >= 8 && password.length <= 64;

  return (
    <div className="min-h-screen flex items-center justify-center bg-background font-anta">
      <div className="bg-container p-8 rounded-button shadow-lg w-full max-w-md text-white">
        <div className="flex justify-center mb-6">
          <img src="/src/assets/logo.svg" alt="App Logo" className="w-48" />
        </div>

        <form onSubmit={handleLogin} className="flex flex-col gap-4">
          <InputField
            placeholder="Username, Email"
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
            disabled={!isFormValid}
            className={`bg-orange text-white py-2 rounded-button mt-2 transition-opacity
              ${!isFormValid ? 'opacity-50 cursor-not-allowed' : 'hover:opacity-90'}
            `}
          >
            Log In
          </button>
        </form>

        <div className="flex items-center my-4">
          <div className="flex-grow border-t border-gray-600"></div>
          <span className="px-2 text-gray-400">OR</span>
          <div className="flex-grow border-t border-gray-600"></div>
        </div>

        <button
          onClick={ handleGoogleLogin } 
          className="flex items-center justify-center gap-2 bg-background border border-gray-600 py-2 rounded-button w-full"
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
