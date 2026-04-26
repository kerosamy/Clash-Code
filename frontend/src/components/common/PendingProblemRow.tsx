import AppButton from "./AppButton";

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

      <AppButton
        label="Approve"
        onClick={(e) => {
          e.stopPropagation(); // Prevent row click
          onApprove();
        }}
        variant="positive"
        size="small"
      />

      <AppButton
        label="Reject"
        onClick={(e) => {
          e.stopPropagation(); // Prevent row click
          onReject();
        }}
        variant="negative"
        size="small"
      />
    </div>
  );
}