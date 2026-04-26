import { apiRequest } from "./api";
import { UserStatus } from "../enums/UserStatus";

export type FriendStatus = "NONE" | "PENDING_SENT" | "PENDING_RECEIVED" | "FRIENDS";

export async function fetchFriendStatus(username: string): Promise<FriendStatus> {
  return apiRequest<FriendStatus>({
    method: "GET",
    url: `/friends/status/${username}`,
  });
}

export async function sendFriendRequest(username: string): Promise<void> {
  return apiRequest<void>({
    method: "POST",
    url: `/friends/send/${username}`,
  });
}

export async function acceptFriendRequest(username: string): Promise<void> {
  return apiRequest<void>({
    method: "POST",
    url: `/friends/accept/${username}`,
  });
}

export async function rejectFriendRequest(username: string): Promise<void> {
  return apiRequest<void>({
    method: "DELETE",
    url: `/friends/reject/${username}`,
  });
}

export async function removeFriend(username: string): Promise<void> {
  return apiRequest<void>({
    method: "DELETE",
    url: `/friends/remove/${username}`,
  });
}

export interface FriendDto {
  username: string;
  currentRate: number;
  imgUrl: string;
  status: FriendStatus;
  userStatus: UserStatus;
  requestedAt: string;
  updatedAt: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export async function getFriendsList(
  page: number = 0,
  size: number = 20
): Promise<Page<FriendDto>> {
  return apiRequest<Page<FriendDto>>({
    method: "GET",
    url: `/friends/list?page=${page}&size=${size}`,
  });
}

export async function getPendingFriendRequests(
  page: number = 0,
  size: number = 20
): Promise<Page<FriendDto>> {
  return apiRequest<Page<FriendDto>>({
    method: "GET",
    url: `/friends/received-requests?page=${page}&size=${size}`,
  });
}

export async function getSentFriendRequests(
  page: number = 0,
  size: number = 20
): Promise<Page<FriendDto>> {
  return apiRequest<Page<FriendDto>>({
    method: "GET",
    url: `/friends/sent-requests?page=${page}&size=${size}`,
  });
}

export async function searchFriends(
  query: string,
  page: number = 0,
  size: number = 20
): Promise<Page<FriendDto>> {
  return apiRequest<Page<FriendDto>>({
    method: "GET",
    url: `/friends/search?query=${encodeURIComponent(query)}&page=${page}&size=${size}`,
  });
}