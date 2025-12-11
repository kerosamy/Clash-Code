package com.clashcode.backend.dto;

import lombok.*;

@Getter
@AllArgsConstructor
public class MatchStartNotificationDto {
    private long matchId;
    private String title;
    private String message;
}
