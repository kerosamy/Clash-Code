import type { UserStatus } from "../enums/UserStatus";
import { useEffect } from "react";
import { apiRequest } from "./api";
import { hasActiveMatch } from "../utils/matchState";

export interface BackendUserResponse {
  username: string;
  rank: string;
  currentRate: number;
  maxRate: number;
  friendCount: number;
  avatarUrl: string;
  userStatus: UserStatus;
  stats: {
    solvedProblems: number;
    attemptedProblems: number;
    matchesPlayed: number;
    matchesWon: number;
  };
  categories: {
    name: string;
    value: number;
    color: string;
  }[];
}

export interface UserProfileBasic {
  username: string;
  rank: string;
  currentRate: number;
  maxRate: number;
  friendCount: number;
  avatarUrl: string;
  userStatus: UserStatus;
}

export interface UserStats {
  solvedProblems: number;
  attemptedProblems: number;
  matchesPlayed: number;
  matchesWon: number;
}

export interface CategoryItem {
  name: string;
  value: number;
  color: string;
}

interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface UserManagementDto {
  id: number;
  username: string;
  email: string;
  role: string;
  rank: string;
}

export const splitUserData = (response: BackendUserResponse) => {
  const profileBasic: UserProfileBasic = {
    username: response.username,
    rank: response.rank,
    currentRate: response.currentRate,
    maxRate: response.maxRate,
    friendCount: response.friendCount,
    avatarUrl: response.avatarUrl,
    userStatus: response.userStatus,
  };

  const stats: UserStats = response.stats;
  const categories: CategoryItem[] = response.categories;

  return { profileBasic, stats, categories };
};

export async function fetchMyProfile(): Promise<BackendUserResponse> {
  return apiRequest<BackendUserResponse>({
    method: "GET",
    url: "/users/profile",
  });
}

export async function fetchUserProfile(username: string): Promise<BackendUserResponse> {
  return apiRequest<BackendUserResponse>({
    method: "GET",
    url: `/users/profile/${username}`
  });
}

export interface UserSearchResponse {
  rank: string;
  username: string;
  currentRate: number;
  friendStatus?: string;
}

export async function searchUsers(username: string): Promise<UserSearchResponse[]> {
  return apiRequest<UserSearchResponse[]>({
    method: "GET",
    url: "/users/search",
    params: { username },
  });
}

export async function searchUsersWithFriendStatus(username: string): Promise<UserSearchResponse[]> {
  return apiRequest<UserSearchResponse[]>({
    method: "GET",
    url: "/users/search-with-friend-status",
    params: { username },
  });
}

export async function addFriend(username: string): Promise<void> {
  return apiRequest<void>({
    method: "POST",
    url: "/users/add-friend",
    data: { username },
  });
}

// get all users here with pagination
export async function getAllUsers(
  page = 0,
  size = 20
): Promise<Page<UserManagementDto>> {
  return apiRequest<Page<UserManagementDto>>({
    method: "GET",
    url: "/super-admin/users",
    params: { page, size },
  });
}

export async function searchUsersByUsername(
  keyword: string,
  page = 0,
  size = 20
): Promise<Page<UserManagementDto>> {
  return apiRequest<Page<UserManagementDto>>({
    method: "GET",
    url: "/super-admin/users/search",
    params: { keyword, page, size },
  });
}

export async function promoteUserToAdmin(userId: number): Promise<void> {
  return apiRequest<void>({
    method: "PUT",
    url: `/super-admin/users/${userId}/promote-to-admin`,
  });
}

export async function demoteUserToUser(userId: number): Promise<void> {
  return apiRequest<void>({
    method: "PUT",
    url: `/super-admin/users/${userId}/demote-to-user`,
  });
}

export async function getFilteredUsersByRole(
  role: string,
  page = 0,
  size = 20
): Promise<Page<UserManagementDto>> {
  return apiRequest<Page<UserManagementDto>>({
    method: "GET",
    url: "/super-admin/users/filter",
    params: { role, page, size },
  });
}
interface UploadImageResponse {
    imageUrl: string;
}

export async function uploadProfileImage(file: File): Promise<UploadImageResponse> {
    const formData = new FormData();
    formData.append('file', file);

    return apiRequest<UploadImageResponse>({
        method: 'POST',
        url: '/users/profile/image',
        data: formData,
    });
}


export interface LeaderboardUserDto {
  username: string;
  currentRate: number;
}

export async function getLeaderboard(
  page = 0,
  size = 20
): Promise<Page<LeaderboardUserDto>> {
  return apiRequest<Page<LeaderboardUserDto>>({
    method: "GET",
    url: "/users/leaderboard", // Assuming this is your endpoint
    params: { page, size },
  });
}


export function useOnlineStatus() {
  useEffect(() => {
    const updateStatus = () => {
      const statusUrl = hasActiveMatch() 
        ? '/users/status/in-match' 
        : '/users/status/online';
      
      apiRequest<void>({
        method: 'POST',
        url: statusUrl,
      }).catch(console.error);
    };
    updateStatus();
    const interval = setInterval(() => {
      updateStatus();
    }, 30000); 

    return () => clearInterval(interval);
  }, []);
}

export async function updateStatusToOnline(): Promise<void> {
    try {
        await apiRequest<void>({
            method: 'POST',
            url: '/users/status/online',
        });
    } catch (error) {
        console.error('Failed to update status to online:', error);
    }
};

export async function updateStatusToInMatch(): Promise<void> {
    try {
        await apiRequest<void>({
            method: 'POST',
            url: '/users/status/in-match',
        });
    } catch (error) {
        console.error('Failed to update status to in-match:', error);
    }
};