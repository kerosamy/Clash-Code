import { useEffect, useState } from "react";

interface MatchIntroAnimationProps {
  player1: {
    username: string;
    avatarUrl: string;
    rank: string;
  };
  player2: {
    username: string;
    avatarUrl: string;
    rank: string;
  };
  onComplete: () => void;
}

const rankColors: Record<string, string> = {
  BRONZE: "#CD7F32",
  SILVER: "#C0C0C0",
  GOLD: "#FFD700",
  DIAMOND: "#00FFFF",
  MASTER: "#8A2BE2",
  CHAMPION: "#4169E1",
  LEGEND: "#80FFA1",
};

export default function MatchIntroAnimation({ player1, player2, onComplete }: MatchIntroAnimationProps) {
  const [stage, setStage] = useState<'slide-in' | 'pulse' | 'fade-out'>('slide-in');

  useEffect(() => {
    const slideTimer = setTimeout(() => setStage('pulse'), 1500);
    const pulseTimer = setTimeout(() => setStage('fade-out'), 3500);
    const completeTimer = setTimeout(() => onComplete(), 4500);

    return () => {
      clearTimeout(slideTimer);
      clearTimeout(pulseTimer);
      clearTimeout(completeTimer);
    };
  }, [onComplete]);

  const getRankColor = (rank: string): string => {
    return rankColors[rank.toUpperCase()] || '#FFFFFF';
  };

  return (
    <div className={`fixed inset-0 z-50 flex items-center justify-center bg-sidebar transition-opacity duration-1000
      ${stage === 'fade-out' ? 'opacity-0' : 'opacity-100'}`}>
      
      {/* Animated background with scanlines */}
      <div className="absolute inset-0 overflow-hidden">
        {/* Dark gradient overlay matching theme */}
        <div className="absolute inset-0 bg-gradient-to-br from-sidebar via-background to-container" />
        
        {/* Horizontal scanlines */}
        <div className="absolute inset-0 opacity-5"
          style={{
            backgroundImage: 'repeating-linear-gradient(0deg, transparent, transparent 2px, #D1D5DB 2px, #D1D5DB 4px)'
          }}
        />
        
        {/* Glowing particles */}
        {[...Array(30)].map((_, i) => (
          <div
            key={i}
            className="absolute w-1 h-1 bg-text rounded-full animate-pulse"
            style={{
              left: `${Math.random() * 100}%`,
              top: `${Math.random() * 100}%`,
              animationDelay: `${Math.random() * 2}s`,
              animationDuration: `${2 + Math.random() * 2}s`,
              opacity: Math.random() * 0.5 + 0.2
            }}
          />
        ))}

        {/* Corner accents with theme colors */}
        <div className="absolute top-0 left-0 w-32 h-32 border-t-4 border-l-4 border-diamond/50" />
        <div className="absolute top-0 right-0 w-32 h-32 border-t-4 border-r-4 border-orange/50" />
        <div className="absolute bottom-0 left-0 w-32 h-32 border-b-4 border-l-4 border-master/50" />
        <div className="absolute bottom-0 right-0 w-32 h-32 border-b-4 border-r-4 border-loseRed/50" />
      </div>

      {/* Player 1 */}
      <div className={`absolute left-0 flex flex-col items-center gap-6 transition-all duration-1000 ease-out
        ${stage === 'slide-in' ? '-translate-x-full opacity-0' : 'translate-x-0 opacity-100'}`}
        style={{ left: '12%' }}>
        
        {/* Player 1 Avatar with advanced glow */}
        <div className="relative">
          {/* Multiple glow layers */}
          <div 
            className="absolute inset-0 rounded-full blur-3xl animate-pulse"
            style={{ 
              backgroundColor: getRankColor(player1.rank),
              opacity: 0.6,
              transform: 'scale(1.3)'
            }}
          />
          <div 
            className="absolute inset-0 rounded-full blur-xl"
            style={{ 
              backgroundColor: getRankColor(player1.rank),
              opacity: 0.8,
              transform: 'scale(1.15)'
            }}
          />
          
          {/* Avatar with border */}
          <div 
            className="relative w-64 h-64 rounded-full border-8 overflow-hidden shadow-2xl bg-container"
            style={{ 
              borderColor: getRankColor(player1.rank),
              boxShadow: `0 0 40px ${getRankColor(player1.rank)}80`
            }}
          >
            <img
              src={player1.avatarUrl}
              alt={player1.username}
              className="w-full h-full object-cover"
            />
          </div>
          
          {/* Rank badge with glow */}
          <div 
            className="absolute -bottom-4 left-1/2 -translate-x-1/2 px-6 py-2 rounded-button font-bold text-lg shadow-lg border-2 bg-container"
            style={{ 
              backgroundColor: getRankColor(player1.rank),
              borderColor: 'rgba(209, 213, 219, 0.3)',
              color: '#000000',
              boxShadow: `0 4px 20px ${getRankColor(player1.rank)}80`
            }}
          >
            {player1.rank}
          </div>

          {/* Orbiting particles */}
          {[...Array(8)].map((_, i) => (
            <div
              key={i}
              className="absolute w-2 h-2 rounded-full"
              style={{
                backgroundColor: getRankColor(player1.rank),
                top: '50%',
                left: '50%',
                animation: `orbit 3s linear infinite`,
                animationDelay: `${i * 0.375}s`,
                transform: `rotate(${i * 45}deg) translateX(150px)`,
                boxShadow: `0 0 10px ${getRankColor(player1.rank)}`
              }}
            />
          ))}
        </div>

        {/* Player 1 Name */}
        <div className="text-center mt-4">
          <h2 
            className="text-5xl font-black tracking-wider drop-shadow-2xl font-anta"
            style={{ 
              color: getRankColor(player1.rank),
              textShadow: `0 0 20px ${getRankColor(player1.rank)}80, 0 0 40px ${getRankColor(player1.rank)}40`
            }}
          >
            {player1.username}
          </h2>
        </div>

        {/* Energy beam effect */}
        <div className="absolute top-1/2 left-full w-40 h-1 -translate-y-1/2">
          <div 
            className="h-full animate-pulse"
            style={{ 
              background: `linear-gradient(to right, ${getRankColor(player1.rank)}, transparent)`,
              boxShadow: `0 0 20px ${getRankColor(player1.rank)}`
            }}
          />
        </div>
      </div>

      {/* VS Badge in Center - Enhanced */}
      <div className={`relative z-10 transition-all duration-500
        ${stage === 'pulse' ? 'scale-125' : 'scale-100'}`}>
        
        {/* Multiple background glows with theme colors */}
        <div className="absolute inset-0 flex items-center justify-center">
          <div className="w-64 h-64 rounded-full bg-orange/40 blur-3xl animate-pulse" 
            style={{ filter: 'blur(60px)' }} />
        </div>
        <div className="absolute inset-0 flex items-center justify-center">
          <div className="w-48 h-48 rounded-full bg-loseRed/30 blur-2xl animate-pulse" 
            style={{ animationDelay: '0.5s', filter: 'blur(40px)' }} />
        </div>

        {/* VS text with enhanced 3D effect */}
        <div className="relative font-black italic flex items-center justify-center font-anta">
          <div className="relative">
            {/* Multiple shadow layers for depth */}
            <span 
              className="absolute text-[12rem]"
              style={{ 
                transform: 'translate(12px, 12px)',
                color: '#FF242460'
              }}
            >
              VS
            </span>
            <span 
              className="absolute text-[12rem]"
              style={{ 
                transform: 'translate(8px, 8px)',
                color: '#EC743860'
              }}
            >
              VS
            </span>
            <span 
              className="absolute text-[12rem]"
              style={{ 
                transform: 'translate(4px, 4px)',
                color: '#FFD70040'
              }}
            >
              VS
            </span>
            
            {/* Main text with gradient and glow */}
            <span 
              className="relative text-[12rem]"
              style={{
                background: 'linear-gradient(135deg, #FFD700, #EC7438, #FF2424)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                backgroundClip: 'text',
                filter: 'drop-shadow(0 0 30px rgba(236, 116, 56, 0.9)) drop-shadow(0 0 60px rgba(255, 36, 36, 0.6))'
              }}
            >
              VS
            </span>
          </div>
        </div>

        {/* Lightning bolts from VS */}
        {[...Array(4)].map((_, i) => (
          <div
            key={i}
            className="absolute top-1/2 left-1/2 bg-orange"
            style={{
              width: '2px',
              height: '80px',
              background: `linear-gradient(to bottom, #EC7438, transparent)`,
              transform: `translate(-50%, -50%) rotate(${i * 90}deg) translateY(-120px)`,
              boxShadow: '0 0 10px #EC7438',
              animation: 'pulse 1.5s ease-in-out infinite',
              animationDelay: `${i * 0.2}s`
            }}
          />
        ))}
      </div>

      {/* Player 2 */}
      <div className={`absolute right-0 flex flex-col items-center gap-6 transition-all duration-1000 ease-out
        ${stage === 'slide-in' ? 'translate-x-full opacity-0' : 'translate-x-0 opacity-100'}`}
        style={{ right: '12%' }}>
        
        {/* Player 2 Avatar with advanced glow */}
        <div className="relative">
          {/* Multiple glow layers */}
          <div 
            className="absolute inset-0 rounded-full blur-3xl animate-pulse"
            style={{ 
              backgroundColor: getRankColor(player2.rank),
              opacity: 0.6,
              transform: 'scale(1.3)'
            }}
          />
          <div 
            className="absolute inset-0 rounded-full blur-xl"
            style={{ 
              backgroundColor: getRankColor(player2.rank),
              opacity: 0.8,
              transform: 'scale(1.15)'
            }}
          />
          
          {/* Avatar with border */}
          <div 
            className="relative w-64 h-64 rounded-full border-8 overflow-hidden shadow-2xl bg-container"
            style={{ 
              borderColor: getRankColor(player2.rank),
              boxShadow: `0 0 40px ${getRankColor(player2.rank)}80`
            }}
          >
            <img
              src={player2.avatarUrl}
              alt={player2.username}
              className="w-full h-full object-cover"
            />
          </div>
          
          {/* Rank badge with glow */}
          <div 
            className="absolute -bottom-4 left-1/2 -translate-x-1/2 px-6 py-2 rounded-button font-bold text-lg shadow-lg border-2 bg-container"
            style={{ 
              backgroundColor: getRankColor(player2.rank),
              borderColor: 'rgba(209, 213, 219, 0.3)',
              color: '#000000',
              boxShadow: `0 4px 20px ${getRankColor(player2.rank)}80`
            }}
          >
            {player2.rank}
          </div>

          {/* Orbiting particles */}
          {[...Array(8)].map((_, i) => (
            <div
              key={i}
              className="absolute w-2 h-2 rounded-full"
              style={{
                backgroundColor: getRankColor(player2.rank),
                top: '50%',
                left: '50%',
                animation: `orbit 3s linear infinite`,
                animationDelay: `${i * 0.375}s`,
                transform: `rotate(${i * 45}deg) translateX(150px)`,
                boxShadow: `0 0 10px ${getRankColor(player2.rank)}`
              }}
            />
          ))}
        </div>

        {/* Player 2 Name */}
        <div className="text-center mt-4">
          <h2 
            className="text-5xl font-black tracking-wider drop-shadow-2xl font-anta"
            style={{ 
              color: getRankColor(player2.rank),
              textShadow: `0 0 20px ${getRankColor(player2.rank)}80, 0 0 40px ${getRankColor(player2.rank)}40`
            }}
          >
            {player2.username}
          </h2>
        </div>

        {/* Energy beam effect */}
        <div className="absolute top-1/2 right-full w-40 h-1 -translate-y-1/2">
          <div 
            className="h-full animate-pulse"
            style={{ 
              background: `linear-gradient(to left, ${getRankColor(player2.rank)}, transparent)`,
              boxShadow: `0 0 20px ${getRankColor(player2.rank)}`
            }}
          />
        </div>
      </div>

      {/* Bottom text with enhanced styling */}
      <div className={`absolute bottom-20 left-1/2 -translate-x-1/2 text-center transition-all duration-1000
        ${stage === 'slide-in' ? 'opacity-0 translate-y-10' : 'opacity-100 translate-y-0'}`}>
        <p className="text-4xl font-bold text-white tracking-[0.3em] font-anta"
          style={{
            textShadow: '0 0 20px rgba(255,255,255,0.5), 0 0 40px rgba(236, 116, 56, 0.4)',
            animation: 'pulse 2s ease-in-out infinite'
          }}>
          PREPARE FOR BATTLE
        </p>
      </div>

      <style>{`
        @keyframes orbit {
          from {
            transform: rotate(0deg) translateX(150px);
          }
          to {
            transform: rotate(360deg) translateX(150px);
          }
        }
      `}</style>
    </div>
  );
}