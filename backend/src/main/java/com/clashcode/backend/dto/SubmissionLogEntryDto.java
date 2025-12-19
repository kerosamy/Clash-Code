package com.clashcode.backend.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionLogEntryDto {
    private Long submissionId;
    private String submittedAt;
    private String status;
    private int numberOfPassedTestCases;
    private int numberOfTotalTestCases;
    private Integer numberOfCurrentTestCase;
}
