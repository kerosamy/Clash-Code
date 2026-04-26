// src/utils/validationUtils.ts

export interface ValidationResult {
  isValid: boolean;
  error?: string;
}

/**
 * Validates email format
 */
export const validateEmail = (email: string): ValidationResult => {
  if (!email.trim()) {
    return { isValid: false, error: 'Email is required' };
  }
  
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    return { isValid: false, error: 'Please enter a valid email address.' };
  }
  
  return { isValid: true };
};

/**
 * Validates username format
 * - Must be 4-32 characters
 * - Only letters, numbers, underscore, and hyphen allowed
 */
export const validateUsername = (username: string): ValidationResult => {
  if (!username.trim()) {
    return { isValid: false, error: 'Username is required' };
  }
  
  if (username.length < 4) {
    return { isValid: false, error: 'Username is too short' };
  }
  
  if (!/^[a-zA-Z0-9_-]{4,32}$/.test(username)) {
    return { 
      isValid: false, 
      error: "Username must be 4–32 characters and contain only letters, numbers, '_' or '-'." 
    };
  }
  
  return { isValid: true };
};

/**
 * Validates password strength
 * - Must be 8-64 characters
 */
export const validatePassword = (password: string): ValidationResult => {
  if (!password.trim()) {
    return { isValid: false, error: 'Password is required' };
  }
  
  if (password.length < 8 || password.length > 64) {
    return { isValid: false, error: 'Password must be 8–64 characters long.' };
  }
  
  return { isValid: true };
};

/**
 * Validates that two passwords match
 */
export const validatePasswordMatch = (
  password: string, 
  confirmPassword: string
): ValidationResult => {
  if (password !== confirmPassword) {
    return { isValid: false, error: 'Passwords do not match.' };
  }
  
  return { isValid: true };
};

/**
 * Validates recovery answer
 * - Must be 2-100 characters
 */
export const validateRecoveryAnswer = (answer: string): ValidationResult => {
  if (!answer.trim()) {
    return { isValid: false, error: 'Please enter an answer.' };
  }
  
  if (answer.length < 2 || answer.length > 100) {
    return { isValid: false, error: 'Recovery answer must be 2–100 characters.' };
  }
  
  return { isValid: true };
};

/**
 * Validates recovery question is selected
 */
export const validateRecoveryQuestion = (question: string): ValidationResult => {
  if (!question.trim()) {
    return { isValid: false, error: 'Please select a recovery question.' };
  }
  
  return { isValid: true };
};

/**
 * Validates email or username for login
 * - Can be either email or username (min 4 chars)
 */
export const validateEmailOrUsername = (value: string): ValidationResult => {
  if (!value.trim()) {
    return { isValid: false, error: 'Email/Username is required' };
  }
  
  if (value.length < 4) {
    return { isValid: false, error: 'Username/Email is too short' };
  }
  
  return { isValid: true };
};