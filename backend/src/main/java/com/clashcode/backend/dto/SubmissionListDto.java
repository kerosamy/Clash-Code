package com.clashcode.backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionListDto {
    private Long submissionId;
    private Long problemId;
    private int timeTaken;
    private int memoryTaken;
    private int numberOfPassedTestCases;
    private int numberOfTotalTestCases;
    private int numberOfCurrentTestCase ;
    private String submittedAt;
    private String problemTitle;
    private String submissionStatus;
    private Long matchId;
}
