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
        "scroll-x": "1rem",
        "sideBar-pad": "0.75rem",
      },

      // FIXED SIDEBAR WIDTH (stable + elegant)
      width: {
        sidebar: "15%",
      },
      minWidth: {
        sidebar: "150px",
      },

      // BUTTON RADII
      borderRadius: {
        button: "12px",
      },
    },
  },
  plugins: [],
};
