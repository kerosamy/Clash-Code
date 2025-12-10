package com.clashcode.backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionLogDto {
    private String submittedAt;
    private String submissionStatus;
}
