import { apiRequest } from "./api";
import type { NotificationRowProps } from "../components/common/NotificationRow";

interface NotificationResponse {
  id: number;
  type: string;
  senderId: number;
  recipientId: number;
  title: string;
  message: string;
  createdAt: string;
  read: boolean;
}

export async function fetchNotifications(): Promise<NotificationRowProps[]> {
  const data = await apiRequest<NotificationResponse[]>({
    method: "GET",
    url: "/notifications",
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

function mapNotificationToRow(notification: NotificationResponse): NotificationRowProps {
  // to do -> extract sender username from title 
  // to do -> adjust based on backend response
  const senderUsername = extractSenderUsername(notification);
  
  const matchData = extractMatchData(notification);

  return {
    id: notification.id,
    type: notification.type as any,
    senderId: notification.senderId,
    senderUsername,
    recipientId: notification.recipientId,
    title: notification.title,
    message: notification.message,
    createdAt: notification.createdAt,
    read: notification.read,
    ...matchData,
  };
}

function extractSenderUsername(notification: NotificationResponse): string {
  // extract username from title or message
  // format "username invited you to a match"
  const titleMatch = notification.title.match(/^(\w+)/);
  if (titleMatch) {
    return titleMatch[1];
  }
  return "System";
}

function extractMatchData(notification: NotificationResponse): Partial<NotificationRowProps> {
  const matchData: Partial<NotificationRowProps> = {};

  // to do -> extract match ID from message
  const matchIdMatch = notification.message.match(/match[:\s#]+(\d+)/i);
  if (matchIdMatch) {
    matchData.matchId = parseInt(matchIdMatch[1]);
  }

  // to do -> extract submission status
  const statusMatch = notification.message.match(/status[:\s]+(\w+)/i);
  if (statusMatch) {
    matchData.submissionStatus = statusMatch[1];
  }

  // to do -> extract test cases
  const casesMatch = notification.message.match(/(\d+)\/(\d+)\s+cases/i);
  if (casesMatch) {
    matchData.passedCases = parseInt(casesMatch[1]);
    matchData.totalCases = parseInt(casesMatch[2]);
  }

  return matchData;
}