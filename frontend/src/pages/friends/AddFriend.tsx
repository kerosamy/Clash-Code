import { useState, useEffect } from "react";
import Board from "../../components/common/Board";
import UserRow from "../../components/common/UserRow";
import SearchBar from "../../components/common/SearchBar";
import { searchUsers, addFriend } from "../../services/UserService";
import type { UserSearchResponse } from "../../services/UserService";
import { useNavigate } from "react-router-dom";

export default function AddFriend() {
  const [searchTerm, setSearchTerm] = useState("");
  const [users, setUsers] = useState<UserSearchResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (!searchTerm.trim()) {
      setUsers([]);
      return;
    }

    const timer = setTimeout(async () => {
      setLoading(true);
      try {
        const results = await searchUsers(searchTerm);
        setUsers(results);
      } catch (error) {
        console.error("Error fetching users:", error);
        setUsers([]);
      } finally {
        setLoading(false);
      }
    }, 300);

    return () => clearTimeout(timer);
  }, [searchTerm]);

  const handleAddFriend = async (user: UserSearchResponse) => {
    try {
      await addFriend(user.username);
      console.log("Friend added:", user.username);
    } catch (error) {
      console.error("Failed to add friend:", error);
    }
  };

  const handleUsernameClick = (user: UserSearchResponse) => {
    console.log("Username clicked:", user.username);
    // Navigate to profile by username
    navigate(`/profile/${user.username}/overview`);
  };

  const renderUserRow = (user: UserSearchResponse) => {
    const order = users.indexOf(user) + 1;

    return (
      <UserRow
        key={user.username}
        order={order}
        username={user.username}
        rank={user.rank}
        onAddClick={() => handleAddFriend(user)}
        onUsernameClick={() => handleUsernameClick(user)}
      />
    );
  };

  return (
    <div className="container mx-auto p-6 space-y-6">
      <SearchBar
        value={searchTerm}
        onChange={setSearchTerm}
        placeholder="Search users..."
      />

      {searchTerm.trim() && (
        <Board
          data={users}
          columns={["#", "Name", "Add"]}
          renderRow={(user) => renderUserRow(user)}
          gridCols="grid-cols-[60px_4fr_100px]"
        />
      )}
    </div>
  );
}
