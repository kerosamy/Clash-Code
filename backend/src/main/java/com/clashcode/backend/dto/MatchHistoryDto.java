package com.clashcode.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
