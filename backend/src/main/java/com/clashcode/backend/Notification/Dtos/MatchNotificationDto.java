package com.clashcode.backend.Notification.Dtos;

import com.clashcode.backend.Notification.NotificationPayload;
import com.clashcode.backend.enums.NotificationMode;
import com.clashcode.backend.enums.NotificationType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchNotificationDto implements NotificationPayload {
    @Builder.Default
    private Long matchId = null;
    private String senderUsername;
    private NotificationType notificationType;
    private String title;
    private String message;

    @Builder.Default
    private String submissionStatus = null;

    @Builder.Default
    private Integer passedCases = null;

    @Builder.Default
    private Integer totalCases = null;

    private NotificationMode mode;

    @Override
    public String getDestination(String recipientUsername) {
        return "/topic/match-pop/" + recipientUsername;
    }
}
