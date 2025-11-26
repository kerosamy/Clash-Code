package com.clashcode.backend.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsDto {
    int solvedProblems;
    int attemptedProblems;
    int matchesPlayed;
    int matchesWon;
}
