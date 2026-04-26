import React from "react";
import { Plus, X, Check } from "lucide-react";
import { getRankColor } from "../../utils/colorMapper";
import { UserStatus } from "../../enums/UserStatus";
import { getStatusColor } from "../../utils/getUserStatusColor";
import { getStatusLabel } from "../../utils/getUserStatusLabel";

interface UserRowProps {
  order: number;
  username: string;
  userStatus?: UserStatus;
  rank?: number;
  action?: React.ReactNode;
  onAddClick?: () => void;
  onCancelClick?: () => void;
  onAcceptClick?: () => void;
  onRejectClick?: () => void;
  onUsernameClick?: () => void;
  showAddButton?: boolean;
  showCancelButton?: boolean;
  showAcceptReject?: boolean;
  friendStatus?: string;
}

export default function UserRow({
  order,
  username,
  userStatus, 
  rank = 0,
  action,
  onAddClick,
  onCancelClick,
  onAcceptClick,
  onRejectClick,
  onUsernameClick,
  showAddButton = false,
  showCancelButton = false,
  showAcceptReject = false,
  friendStatus,
}: UserRowProps) {
  const usernameColor = getRankColor(rank);

  const getStatusDisplay = () => {
    if (!friendStatus) return null;
    
    const statusConfig: Record<string, { text: string; color: string; borderColor: string }> = {
      "FRIENDS": { text: "Friends", color: "#34D399", borderColor: "#34D399" },
      "PENDING_SENT": { text: "Pending", color: "#FFD700", borderColor: "#FFD700" },
      "PENDING_RECEIVED": { text: "Requested", color: "#60A5FA", borderColor: "#60A5FA" },
      "NONE": { text: "Not Friend", color: "#FF6B35", borderColor: "#FF6B35" },
    };

    const config = statusConfig[friendStatus] || statusConfig["NONE"];
    
    return (
      <span
        className="px-3 py-1.5 rounded-full text-sm font-anta uppercase tracking-wider"
        style={{ 
          color: config.color,
          border: `2px solid ${config.borderColor}`,
          backgroundColor: `${config.borderColor}15`
        }}
      >
        {config.text}
      </span>
    );
  };

  return (
    <div className="grid grid-cols-[60px_1fr_120px_auto] gap-4 px-8 py-4 hover:bg-sidebar/30 transition-colors items-center border-b border-sidebar/50">
      {/* Order */}
      <div className="text-text font-anta text-lg flex items-center justify-center">
        {order}
      </div>

      {/* Username */}
      <div
        className="font-anta text-xl cursor-pointer hover:opacity-80 transition-opacity flex items-center justify-center"
        style={{ color: usernameColor }}
        onClick={onUsernameClick}
      >
        {username}
      </div>

      {/* Status Column */}
      <div className="flex items-center justify-center gap-2">
        {userStatus && (
          <>
            {/* Status Dot */}
            <div className="relative flex items-center">
              <div className={`w-2.5 h-2.5 rounded-full ${getStatusColor(userStatus).replace('text-', 'bg-')}`}>
                {userStatus === UserStatus.ONLINE && (
                  <div className="absolute inset-0 rounded-full bg-green-500 animate-ping opacity-75"></div>
                )}
              </div>
            </div>
            
            {/* Status Label */}
            {userStatus && (
              <span className={`text-sm font-anta ${getStatusColor(userStatus)}`}>
              {getStatusLabel(userStatus)}
            </span>)}
            
          </>
        )}
      </div>

      {/* Action */}
      <div className="flex gap-2 items-center justify-center">
        {friendStatus ? getStatusDisplay() : action}
        
        {showAddButton && onAddClick && (
          <button
            onClick={onAddClick}
            className="w-7 h-7 rounded-full bg-sidebar border-2 border-orange hover:bg-orange transition-all flex items-center justify-center group"
            aria-label="Add friend"
          >
            <Plus className="w-4 h-4 text-orange group-hover:text-white transition-colors" />
          </button>
        )}

        {showCancelButton && onCancelClick && (
          <button
            onClick={onCancelClick}
            className="w-7 h-7 rounded-full bg-sidebar border-2 border-rose-500 hover:bg-rose-500 transition-all flex items-center justify-center group"
            aria-label="Cancel request"
          >
            <X className="w-4 h-4 text-rose-500 group-hover:text-white transition-colors" />
          </button>
        )}

        {showAcceptReject && onAcceptClick && onRejectClick && (
          <>
            <button
              onClick={onAcceptClick}
              className="w-7 h-7 rounded-full bg-sidebar border-2 border-emerald-500 hover:bg-emerald-500 transition-all flex items-center justify-center group"
              aria-label="Accept friend request"
            >
              <Check className="w-4 h-4 text-emerald-500 group-hover:text-white transition-colors" />
            </button>
            <button
              onClick={onRejectClick}
              className="w-7 h-7 rounded-full bg-sidebar border-2 border-rose-500 hover:bg-rose-500 transition-all flex items-center justify-center group"
              aria-label="Reject friend request"
            >
              <X className="w-4 h-4 text-rose-500 group-hover:text-white transition-colors" />
            </button>
          </>
        )}
      </div>
    </div>
  );
}