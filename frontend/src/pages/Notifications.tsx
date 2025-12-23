import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Board from "../components/common/Board";
import NotificationRow, { type NotificationRowProps } from "../components/common/NotificationRow";
import NotificationDetail from "../components/common/NotificationDetail";
// import { fetchNotifications } from "../services/NotificationService";
import NotificationTypeFilter from "../components/common/NotificationTypeFilter";
import { NotificationType } from "../enums/NotificationType";

// mock data for testing 
const MOCK_NOTIFICATIONS: NotificationRowProps[] = [
  {
    id: 1,
    type: NotificationType.MATCH_INVITE,
    senderId: 101,
    senderUsername: "CodeMaster",
    recipientId: 1,
    title: "Match Invitation",
    message: "CodeMaster has invited you to a coding match. Accept to start competing!",
    createdAt: new Date(Date.now() - 5 * 60 * 1000).toISOString(), // 5 minutes ago
    read: false,
  },
  {
    id: 2,
    type: NotificationType.MATCH_STARTED,
    senderId: 102,
    senderUsername: "AlgoNinja",
    recipientId: 1,
    title: "Match Started",
    message: "Your match with AlgoNinja has started. Good luck!",
    createdAt: new Date(Date.now() - 15 * 60 * 1000).toISOString(), // 15 minutes ago
    read: false,
    matchId: 1234,
  },

  {
    id: 5,
    type: NotificationType.MATCH_ENDED,
    senderId: 103,
    senderUsername: "PyGenius",
    recipientId: 1,
    title: "Match Ended",
    message: "Your match with PyGenius has ended. Check the results!",
    createdAt: new Date(Date.now() - 5 * 60 * 60 * 1000).toISOString(), // 5 hours ago
    read: true,
    matchId: 1235,
  },
  {
    id: 6,
    type: NotificationType.OPPONENT_RESIGNED,
    senderId: 104,
    senderUsername: "JavaPro",
    recipientId: 1,
    title: "Opponent Resigned",
    message: "JavaPro has resigned from the match. You win!",
    createdAt: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(), // 1 day ago
    read: true,
    matchId: 1236,
  },
  {
    id: 7,
    type: NotificationType.FRIEND_REQUEST,
    senderId: 105,
    senderUsername: "DevGuru",
    recipientId: 1,
    title: "Friend Request",
    message: "DevGuru wants to be your friend. Accept to connect!",
    createdAt: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(), // 2 days ago
    read: false,
  },
  {
    id: 8,
    type: NotificationType.FRIEND_ACCEPTED,
    senderId: 106,
    senderUsername: "CppChampion",
    recipientId: 1,
    title: "Friend Request Accepted",
    message: "CppChampion accepted your friend request. You are now connected!",
    createdAt: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000).toISOString(), // 3 days ago
    read: true,
  },

];

export default function Notifications() {
  const [notifications, setNotifications] = useState<NotificationRowProps[]>(MOCK_NOTIFICATIONS);
  const [selectedType, setSelectedType] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1); // total pages from backend
  const { notificationId } = useParams<{ notificationId: string }>();
  const navigate = useNavigate();

  const selectedNotification = notificationId 
    ? notifications.find(n => n.id === parseInt(notificationId))
    : null;

  async function loadNotifications(pageToLoad = 0) {
    try {
      // Uncomment this when backend is ready
      // const data = await fetchNotifications();
      
      // For now, use mock data
      const data = MOCK_NOTIFICATIONS;

      // Filter by type if selected
      const filtered = selectedType
      setNotifications(data);
      setPage(pageToLoad);
      
    } catch (err) {
      console.error("Failed to fetch notifications", err);
    }
  }

  useEffect(() => {
    loadNotifications();
  }, [selectedType]);

  const handleNotificationClick = (notification: NotificationRowProps) => {
    console.log("Notification clicked:", notification);
    console.log("Navigating to:", `/notifications/${notification.id}`);
    navigate(`/notifications/${notification.id}`);
    // TODO: Mark as read when clicked
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
      <div className="flex items-center justify-between flex-wrap space-y-4">
        <NotificationTypeFilter
          value={selectedType}
          onChange={setSelectedType}
        />
      </div>

      {/* Table Area */}
      <div className="flex-1 overflow-hidden rounded-xl border border-white/5 bg-sidebar/10 shadow-xl">
        <div className="h-full overflow-y-auto custom-scroll">
          {notifications.length === 0 ? (
            <div className="flex items-center justify-center h-full">
              <p className="text-text/60 font-anta text-lg">
                No notifications to display
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
    </div>
  );
}