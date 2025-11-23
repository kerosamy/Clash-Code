import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import InputField from '../components/authentication/InputField';
import PasswordField from '../components/authentication/PasswordField';

export default function LogIn() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState({ email: '', password: '' });

  const navigate = useNavigate();

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();

    let formValid = true;
    const newErrors = { email: '', password: '' };

    if (!email.trim()) {
      newErrors.email = 'Email/Username is required';
      formValid = false;
    }
    if (!password.trim()) {
      newErrors.password = 'Password is required';
      formValid = false;
    }

    setErrors(newErrors);

    if (!formValid) return;

    // TODO: Implement login API call
    navigate('/profile');
  };

  const handleGoogleLogin = () => {
    // TODO: Implement Google login
  };

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
            disabled={!email || !password}
            className={`bg-orange text-white py-2 rounded-button mt-2 transition-opacity
              ${!email || !password ? 'opacity-50 cursor-not-allowed' : 'hover:opacity-90'}
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
          onClick={handleGoogleLogin}
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
