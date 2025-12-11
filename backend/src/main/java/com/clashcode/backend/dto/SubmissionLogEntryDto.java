package com.clashcode.backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionLogEntryDto {
    private String submittedAt;
    private String submissionStatus;
    private int numberOfPassedTestCases;
    private int numberOfTotalTestCases;
}
