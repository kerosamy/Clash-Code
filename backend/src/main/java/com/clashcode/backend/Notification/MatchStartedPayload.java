package com.clashcode.backend.Notification;

import com.clashcode.backend.enums.NotificationMode;
import com.clashcode.backend.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchStartedPayload implements NotificationPayload {
    private final Long matchId;
    private final Long problemId;

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.MATCH_STARTED;
    }

    @Override
    public String getTitle() {
        return "Match Started";
    }

    @Override
    public String getMessage() {
        return "Your match has started. Good luck!";
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