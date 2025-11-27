import type { FC } from "react";

interface LoadingProps {
  message?: string;
  minHeight?: string;
}

const Loading: FC<LoadingProps> = ({ 
  message = "Loading...", 
  minHeight = "60vh" 
}) => {
  return (
    <div className="flex items-center justify-center" style={{ minHeight }}>
      <div className="text-center">
        <div className="relative w-16 h-16 mx-auto mb-4">
          <div className="absolute inset-0 border-4 border-container rounded-full"></div>
          <div className="absolute inset-0 border-4 border-primary border-t-transparent rounded-full animate-spin"></div>
        </div>
        <p className="text-text font-anta text-lg">{message}</p>
      </div>
    </div>
  );
};

export default Loading;