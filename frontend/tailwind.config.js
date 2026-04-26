/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      // MAIN COLORS
      colors: {
        sidebar: "#0B0F1B",
        background: "#151924",
        container: "#212530",
        orange: "#EC7438",
        black: "#000000",
        white: "#FFFFFF",
        text: "#D1D5DB",

        // STATUS COLORS
        winGreen: "#81B64C",
        loseRed: "#FF2424",

        // RANK COLORS
        bronze: "#CD7F32",
        silver: "#C0C0C0",
        gold: "#FFD700",
        diamond: "#00FFFF",
        master: "#8A2BE2",
        champion: "#4169E1",
        legend: "#80FFA1",

        // STATUS COLORS
        statusSolved: "#22C55E",      // green-500
        statusAttempted: "#EAB308",   // yellow-500
        statusUnsolved: "#EF4444",    // red-500",

        // DIFFICULTY COLORS
        difficultyEasy: "#22C55E",   
        difficultyMedium: "#EAB308", 
        difficultyHard: "#EF4444",  
        
      },

      fontFamily: {
        anta: ["Anta", "sans-serif"],
      },

      // CUSTOM FONT SIZES
      fontSize: {
        "container-title": ["1.25rem", { lineHeight: "1.3" }],
        "container-list": ["0.9375rem", { lineHeight: "1.8" }],
        "sideBar-list": ["1.1rem", { lineHeight: "1.5" }],
      },

      // CUSTOM SPACING
      spacing: {
        "scroll-x": "2rem",
        "sideBar-pad": "0.75rem",
      },

      // FIXED SIDEBAR WIDTH (stable + elegant)
      width: {
        sidebar: "15%",
        icon: "1.5rem",
      },
      minWidth: {
        sidebar: "200px",
      },

      // BUTTON RADII
      borderRadius: {
        button: "12px",
      },
    },
  },
  plugins: [],
};
