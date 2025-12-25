package com.clashcode.backend.Notification;

import com.clashcode.backend.enums.NotificationMode;
import com.clashcode.backend.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubmissionResultPayload implements NotificationPayload {
    private final String senderUsername;
    private final String submissionStatus;
    private final Integer passedCases;
    private final Integer totalCases;
    private final Long matchId;

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.SUBMISSION_RESULT;
    }

    @Override
    public String getTitle() {
        return "Submission Result";
    }

    @Override
    public String getMessage() {
        return senderUsername + " got " + submissionStatus + " - " + passedCases + "/" + totalCases + " test cases passed";
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