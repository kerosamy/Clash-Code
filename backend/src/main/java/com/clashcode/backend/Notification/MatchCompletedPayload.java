package com.clashcode.backend.Notification;

import com.clashcode.backend.enums.NotificationMode;
import com.clashcode.backend.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchCompletedPayload implements NotificationPayload {
    private final Long matchId;

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.MATCH_COMPLETED;
    }

    @Override
    public String getTitle() {
        return "Match Completed";
    }

    @Override
    public String getMessage() {
        return "The match has ended. Check your results!";
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