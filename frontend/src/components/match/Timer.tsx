import { useState, useEffect, useRef } from "react";

interface DraggableTimerProps {
  startAt: string;
  durationMinutes: number;
  isMatchOver: boolean;
  onTimeExpire?: () => void;
}

export default function DraggableTimer({ 
  startAt, 
  durationMinutes, 
  isMatchOver, 
  onTimeExpire,
}: DraggableTimerProps) {
  
  const [timeLeft, setTimeLeft] = useState<number>(0);
  const [isMinimized, setIsMinimized] = useState(false);
  
  const [position, setPosition] = useState({ x: window.innerWidth - 220, y: 100 });
  const [isDragging, setIsDragging] = useState(false);
  
  const dragOffset = useRef({ x: 0, y: 0 });
  const hasDragged = useRef(false); 

  useEffect(() => {
    const calculateTime = () => {
      if (isMatchOver) {
        return;
      }
      const startTime = new Date(startAt).getTime();
      const endTime = startTime + durationMinutes * 60 * 1000;
      const now = new Date().getTime();
      const diff = endTime - now;

      if (diff <= 0) {
        setTimeLeft(0);
        if (onTimeExpire) onTimeExpire();
        return;
      }
      setTimeLeft(diff);
    };

    calculateTime();
    if (!isMatchOver) {
        const timer = setInterval(calculateTime, 1000);
        return () => clearInterval(timer);
    }
  }, [startAt, durationMinutes, isMatchOver]);

  const handleMouseDown = (e: React.MouseEvent) => {
    if ((e.target as HTMLElement).closest('button')) return;
    
    hasDragged.current = false; 
    setIsDragging(true);
    dragOffset.current = { x: e.clientX - position.x, y: e.clientY - position.y };
  };

  useEffect(() => {
    const handleMouseMove = (e: MouseEvent) => {
      if (!isDragging) return;
      
      hasDragged.current = true; 

      let newX = e.clientX - dragOffset.current.x;
      let newY = e.clientY - dragOffset.current.y;
      
      newX = Math.max(0, Math.min(newX, window.innerWidth - 50));
      newY = Math.max(0, Math.min(newY, window.innerHeight - 50));
      
      setPosition({ x: newX, y: newY });
    };
    
    const handleMouseUp = () => setIsDragging(false);

    if (isDragging) {
      window.addEventListener("mousemove", handleMouseMove);
      window.addEventListener("mouseup", handleMouseUp);
    }
    return () => {
      window.removeEventListener("mousemove", handleMouseMove);
      window.removeEventListener("mouseup", handleMouseUp);
    };
  }, [isDragging]);

  const formatTime = (ms: number) => {
    const totalSeconds = Math.floor(ms / 1000);
    const m = Math.floor(totalSeconds / 60);
    const s = totalSeconds % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  const getTimerColor = () => {
    if (isMatchOver || timeLeft === 0) return "text-text"; 
    const minutesLeft = timeLeft / 60000;
    if (minutesLeft < 5) return "text-loseRed";
    if (minutesLeft < 15) return "text-statusAttempted";
    return "text-statusSolved";
  };

  if (isMinimized) {
    return (
      <div 
        style={{ left: position.x, top: position.y }}
        className="fixed z-50 cursor-pointer bg-container border border-sidebar p-3 rounded-full shadow-xl hover:bg-sidebar transition-transform hover:scale-110 flex items-center justify-center"
        onMouseDown={handleMouseDown} 
        onClick={() => {
            if (!hasDragged.current) {
                setIsMinimized(false);
            }
        }}
        title="Show Timer"
      >
        <span className="text-2xl">⏳</span>
      </div>
    );
  }

  return (
    <div
      style={{ left: position.x, top: position.y }}
      onMouseDown={handleMouseDown}
      className="fixed z-50 w-48 bg-container border border-orange/20 shadow-2xl rounded-xl backdrop-blur-md select-none overflow-hidden cursor-move active:cursor-grabbing"
    >
      <div className="flex items-center justify-between bg-sidebar px-3 py-2 border-b border-gray-700/50">
        <span className="text-xs text-text/80 font-anta uppercase tracking-wider font-bold">Time Left</span>
        
        <button
          onClick={() => setIsMinimized(true)}
          className="text-text hover:text-white transition-colors cursor-pointer p-1 rounded hover:bg-white/10"
          onMouseDown={(e) => e.stopPropagation()} 
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
            <path fillRule="evenodd" d="M3 10a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1z" clipRule="evenodd" />
          </svg>
        </button>
      </div>
      
      <div className="flex flex-col justify-center items-center py-4 bg-container gap-3">
        <span className={`text-4xl font-mono font-bold ${getTimerColor()} drop-shadow-md`}>
            {formatTime(timeLeft)}
        </span>

      </div>
    </div>
  );
}