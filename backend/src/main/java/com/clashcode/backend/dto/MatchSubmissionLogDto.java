package com.clashcode.backend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchSubmissionLogDto {
    private Long playerId;
    private List<SubmissionLogDto> submissions;
}

