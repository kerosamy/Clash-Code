
export default function AnimatedBackground() {
  const colors = {
    background: "#151924",
    container: "#212530",
    orange: "#EC7438",
    black: "#000000",
  };

  // Creating a 15x15 tactical grid
  const cells = Array.from({ length: 225 });

  return (
    <div className="absolute inset-0 overflow-hidden pointer-events-none" style={{ backgroundColor: colors.background }}>
      {/* 1. Original Animations */}
      <style>{`
        @keyframes float {
          0%, 100% { transform: translate(0, 0) scale(1); }
          33% { transform: translate(30px, -30px) scale(1.1); }
          66% { transform: translate(-20px, 20px) scale(0.9); }
        }
        @keyframes drift {
          0%, 100% { transform: translateY(0px); }
          50% { transform: translateY(-20px); }
        }
        @keyframes driftX {
          0%, 100% { transform: translateX(0px); }
          50% { transform: translateX(20px); }
        }
        .float-animate { animation: float 20s ease-in-out infinite; }
        .drift-y { animation: drift 15s ease-in-out infinite; }
        .drift-x { animation: driftX 12s ease-in-out infinite; }
        
        /* Tactical Diamond Shape */
        .diamond {
          clip-path: polygon(50% 0%, 100% 50%, 50% 100%, 0% 50%);
        }
      `}</style>

      {/* 2. Interactive Diamond Grid */}
      <div className="absolute inset-0 flex items-center justify-center opacity-20 pointer-events-auto">
        <div className="grid grid-cols-[repeat(15,minmax(0,1fr))] w-[120vw] h-[120vh] rotate-[15deg]">
          {cells.map((_, i) => (
            <div
              key={i}
              className="diamond border border-white/5 transition-all duration-700 ease-out hover:duration-75"
              style={{ backgroundColor: `${colors.container}44` }}
              onMouseEnter={(e) => {
                const target = e.target as HTMLElement; // <-- type assertion
                target.style.backgroundColor = colors.orange;
                target.style.transform = "scale(1.1) translateZ(20px)";
                target.style.opacity = "0.8";
              }}
              onMouseLeave={(e) => {
                const target = e.target as HTMLElement; // <-- type assertion
                target.style.backgroundColor = `${colors.container}44`;
                target.style.transform = "scale(0.95)";
                target.style.opacity = "1";
              }}
            />
          ))}
        </div>
      </div>

      {/* 3. Original Floating Objects (Restored) */}
      <div className="absolute inset-0 pointer-events-none">
         {/* Background Orbs */}
         <div className="absolute -top-20 -left-20 w-96 h-96 bg-gradient-to-br from-[#EC7438]/20 via-[#EC7438]/10 to-transparent rounded-full blur-3xl float-animate"></div>
         <div className="absolute top-1/4 -right-32 w-[500px] h-[500px] bg-gradient-to-bl from-blue-500/10 via-blue-600/5 to-transparent rounded-full blur-3xl float-animate" style={{ animationDelay: '-5s' }}></div>

         {/* Code Symbols */}
         <div className="absolute top-20 left-20 text-6xl font-mono opacity-25 drift-y" style={{ color: colors.orange }}>&lt;/&gt;</div>
         <div className="absolute top-32 right-40 text-blue-400 text-7xl font-mono opacity-20 float-animate" style={{ animationDelay: '3s' }}>&#123;&#125;</div>
         <div className="absolute bottom-40 left-32 text-blue-300 text-7xl font-mono opacity-20 drift-x" style={{ animationDelay: '2s' }}>[]</div>
         <div className="absolute bottom-24 right-24 text-6xl font-mono opacity-25 float-animate" style={{ color: colors.orange, animationDelay: '4s' }}>()</div>

         {/* Binary Streams */}
         <div className="absolute top-0 left-1/4 text-xs font-mono whitespace-pre leading-relaxed drift-y opacity-20" style={{ color: colors.orange }}>
            01001000<br/>10110101<br/>11010010
         </div>
         <div className="absolute bottom-10 right-1/4 text-blue-300/20 text-xs font-mono whitespace-pre leading-relaxed drift-y" style={{ animationDelay: '4s' }}>
            01101001<br/>11100101<br/>00010110
         </div>
         
      </div>

      {/* 4. Vignette Layer */}
      <div className="absolute inset-0 shadow-[inset_0_0_200px_#000000] opacity-90 pointer-events-none"></div>
    </div>
  );
}