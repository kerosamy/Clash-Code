import { useState, useEffect } from "react";
import Board from "../../components/common/Board";
import UserRow from "../../components/common/UserRow";
import SearchBar from "../../components/common/SearchBar";
import { searchUsersWithFriendStatus } from "../../services/UserService";
import type { UserSearchResponse } from "../../services/UserService";
import { useNavigate } from "react-router-dom";

interface UserWithStatus extends UserSearchResponse {
  friendStatus?: string;
}

export default function Users() {
  const [searchTerm, setSearchTerm] = useState("");
  const [users, setUsers] = useState<UserWithStatus[]>([]);
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
        const results = await searchUsersWithFriendStatus(searchTerm);
        console.log("Search results:", results);
        console.log("Search term:", searchTerm);
        
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

  const handleUsernameClick = (user: UserWithStatus) => {
    navigate(`/profile/${user.username}/overview`);
  };

  if (loading) {
    return (
      <div className="container mx-auto p-6 space-y-6">
        <h1 className="text-3xl font-bold text-orange font-anta">Search Users</h1>
        <SearchBar
          value={searchTerm}
          onChange={setSearchTerm}
          placeholder="Search users..."
        />
        <div className="bg-container rounded-lg p-8">
          <div className="text-center py-8 text-text text-lg">Loading...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-6 space-y-6">
      <h1 className="text-3xl font-bold text-orange font-anta">Search Users</h1>

      <SearchBar
        value={searchTerm}
        onChange={setSearchTerm}
        placeholder="Search users..."
      />

      {searchTerm.trim() && (
        <Board
          data={users}
          columns={["#", "Name", "Status"]}
          gridCols="grid-cols-[60px_1fr_auto]"
          renderRow={(user) => (
            <UserRow
              key={user.username}
              order={users.indexOf(user) + 1}
              username={user.username}
              rank={user.currentRate}
              friendStatus={user.friendStatus}
              onUsernameClick={() => handleUsernameClick(user)}
            />
          )}
        />
      )}
    </div>
  );
}