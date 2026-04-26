import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Board from "../components/common/Board";
import NotificationRow, { type NotificationRowProps } from "../components/common/NotificationRow";
import NotificationDetail from "../components/common/NotificationDetail";
import { fetchNotifications } from "../services/NotificationService";

export default function Notifications() {
  const [notifications, setNotifications] = useState<NotificationRowProps[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<"all" | "match" | "friend">("all");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { notificationId } = useParams<{ notificationId: string }>();
  const navigate = useNavigate();

  const itemsPerPage = 10;

  const selectedNotification = notificationId 
    ? notifications.find(n => n.id === parseInt(notificationId))
    : null;

  async function loadNotifications(pageToLoad = 0, isInitialLoad = false) {
    try {
      if (isInitialLoad) {
        setLoading(true);
      }
      setError(null);
      
      const data = await fetchNotifications(selectedCategory, pageToLoad, itemsPerPage);

      setNotifications(data.content);
      setTotalPages(data.totalPages || 1);
      setTotalElements(data.totalElements);
      setPage(pageToLoad);
      
    } catch (err) {
      console.error("Failed to fetch notifications:", err);
      setError(`Failed to load notifications: ${err instanceof Error ? err.message : 'Unknown error'}`);
    } finally {
      setLoading(false);
    }
  }

  // Load notifications on mount
  useEffect(() => {
    loadNotifications(0, true);
  }, []);

  // Reload when category changes - reset to page 0
  useEffect(() => {
    loadNotifications(0, false);
  }, [selectedCategory]);

  // Reload when returning from detail view
  useEffect(() => {
    if (!notificationId) {
      loadNotifications(page, false);
    }
  }, [notificationId]);

  const handleNotificationClick = (notification: NotificationRowProps) => {
    navigate(`/notifications/${notification.id}`);
  };

  const handleReturn = () => {
    navigate("/notifications");
  };

  // If a notification is selected, show detail view
  if (selectedNotification) {
    return (
      <div className="flex flex-col h-[90vh] space-y-4 p-scroll-x">
        <div className="flex-1 overflow-hidden rounded-xl border border-white/5 bg-sidebar/10 shadow-xl">
          <div className="h-full overflow-y-auto custom-scroll">
            <NotificationDetail
              notification={selectedNotification}
              onReturn={handleReturn}
            />
          </div>
        </div>
      </div>
    );
  }

  const handlePrevPage = () => {
    if (page > 0) {
      loadNotifications(page - 1);
    }
  };

  const handleNextPage = () => {
    if (page < totalPages - 1) {
      loadNotifications(page + 1);
    }
  };

  return (
    <div className="flex flex-col h-[90vh] space-y-4 p-scroll-x">
      <div className="flex items-center justify-between flex-wrap gap-4">
        {/* Category Filter */}
        <div className="flex items-center gap-2">
          <button
            onClick={() => setSelectedCategory("all")}
            className={`
              px-4 py-2 rounded-full font-anta text-sm transition-all duration-300
              ${selectedCategory === "all"
                ? "bg-orange text-white border border-orange shadow-[0_0_10px_rgba(249,115,22,0.3)]"
                : "bg-sidebar/50 text-text/70 border border-white/10 hover:border-orange/50 hover:text-orange"
              }
            `}
          >
            All
          </button>
          <button
            onClick={() => setSelectedCategory("match")}
            className={`
              px-4 py-2 rounded-full font-anta text-sm transition-all duration-300
              ${selectedCategory === "match"
                ? "bg-orange text-white border border-orange shadow-[0_0_10px_rgba(249,115,22,0.3)]"
                : "bg-sidebar/50 text-text/70 border border-white/10 hover:border-orange/50 hover:text-orange"
              }
            `}
          >
            Match
          </button>
          <button
            onClick={() => setSelectedCategory("friend")}
            className={`
              px-4 py-2 rounded-full font-anta text-sm transition-all duration-300
              ${selectedCategory === "friend"
                ? "bg-orange text-white border border-orange shadow-[0_0_10px_rgba(249,115,22,0.3)]"
                : "bg-sidebar/50 text-text/70 border border-white/10 hover:border-orange/50 hover:text-orange"
              }
            `}
          >
            Friend
          </button>
        </div>

        {/* Results count */}
        {!loading && totalElements > 0 && (
          <div className="text-text/60 text-sm font-anta">
            {totalElements} notification{totalElements !== 1 ? 's' : ''} total
          </div>
        )}
      </div>

      {/* Table Area */}
      <div className="flex-1 overflow-hidden rounded-xl border border-white/5 bg-sidebar/10 shadow-xl">
        <div className="h-full overflow-y-auto custom-scroll">
          {loading ? (
            <div className="flex items-center justify-center h-full">
              <p className="text-text/60 font-anta text-lg">Loading notifications...</p>
            </div>
          ) : error ? (
            <div className="flex items-center justify-center h-full">
              <div className="text-center max-w-md">
                <p className="text-red-400 font-anta text-lg mb-2">⚠️ Error Loading Notifications</p>
                <p className="text-text/60 text-sm mb-4">{error}</p>
                <button
                  onClick={() => loadNotifications(page)}
                  className="px-4 py-2 bg-orange text-white rounded-lg hover:bg-orange/80 transition-colors font-anta"
                >
                  Retry
                </button>
              </div>
            </div>
          ) : totalElements === 0 ? (
            <div className="flex items-center justify-center h-full">
              <div className="text-center">
                <p className="text-text/60 font-anta text-lg">
                  {selectedCategory === "all" 
                    ? "No notifications yet" 
                    : `No ${selectedCategory} notifications`}
                </p>
                <p className="text-text/40 text-sm mt-2">
                  You'll see notifications here when you receive match invites or updates
                </p>
              </div>
            </div>
          ) : (
            <Board<NotificationRowProps>
              data={notifications}
              columns={["Type", "From", "Message", "Time", "Status"]}
              gridCols="grid-cols-[150px_150px_1fr_120px_120px]"
              onRowClick={handleNotificationClick}
              renderRow={(notification, onClick) => (
                <NotificationRow
                  key={notification.id}
                  {...notification}
                  onClick={onClick}
                  className="cursor-pointer"
                />
              )}
            />
          )}
        </div>
      </div>

      {/* Pagination */}
      {!loading && notifications.length > 0 && (
        <div className="flex justify-center gap-4">
          <button
            onClick={handlePrevPage}
            disabled={page === 0}
            className="px-5 py-2 bg-sidebar/50 border border-white/10 text-white rounded-full hover:bg-orange hover:border-orange disabled:opacity-30 disabled:hover:bg-sidebar/50 disabled:hover:border-white/10 disabled:cursor-not-allowed transition-all duration-300 font-anta text-sm"
          >
            Previous
          </button>

          <span className="flex items-center text-text/80 font-anta text-sm bg-sidebar/30 px-4 rounded-full border border-white/5">
            Page {page + 1} of {totalPages}
          </span>

          <button
            onClick={handleNextPage}
            disabled={page >= totalPages - 1}
            className="px-5 py-2 bg-sidebar/50 border border-white/10 text-white rounded-full hover:bg-orange hover:border-orange disabled:opacity-30 disabled:hover:bg-sidebar/50 disabled:hover:border-white/10 disabled:cursor-not-allowed transition-all duration-300 font-anta text-sm"
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}