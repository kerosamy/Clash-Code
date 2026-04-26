import { useState, useEffect, useRef, useCallback } from 'react';
import { X, Search } from 'lucide-react';
import { searchFriends, getFriendsList, type FriendDto } from '../../services/FriendService';
import { getRankName } from '../../utils/colorMapper';
import UserInvite from "./UserInvite";

interface FriendMatchingPopUpProps {
  isOpen: boolean;
  onClose: () => void;
  onInvite: (notificationId: number, username: string) => void;
}

export default function FriendMatchingPopUp({ isOpen, onClose, onInvite }: FriendMatchingPopUpProps) {
  const [allFriends, setAllFriends] = useState<FriendDto[]>([]);
  const [searchResults, setSearchResults] = useState<FriendDto[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [isSearching, setIsSearching] = useState(false);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [currentPage, setCurrentPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const observerTarget = useRef<HTMLDivElement>(null);

  // Reset state when popup closes
  useEffect(() => {
    if (!isOpen) {
      setSearchQuery('');
      setAllFriends([]);
      setSearchResults([]);
      setCurrentPage(0);
      setHasMore(true);
    }
  }, [isOpen]);

  // Fetch initial friends when popup opens
  useEffect(() => {
    if (!isOpen) return;

    const fetchInitialFriends = async () => {
      setIsLoadingMore(true);
      try {
        const result = await getFriendsList(0, 20);
        setAllFriends(result.content);
        setCurrentPage(0);
        setHasMore(!result.last);
      } catch (error) {
        console.error('Failed to fetch friends:', error);
        setAllFriends([]);
      } finally {
        setIsLoadingMore(false);
      }
    };

    fetchInitialFriends();
  }, [isOpen]);

  // Load more friends
  const loadMoreFriends = useCallback(async () => {
    if (isLoadingMore || !hasMore) return;

    setIsLoadingMore(true);
    try {
      const nextPage = currentPage + 1;
      const result = await getFriendsList(nextPage, 20);
      setAllFriends(prev => [...prev, ...result.content]);
      setCurrentPage(nextPage);
      setHasMore(!result.last);
    } catch (error) {
      console.error('Failed to load more friends:', error);
    } finally {
      setIsLoadingMore(false);
    }
  }, [currentPage, hasMore, isLoadingMore]);

  // Intersection Observer for infinite scroll
  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && hasMore && !isLoadingMore && !searchQuery.trim()) {
          loadMoreFriends();
        }
      },
      { threshold: 0.1 }
    );

    const currentTarget = observerTarget.current;
    if (currentTarget) {
      observer.observe(currentTarget);
    }

    return () => {
      if (currentTarget) {
        observer.unobserve(currentTarget);
      }
    };
  }, [hasMore, isLoadingMore, loadMoreFriends, searchQuery]);
  
  // Search friends based on query
  useEffect(() => {
    if (!isOpen) return;

    // If no search query, clear search results
    if (!searchQuery.trim()) {
      setSearchResults([]);
      return;
    }

    const timer = setTimeout(async () => {
      setIsSearching(true);
      try {
        const result = await searchFriends(searchQuery, 0, 100);
        setSearchResults(result.content);
      } catch (error) {
        console.error('Failed to search friends:', error);
        setSearchResults([]);
      } finally {
        setIsSearching(false);
      }
    }, 300);

    return () => clearTimeout(timer);
  }, [searchQuery, isOpen]);

  const handleInvite = (notificationId: number, username: string) => {
    onInvite(notificationId, username);
    onClose();
  };

  const handleClose = () => {
    onClose();
  };

  if (!isOpen) return null;

  // Determine what to display
  const isSearchActive = searchQuery.trim().length > 0;
  const displayFriends = isSearchActive ? searchResults : allFriends;
  const isLoading = isSearchActive ? isSearching : isLoadingMore && allFriends.length === 0;

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

        {/* Search Input */}
        <div className="p-4 border-b border-gray-700">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search friends by username..."
              className="w-full pl-10 pr-4 py-2 bg-background border border-gray-600 rounded-lg text-text placeholder-gray-400 focus:outline-none focus:border-orange focus:ring-1 focus:ring-orange transition-colors font-anta"
            />
          </div>
        </div>

        {/* Friends List with Infinite Scroll */}
        <div className="flex-1 overflow-y-auto custom-scroll">
          {isLoading ? (
            <div className="flex items-center justify-center h-full">
              <div className="text-text font-anta">
                {isSearchActive ? 'Searching...' : 'Loading friends...'}
              </div>
            </div>
          ) : displayFriends.length === 0 ? (
            <div className="flex items-center justify-center h-full">
              <div className="text-text font-anta text-center px-4">
                {isSearchActive 
                  ? `No friends found matching "${searchQuery}"`
                  : 'No friends yet. Add some friends first!'}
              </div>
            </div>
          ) : (
            <>
              {displayFriends.map((friend, index) => (
                <UserInvite
                  key={`${friend.username}-${index}`}
                  userStatus={friend.userStatus}
                  order={index + 1}
                  username={friend.username}
                  rank={getRankName(friend.currentRate)}
                  onInviteClick={handleInvite}
                  onUsernameClick={() => {}}
                />
              ))}
              
              {/* Infinite Scroll Trigger & Loading Indicator */}
              {!isSearchActive && (
                <div ref={observerTarget} className="py-4 text-center">
                  {isLoadingMore && (
                    <div className="text-text font-anta text-sm">
                      Loading more...
                    </div>
                  )}
                  {!hasMore && allFriends.length > 0 && (
                    <div className="text-gray-400 font-anta text-sm">
                      No more friends to load
                    </div>
                  )}
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
}