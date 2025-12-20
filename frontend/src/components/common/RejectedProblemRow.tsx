import React, { useState } from "react";

// Adjusted Grid: Index, Name, Rejection Note
export const REJECTED_GRID = 
  "grid grid-cols-[80px_1fr_2fr] gap-4 px-6 py-4 items-center border-b border-white/5 last:border-0 " +
  "[&>*:first-child]:justify-self-center";

interface RejectedProblemRowProps {
  id: number;
  index: number;
  name: string;
  rejectionNote: string;
  onRowClick: () => void;
}

export default function RejectedProblemRow({
  index,
  name,
  rejectionNote,
  onRowClick,
}: RejectedProblemRowProps) {
  const [showNote, setShowNote] = useState(false);

  return (
    <>
      <div
        onClick={onRowClick}
        className={`${REJECTED_GRID} hover:bg-sidebar/30 transition-all duration-200 group cursor-pointer`}
      >
        {/* Index */}
        <span className="text-text/60 group-hover:text-text font-anta text-xs truncate">
          {index}
        </span>

        {/* Name */}
        <span className="text-text font-anta text-sm truncate" title={name}>
          {name}
        </span>

        {/* Rejection Note Clickable */}
        <span 
          onClick={(e) => {
            e.stopPropagation(); // Prevent navigating to the problem
            setShowNote(true);
          }}
          className="text-orange/80 hover:text-orange text-sm font-anta truncate cursor-help italic underline underline-offset-4 decoration-white/10"
        >
          {rejectionNote}
        </span>
      </div>

      {/* Rejection Note Popup Overlay */}
      {showNote && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
          <div className="bg-[#1a1a1a] border border-white/10 p-6 rounded-2xl max-w-md w-full shadow-2xl">
            <h3 className="text-orange font-anta text-lg mb-2">Rejection Reason</h3>
            <p className="text-text/90 font-sans leading-relaxed mb-6">
              {rejectionNote}
            </p>
            <button
              onClick={() => setShowNote(false)}
              className="w-full py-2 bg-white/5 hover:bg-white/10 border border-white/10 text-white rounded-lg transition-colors font-anta"
            >
              Close
            </button>
          </div>
        </div>
      )}
    </>
  );
}