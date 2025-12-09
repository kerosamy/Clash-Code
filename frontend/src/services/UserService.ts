import { apiRequest } from "./api";

export interface BackendUserResponse {
  username: string;
  rank: string;
  currentRate: number;
  maxRate: number;
  friendCount: number;
  avatarUrl: string;
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

export const splitUserData = (response: BackendUserResponse) => {
  const profileBasic: UserProfileBasic = {
    username: response.username,
    rank: response.rank,
    currentRate: response.currentRate,
    maxRate: response.maxRate,
    friendCount: response.friendCount,
    avatarUrl: response.avatarUrl,
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
  username: string;
  rank: string;
}

export async function searchUsers(username: string): Promise<UserSearchResponse[]> {
  return apiRequest<UserSearchResponse[]>({
    method: "GET",
    url: "/users/search",
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
