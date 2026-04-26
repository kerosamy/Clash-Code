package com.clashcode.backend.Notification;

import com.clashcode.backend.enums.NotificationMode;
import com.clashcode.backend.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FriendRequestReceivedPayload implements NotificationPayload {
    private final String senderUsername;
    private final String senderAvatarUrl;
    private final Long senderId;

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.FRIEND_REQUEST_RECEIVED;
    }

    @Override
    public String getTitle() {
        return "New Friend Request";
    }

    @Override
    public String getMessage() {
        return senderUsername + " sent you a friend request";
    }

    @Override
    public NotificationMode getMode() {
        return NotificationMode.PERSISTENT;
    }

    @Override
    public String getDestination(String recipientUsername) {
        return "/topic/match-pop/" + recipientUsername;
    }
}