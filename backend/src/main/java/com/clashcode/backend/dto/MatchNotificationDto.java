package com.clashcode.backend.dto;

import lombok.*;

@Getter
@AllArgsConstructor
public class MatchNotificationDto {
    private long matchId;
    private String senderUsername;
    private String title;
    private String message;
}
