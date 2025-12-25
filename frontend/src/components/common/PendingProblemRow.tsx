
// Grid: Added click handling styles
export const PENDING_GRID = 
  "grid grid-cols-[80px_2fr_1fr_100px_100px_100px] gap-4 px-6 py-2 items-center border-b border-white/5 last:border-0 " +
  "[&>*:first-child]:justify-self-center " + 
  "[&>*:nth-child(5)]:justify-self-center " + 
  "[&>*:nth-child(6)]:justify-self-center";

interface PendingProblemRowProps {
  id: number;
  index: number;
  name: string;
  author: string | null;
  onApprove: () => void;
  onReject: () => void;
  onRowClick: () => void;
  onAuthorClick: () => void;
}

export default function PendingProblemRow({
  index,
  name,
  author,
  onApprove,
  onReject,
  onRowClick,
  onAuthorClick,
}: PendingProblemRowProps) {
  return (
    <div
      onClick={onRowClick}
      className={`${PENDING_GRID} hover:bg-sidebar/30 transition-all duration-200 group cursor-pointer`}
    >
      {/* Index (#) */}
      <span className="text-text/60 group-hover:text-text font-anta text-xs truncate transition-colors">
        {index}
      </span>

      {/* Name */}
      <span className="text-text font-anta text-sm truncate" title={name}>
        {name}
      </span>

      {/* Author - Clickable */}
      <span className="text-text/80 font-anta text-xs truncate">
        {author ? (
          <span 
            onClick={(e) => {
              e.stopPropagation(); // Prevent row click
              onAuthorClick();
            }}
            className="px-2 py-0.5 rounded bg-white/5 text-text/80 hover:bg-orange/20 hover:text-orange cursor-pointer transition-colors"
          >
            @{author}
          </span>
        ) : (
          <span className="opacity-30">-</span>
        )}
      </span>

      {/* Empty column (Spacer) */}
      <span></span>

      {/* Approve Button */}
      <button
        onClick={(e) => {
          e.stopPropagation(); // Prevent row click
          onApprove();
        }}
        className="
            flex items-center justify-center
            border border-emerald-500/30 bg-emerald-500/5 text-emerald-400
            hover:bg-emerald-500 hover:text-white hover:border-emerald-500 hover:shadow-[0_0_10px_rgba(16,185,129,0.3)]
            px-4 py-1 rounded-full 
            font-anta text-[10px] uppercase tracking-widest 
            transition-all duration-300
        "
      >
        Approve
      </button>

      {/* Reject Button */}
      <button
        onClick={(e) => {
          e.stopPropagation(); // Prevent row click
          onReject();
        }}
        className="
            flex items-center justify-center
            border border-rose-500/30 bg-rose-500/5 text-rose-400
            hover:bg-rose-600 hover:text-white hover:border-rose-600 hover:shadow-[0_0_10px_rgba(225,29,72,0.3)]
            px-4 py-1 rounded-full 
            font-anta text-[10px] uppercase tracking-widest 
            transition-all duration-300
        "
      >
        Reject
      </button>
    </div>
  );
}