package com.clashcode.backend.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchResultDto {
    private boolean isRated;
    private String username;
    private String avatarUrl;
    private Integer rank;
    private Integer rateChange;
    private Integer newRating;   
}
