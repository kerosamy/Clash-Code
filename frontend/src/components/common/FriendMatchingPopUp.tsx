import { useState, useEffect } from 'react';
import { X, Search } from 'lucide-react';
import { searchUsers, type UserSearchResponse } from '../../services/UserService';
import UserInvite from "./UserInvite";

interface FriendMatchingPopUpProps {
  isOpen: boolean;
  onClose: () => void;
  onInvite: (notificationId: number, username: string) => void; // Updated signature
}

export default function FriendMatchingPopUp({ isOpen, onClose, onInvite }: FriendMatchingPopUpProps) {
  const [users, setUsers] = useState<UserSearchResponse[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (!isOpen) {
      setSearchQuery('');
      setUsers([]);
    }
  }, [isOpen]);
  
  useEffect(() => {
    if (!searchQuery.trim()) {
      setUsers([]);
      return;
    }

    const timer = setTimeout(async () => {
      setIsLoading(true);
      try {
        const results = await searchUsers(searchQuery);
        setUsers(results);
      } catch (error) {
        setUsers([]);
      } finally {
        setIsLoading(false);
      }
    }, 300);

    return () => clearTimeout(timer);
  }, [searchQuery]);

  const handleInvite = (notificationId: number, username: string) => {
    // Pass both notification ID and username to parent
    onInvite(notificationId, username);
    // Close the popup after sending invite
    onClose();
  };

  const handleClose = () => {
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-50">
      <div className="bg-sidebar rounded-lg w-full max-w-2xl max-h-[80vh] flex flex-col shadow-2xl border border-gray-700">

        <div className="flex items-center justify-between p-6 border-b border-gray-700">
          <h2 className="text-2xl font-bold text-orange font-anta uppercase tracking-wider">
            Friend Matching
          </h2>
          <button
            onClick={handleClose}
            className="text-gray-400 hover:text-white transition-colors"
          >
            <X className="w-6 h-6" />
          </button>
        </div>

        <div className="p-4 border-b border-gray-700">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search by username..."
              className="w-full pl-10 pr-4 py-2 bg-background border border-gray-600 rounded-lg text-text placeholder-gray-400 focus:outline-none focus:border-orange focus:ring-1 focus:ring-orange transition-colors font-anta"
            />
          </div>
        </div>

        <div className="flex-1 overflow-y-auto custom-scroll">
          {isLoading ? (
            <div className="flex items-center justify-center h-full">
              <div className="text-text font-anta">Searching...</div>
            </div>
          ) : users.length === 0 ? (
            <div className="flex items-center justify-center h-full">
              <div className="text-text font-anta text-center px-4">
                {searchQuery ? 'No users found' : 'Enter a username to search'}
              </div>
            </div>
          ) : (
            <>
              {users.map((user, index) => (
                <UserInvite
                  key={`${user.username}-${index}`}
                  order={index + 1}
                  username={user.username}
                  rank={user.rank}
                  onInviteClick={handleInvite}
                  onUsernameClick={() => {}}
                />
              ))}
            </>
          )}
        </div>
      </div>
    </div>
  );
}