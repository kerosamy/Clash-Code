package com.clashcode.backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchParticipantDto {
    private Long userId;
    private Integer rank;
    private Integer rateChange;
    private Integer newRating;
}
