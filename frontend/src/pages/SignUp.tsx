import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

export default function SignUp() {
  const [emailOrHandle, setEmailOrHandle] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleSignUp = (e: React.FormEvent) => {
    e.preventDefault();
    // TODO: Implement signup API call
    navigate('/profile'); // Redirect to profile or any onboarding page
  };

  const handleGoogleSignUp = () => {
    // TODO: Implement Google signup
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background font-anta">
      <div className="bg-container p-8 rounded-button shadow-lg w-full max-w-md text-white">
        <div className="flex justify-center mb-6">
          <img src="/src/assets/logo.svg" alt="App Logo" className="w-48" />
        </div>

        <form onSubmit={handleSignUp} className="flex flex-col gap-4">
          <div>
            <input
              type="text"
              placeholder="Email or Handle"
              value={emailOrHandle}
              onChange={(e) => setEmailOrHandle(e.target.value)}
              className="w-full p-3 rounded-button bg-background text-white placeholder-gray-400 focus:outline-none border border-gray-600"
            />
          </div>
          <div className="relative">
            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full p-3 rounded-button bg-background text-white placeholder-gray-400 focus:outline-none border border-gray-600"
            />
          </div>

          <button
            type="submit"
            className="bg-orange hover:opacity-90 text-white py-2 rounded-button mt-2"
          >
            Sign Up
          </button>
        </form>

        <div className="flex items-center my-4">
          <div className="flex-grow border-t border-gray-600"></div>
          <span className="px-2 text-gray-400">OR</span>
          <div className="flex-grow border-t border-gray-600"></div>
        </div>

        <button
          onClick={handleGoogleSignUp}
          className="flex items-center justify-center gap-2 bg-background border border-gray-600 py-2 rounded-button w-full"
        >
          <img src="src/assets/google-icon-1.png" alt="Google Icon" className="w-7 h-7" />
          Sign up with Google
        </button>

        <div className="text-center text-sm mt-4">
          <span>Already have an account? </span>
          <Link to="/login" className="text-orange hover:underline">
            Log in
          </Link>
        </div>
      </div>
    </div>
  );
}
