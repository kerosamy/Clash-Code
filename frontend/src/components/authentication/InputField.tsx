import React from "react";

interface InputFieldProps {
  type?: string;
  placeholder: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  error?: string;
}

export default function InputField({
  type = "text",
  placeholder,
  value,
  onChange,
  error,
}: InputFieldProps) {
  return (
    <div>
      <input
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        className="w-full p-3 rounded-button bg-background text-white placeholder-gray-400 border border-gray-600"
      />
      {error && <p className="text-red-500 text-sm">{error}</p>}
    </div>
  );
}
