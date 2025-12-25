import { useState, useEffect } from "react";
import Board from "../../components/common/Board";
import UserRow from "../../components/common/UserRow";
import ConfirmationModal from "../../components/common/ConfirmationModal";
import LogoLoader from "../../components/Loader/LogoLoader";
import { waitForLoader } from "../../components/Loader/WaitLoader";
import { getFriendsList, removeFriend } from "../../services/FriendService";
import type { FriendDto } from "../../services/FriendService";
import { useNavigate } from "react-router-dom";

export default function MyFriends() {
  const [friends, setFriends] = useState<FriendDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showConfirm, setShowConfirm] = useState(false);
  const [selectedFriend, setSelectedFriend] = useState<FriendDto | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const navigate = useNavigate();

  const pageSize = 20;

  useEffect(() => {
    const fetchFriends = async () => {
      const startTime = Date.now();
      setLoading(true);
      setError(null);

      try {
        const response = await getFriendsList(currentPage, pageSize);
        console.log("Fetched friends:", response);
        await waitForLoader(startTime);
        setFriends(response.content);
        setTotalPages(response.totalPages);
      } catch (err) {
        console.error("Error fetching friends:", err);
        setError("Failed to load friends list.");
      } finally {
        setLoading(false);
      }
    };

    fetchFriends();
  }, [currentPage]);

  const handleUnFriend = async () => {
    if (!selectedFriend) return;
    try {
      await removeFriend(selectedFriend.username);
      setFriends((prev) =>
        prev.filter((f) => f.username !== selectedFriend.username)
      );
    } catch (error) {
      console.error("Failed to unfriend:", error);
    } finally {
      setShowConfirm(false);
      setSelectedFriend(null);
    }
  };

  if (loading) {
    return <LogoLoader loadingMessage="Loading Friends" />;
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
      <h1 className="text-3xl font-bold text-orange font-anta">My Friends</h1>

      <Board
        data={friends}
        columns={["#", "Name", "Action"]}
        gridCols="grid-cols-[60px_1fr_120px_auto]"
        renderRow={(user) => (
          <UserRow
            key={user.username}
            order={friends.indexOf(user) + 1 + currentPage * pageSize}
            username={user.username}
            rank={user.currentRate}
            action={
                <button onClick={() => {
                    setSelectedFriend(user);
                    setShowConfirm(true);
                }} 
                className={ `
                flex items-center justify-center
                px-3 py-1 rounded-full border-2 
                font-anta uppercase tracking-widest 
                transition-all duration-300 hover:text-white
                border-rose-500/30 bg-rose-500/5 text-rose-400
                hover:bg-rose-600 hover:border-rose-600 
                hover:shadow-[0_0_10px_rgba(225,29,72,0.3)]
                `}>
                    Unfriend
                </button>
            }
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

      <ConfirmationModal
        isOpen={showConfirm}
        onClose={() => setShowConfirm(false)}
        onConfirm={handleUnFriend}
        title="Confirm Unfriend"
        message={`Are you sure you want to unfriend ${selectedFriend?.username}?`}
        confirmText="Unfriend"
        cancelText="Cancel"
      />
    </div>
  );
}