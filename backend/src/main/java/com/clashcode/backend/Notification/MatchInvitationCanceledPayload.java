package com.clashcode.backend.Notification;

import com.clashcode.backend.enums.NotificationMode;
import com.clashcode.backend.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchInvitationCanceledPayload implements NotificationPayload {
    private final String senderUsername;

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.MATCH_INVITATION_CANCELED;
    }

    @Override
    public String getTitle() {
        return "Match Invitation Canceled";
    }

    @Override
    public String getMessage() {
        return senderUsername + " has canceled their match invitation";
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