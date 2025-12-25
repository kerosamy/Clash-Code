package com.clashcode.backend.Notification;

import com.clashcode.backend.enums.NotificationMode;
import com.clashcode.backend.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResignedPayload implements NotificationPayload {
    private final String senderUsername;
    private final Long matchId;

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.USER_RESIGNED;
    }

    @Override
    public String getTitle() {
        return "Opponent Resigned";
    }

    @Override
    public String getMessage() {
        return senderUsername + " has resigned from the match. You win!";
    }

    @Override
    public NotificationMode getMode() {
        return NotificationMode.EPHEMERAL;
    }

    @Override
    public String getDestination(String recipientUsername) {
        return "/topic/match-pop/" + recipientUsername;
    }
}