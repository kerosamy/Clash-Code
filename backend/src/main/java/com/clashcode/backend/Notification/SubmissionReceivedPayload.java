package com.clashcode.backend.Notification;

import com.clashcode.backend.enums.NotificationMode;
import com.clashcode.backend.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubmissionReceivedPayload implements NotificationPayload {
    private final String senderUsername;
    private final Long matchId;

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.SUBMISSION_RECEIVED;
    }

    @Override
    public String getTitle() {
        return "Code Submitted";
    }

    @Override
    public String getMessage() {
        return senderUsername + " submitted their solution";
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