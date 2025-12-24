package com.clashcode.backend.dto;

import com.clashcode.backend.enums.NotificationType;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private NotificationType type;
    private Long senderId;
    private String senderUsername;
    private Long recipientId;
    private String title;
    private String message;
    private Instant createdAt;
    private boolean read;
    private Long matchId;
}