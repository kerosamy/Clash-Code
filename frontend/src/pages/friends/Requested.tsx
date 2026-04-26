import { useState, useEffect } from "react";
import Board from "../../components/common/Board";
import UserRow from "../../components/common/UserRow";
import LogoLoader from "../../components/Loader/LogoLoader";
import { waitForLoader } from "../../components/Loader/WaitLoader";
import { 
  getSentFriendRequests, 
  removeFriend 
} from "../../services/FriendService";
import type { FriendDto } from "../../services/FriendService";
import { useNavigate } from "react-router-dom";

export default function Requested() {
  const [requested, setRequested] = useState<FriendDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const navigate = useNavigate();

  const pageSize = 20;

  useEffect(() => {
    const fetchRequested = async () => {
      const startTime = Date.now();
      setLoading(true);
      setError(null);

      try {
        const response = await getSentFriendRequests(currentPage, pageSize);
        await waitForLoader(startTime);
        setRequested(response.content);
        setTotalPages(response.totalPages);
      } catch (err) {
        console.error("Error fetching requested friends:", err);
        setError("Failed to load sent friend requests.");
      } finally {
        setLoading(false);
      }
    };

    fetchRequested();
  }, [currentPage]);

  const handleCancelRequest = async (username: string) => {
    try {
      await removeFriend(username);
      setRequested(prev => prev.filter(u => u.username !== username));
    } catch (error) {
      console.error("Failed to cancel friend request:", error);
    }
  };

  if (loading) {
    return <LogoLoader loadingMessage="Loading Sent Requests" />;
  }

  if (error) {
    return (
      <div className="text-red-500 text-center p-6">
        {error}
      </div>
    );
  }

  return (
    <div className="container mx-auto p-6 space-y-6">
      <h1 className="text-3xl font-bold text-orange font-anta">Requested Friends</h1>

      <Board
        data={requested}
        columns={["#", "Name", "Cancel"]}
        gridCols="grid-cols-[60px_1fr_auto]"
        renderRow={(user) => (
          <UserRow
            key={user.username}
            order={requested.indexOf(user) + 1 + currentPage * pageSize}
            username={user.username}
            rank={user.currentRate}
            showCancelButton={true}
            onCancelClick={() => handleCancelRequest(user.username)}
            onUsernameClick={() => navigate(`/profile/${user.username}/overview`)}
            userStatus={user.userStatus}
          />
        )}
      />

      {totalPages > 1 && (
        <div className="flex justify-center items-center gap-2">
          <button
            onClick={() => setCurrentPage(currentPage - 1)}
            disabled={currentPage === 0}
            className="px-4 py-2 bg-container text-text rounded-lg hover:bg-sidebar disabled:opacity-50 disabled:cursor-not-allowed font-anta"
          >
            Previous
          </button>
          
          <span className="text-text font-anta">
            Page {currentPage + 1} of {totalPages}
          </span>
          
          <button
            onClick={() => setCurrentPage(currentPage + 1)}
            disabled={currentPage >= totalPages - 1}
            className="px-4 py-2 bg-container text-text rounded-lg hover:bg-sidebar disabled:opacity-50 disabled:cursor-not-allowed font-anta"
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}