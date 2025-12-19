package com.clashcode.backend.dto;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchSubmissionLogDto {
    private String username;
    private String avatarUrl;
    private String rank;
    private List<SubmissionLogEntryDto> submissions;
}

