import { useState } from 'react'

function App() {
  const [liked, setLiked] = useState(false)

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center">
      <div className="bg-white p-8 rounded-lg shadow-lg">
        <h1 className="text-3xl font-bold text-gray-800 mb-6 text-center">
          Tailwind Setup Complete ✓
        </h1>
        
        <button
          onClick={() => setLiked(!liked)}
          className={`
            px-6 py-3 rounded-lg font-semibold transition-all transform
            ${liked 
              ? 'bg-red-500 hover:bg-red-600 text-white scale-110' 
              : 'bg-gray-200 hover:bg-gray-300 text-gray-700'
            }
          `}
        >
          {liked ? '❤️ Liked' : '🤍 Like'}
        </button>
      </div>
    </div>
  )
}

export default App