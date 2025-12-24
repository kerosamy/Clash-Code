import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Board from "../components/common/Board";
import NotificationRow, { type NotificationRowProps } from "../components/common/NotificationRow";
import NotificationDetail from "../components/common/NotificationDetail";
import { fetchNotifications } from "../services/NotificationService";
import { NotificationType } from "../enums/NotificationType";

export default function Notifications() {
  const [notifications, setNotifications] = useState<NotificationRowProps[]>([]);
  const [allNotifications, setAllNotifications] = useState<NotificationRowProps[]>([]);
  const [selectedType, setSelectedType] = useState<string | null>(null);
  const [selectedCategory, setSelectedCategory] = useState<"all" | "match" | "friend">("all");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { notificationId } = useParams<{ notificationId: string }>();
  const navigate = useNavigate();

  const selectedNotification = notificationId 
    ? allNotifications.find(n => n.id === parseInt(notificationId))
    : null;

  async function loadNotifications(pageToLoad = 0, isInitialLoad = false) {
    try {
      // Only show loading on initial page load
      if (isInitialLoad && allNotifications.length === 0) {
        setLoading(true);
      }
      setError(null);
      
      console.log("Fetching notifications from backend...");
      const data = await fetchNotifications(selectedCategory);
      console.log("Received notifications:", data);
      console.log("Number of notifications:", data.length);

      setAllNotifications(data);

      // Filter by type if selected
      const filtered = selectedType
        ? data.filter(n => n.type === selectedType)
        : data;

      console.log("Filtered notifications:", filtered.length);

      // Implement client-side pagination
      const itemsPerPage = 10;
      const startIndex = pageToLoad * itemsPerPage;
      const endIndex = startIndex + itemsPerPage;
      const paginatedData = filtered.slice(startIndex, endIndex);
      
      console.log("Paginated data:", paginatedData.length);
      
      setNotifications(paginatedData);
      setTotalPages(Math.ceil(filtered.length / itemsPerPage) || 1);
      setPage(pageToLoad);
      
    } catch (err) {
      console.error("Failed to fetch notifications:", err);
      console.error("Error details:", err instanceof Error ? err.message : err);
      setError(`Failed to load notifications: ${err instanceof Error ? err.message : 'Unknown error'}`);
    } finally {
      setLoading(false);
    }
  }

  // Load notifications on mount - only show loading on first load
  useEffect(() => {
    loadNotifications(0, true);
  }, []);

  // Reload notifications when category filter changes
  useEffect(() => {
    loadNotifications(0, false);
  }, [selectedCategory]);

  // Reload notifications when type filter changes - no loading screen
  useEffect(() => {
    if (allNotifications.length > 0) {
      loadNotifications(0, false);
    }
  }, [selectedType]);

  // Reload notifications when returning to the list view - no loading screen
  useEffect(() => {
    if (!notificationId && allNotifications.length > 0) {
      loadNotifications(page, false);
    }
  }, [notificationId]);

  const handleNotificationClick = (notification: NotificationRowProps) => {
    console.log("Notification clicked:", notification);
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
    if (page > 0) loadNotifications(page - 1);
  };

  const handleNextPage = () => {
    if (page < totalPages - 1) loadNotifications(page + 1);
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
          ) : notifications.length === 0 && allNotifications.length === 0 ? (
            <div className="flex items-center justify-center h-full">
              <div className="text-center">
                <p className="text-text/60 font-anta text-lg">
                  No notifications yet
                </p>
                <p className="text-text/40 text-sm mt-2">
                  You'll see notifications here when you receive match invites or updates
                </p>
              </div>
            </div>
          ) : notifications.length === 0 ? (
            <div className="flex items-center justify-center h-full">
              <p className="text-text/60 font-anta text-lg">
                No notifications match the selected filter
              </p>
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