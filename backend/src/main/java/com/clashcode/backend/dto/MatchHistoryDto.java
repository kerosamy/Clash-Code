package com.clashcode.backend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MatchHistoryDto {
    private Long matchId;
    private LocalDateTime time;
    private String opponent;
    private String problem;
    private Integer rank;
    private Integer rateChange;
    private Integer newRating;
    private boolean isRated;
}
