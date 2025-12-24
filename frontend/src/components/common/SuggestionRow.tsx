import React from "react";

export const SUGGESTION_GRID_COLS = "grid-cols-[0.5fr_2fr_1fr_3fr]";

interface SuggestionRowProps {
  id: number;
  index: number;
  name: string;
  status: string;
  rejectionNote: string;
  onRowClick: () => void;
}

const SuggestionRow: React.FC<SuggestionRowProps> = ({
  index,
  name,
  status,
  rejectionNote,
  onRowClick,
}) => {
  const getStatusStyle = (status: string) => {
    switch (status) {
      case "APPROVED":
        return "text-green-400 bg-green-400/10 border-green-400/20";
      case "REJECTED":
        return "text-red-400 bg-red-400/10 border-red-400/20";
      case "PENDING_APPROVAL":
        return "text-yellow-400 bg-yellow-400/10 border-yellow-400/20";
      default:
        return "text-text/70 bg-white/5 border-white/10";
    }
  };

  const formatStatus = (status: string) => status.replace("_", " ");

  const cellStyle = "text-sm font-anta truncate flex items-center justify-center";

  return (
    <div
      onClick={onRowClick}
      className={`grid ${SUGGESTION_GRID_COLS} gap-4 px-6 py-4 border-b border-white/5 hover:bg-white/5 transition-colors cursor-pointer group`}
    >
      <div className={`${cellStyle}`}>
        <span className="text-text/60 group-hover:text-orange transition-colors">
          {index}
        </span>
      </div>

      <div className={`${cellStyle} justify-start'`}>
        <span className="text-white">{name}</span>
      </div>

      <div className={`${cellStyle}`}>
        <span
          className={`px-3 py-1 rounded-full text-xs border tracking-wide ${getStatusStyle(
            status
          )}`}
        >
          {formatStatus(status)}
        </span>
      </div>

      <div className={`${cellStyle}`}>
        <span className="text-white" title={rejectionNote}>
          {rejectionNote}
        </span>
      </div>
    </div>
  );
};

export default SuggestionRow;