import { useState } from "react";

function App() {
  const [liked, setLiked] = useState(false);

  return (
    <div className="min-h-screen bg-background flex font-anta">

      {/* SIDEBAR */}
      <aside className="bg-sidebar w-sidebar min-w-sidebar min-h-screen p-sideBar-pad text-white">
        <h2 className="text-xl font-bold mb-6">Sidebar Test</h2>

        <ul className="space-y-2">
          <li className="sidebar-list">Profile</li>
          <li className="sidebar-list">Friends</li>
          <li className="sidebar-list">Practice</li>
          <li className="sidebar-list">Matches</li>
        </ul>
      </aside>

      {/* MAIN CONTENT */}
      <main className="flex-1 p-10">

        {/* CONTAINER */}
        <div className="bg-container p-scroll-x rounded-lg shadow-lg text-white 
                        mx-scroll-x overflow-y-auto max-h-[70vh]">

          <h1 className="container-title mb-6">Template Title Test</h1>

          <p className="container-list mb-4">
            This paragraph tests basic text using your custom list font size.
          </p>

          {/* COLORS TEST */}
          <div className="grid grid-cols-2 gap-4 mt-6">
            <div className="p-4 rounded bg-orange text-black">Orange</div>
            <div className="p-4 rounded bg-winGreen text-black">Win Green</div>
            <div className="p-4 rounded bg-loseRed text-white">Lose Red</div>
            <div className="p-4 rounded bg-bronze text-black">Bronze</div>
            <div className="p-4 rounded bg-silver text-black">Silver</div>
            <div className="p-4 rounded bg-gold text-black">Gold</div>
            <div className="p-4 rounded bg-diamond text-black">Diamond</div>
            <div className="p-4 rounded bg-master text-white">Master</div>
            <div className="p-4 rounded bg-champion text-white">Champion</div>
            <div className="p-4 rounded bg-legend text-black">Legend</div>
          </div>

          {/* BUTTON TEST */}
          <div className="mt-10 flex justify-center">
            <button
              onClick={() => setLiked(!liked)}
              className={`
                px-6 py-3 rounded-button font-semibold transition-all transform mt-4
                ${
                  liked
                    ? "bg-winGreen hover:bg-green-600 text-black scale-110"
                    : "bg-gray-300 hover:bg-gray-400 text-black"
                }
              `}
            >
              {liked ? "❤️ Liked" : "🤍 Like"}
            </button>
          </div>

        </div>
      </main>
    </div>
  );
}

export default App;
