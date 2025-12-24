import { apiRequest } from "./api";
import type { NotificationRowProps } from "../components/common/NotificationRow";

interface NotificationResponse {
  id: number;
  type: string;
  senderId: number;
  senderUsername: string;
  recipientId: number;
  title: string;
  message: string;
  createdAt: string;
  read: boolean;
  matchId?: number;
}

interface MatchResponseDto {
  id: number;
  duration: number;
  gameMode: string;
  matchState: string;
  problem: any;
  participants: any[];
}

export async function fetchNotifications(category: "all" | "match" | "friend" = "all"): Promise<NotificationRowProps[]> {
  const params: Record<string, string> = {};
  
  if (category !== "all") {
    params.category = category;
  }

  const data = await apiRequest<NotificationResponse[]>({
    method: "GET",
    url: "/notifications",
    params,
  });

  return data.map(mapNotificationToRow);
}

export async function getUnreadCount(): Promise<number> {
  return await apiRequest<number>({
    method: "GET",
    url: "/notifications/unread-count",
  });
}

export async function markNotificationAsRead(notificationId: number): Promise<void> {
  await apiRequest<void>({
    method: "PATCH",
    url: `/notifications/${notificationId}/read`,
  });
}

// Updated to return the notification ID
export async function sendMatchInvite(recipientUsername: string): Promise<number> {
  return await apiRequest<number>({
    method: "POST",
    url: `/matches/invite/${recipientUsername}`,
  });
}

export async function cancelMatchInvite(notificationId: number): Promise<void> {
  console.log('Sending match cancell to:', notificationId);

  await apiRequest<void>({
    method: "PATCH",
    url: `/matches/invite/${notificationId}/cancel`,
  });
}

export async function acceptMatchInvite(notificationId: number): Promise<MatchResponseDto> {
  return await apiRequest<MatchResponseDto>({
    method: "POST",
    url: `/matches/invite/${notificationId}/accept`,
  });
}

function mapNotificationToRow(notification: NotificationResponse): NotificationRowProps {
  const matchData = extractMatchData(notification);

  return {
    id: notification.id,
    type: notification.type as any,
    senderId: notification.senderId,
    senderUsername: notification.senderUsername,
    recipientId: notification.recipientId,
    title: notification.title,
    message: notification.message,
    createdAt: notification.createdAt,
    read: notification.read,
    matchId: notification.matchId,
    ...matchData,
  };
}

function extractMatchData(notification: NotificationResponse): Partial<NotificationRowProps> {
  const matchData: Partial<NotificationRowProps> = {};

  // Extract submission status from message
  const statusMatch = notification.message.match(/got\s+(\w+)|status[:\s]+(\w+)/i);
  if (statusMatch) {
    matchData.submissionStatus = statusMatch[1] || statusMatch[2];
  }

  // Extract test cases from message
  const casesMatch = notification.message.match(/\((\d+)\/(\d+)\)|(\d+)\/(\d+)/);
  if (casesMatch) {
    matchData.passedCases = parseInt(casesMatch[1] || casesMatch[3]);
    matchData.totalCases = parseInt(casesMatch[2] || casesMatch[4]);
  }

  return matchData;
}