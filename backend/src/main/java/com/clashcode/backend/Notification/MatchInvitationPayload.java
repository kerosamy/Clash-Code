package com.clashcode.backend.Notification;

import com.clashcode.backend.enums.NotificationMode;
import com.clashcode.backend.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchInvitationPayload implements NotificationPayload {
    private final String senderUsername;
    private final String senderAvatarUrl;
    private final Long senderId;

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.MATCH_INVITATION;
    }

    @Override
    public String getTitle() {
        return "Match Invitation";
    }

    @Override
    public String getMessage() {
        return senderUsername + " invited you to a match";
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