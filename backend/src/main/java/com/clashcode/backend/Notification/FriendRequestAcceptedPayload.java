package com.clashcode.backend.Notification;

import com.clashcode.backend.enums.NotificationMode;
import com.clashcode.backend.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FriendRequestAcceptedPayload implements NotificationPayload {
    private final String accepterUsername;
    private final String accepterAvatarUrl;
    private final Long accepterId;

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.FRIEND_REQUEST_ACCEPTED;
    }

    @Override
    public String getTitle() {
        return "Friend Request Accepted";
    }

    @Override
    public String getMessage() {
        return accepterUsername + " accepted your friend request";
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