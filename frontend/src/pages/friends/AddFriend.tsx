import { useState, useEffect } from 'react';
import Board from '../../components/common/Board';
import UserRow from '../../components/common/UserRow';
import SearchBar from "../../components/common/SearchBar";

interface UserSearchResponse {
  username: string;
  rank: string;
}

export default function AddFriend() {
  const [searchTerm, setSearchTerm] = useState('');
  const [users, setUsers] = useState<UserSearchResponse[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!searchTerm.trim()) {
      setUsers([]);
      return;
    }

    const timer = setTimeout(() => {
        setLoading(true);

       const fetchUsers = async () => {
      setLoading(true);
      try {
        const response = await fetch(
          `http://localhost:8080/users/search?username=${encodeURIComponent(searchTerm)}`
        );
        if (!response.ok) throw new Error('Failed to fetch users');

        const data: UserSearchResponse[] = await response.json();
        setUsers(data);
      } catch (error) {
        console.error('Error fetching users:', error);
        setUsers([]);
      } finally {
        setLoading(false);
      }
    };

    fetchUsers(); 
  }, 300);

  return () => clearTimeout(timer);
}, [searchTerm]);

  const handleAddFriend = (user: UserSearchResponse) => {
    console.log('Add friend:', user.username);
  };

  const handleUsernameClick = (user: UserSearchResponse) => {
    console.log('Username clicked:', user.username);
    // TODO: handle username click (e.g., open profile)
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
          columns={['#', 'Name', 'Add']}
          renderRow={(user) => renderUserRow(user)}
          gridCols="grid-cols-[60px_4fr_100px]"
        />
      )}
    </div>
  );
}
