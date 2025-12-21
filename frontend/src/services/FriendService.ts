import { apiRequest } from "./api";

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
