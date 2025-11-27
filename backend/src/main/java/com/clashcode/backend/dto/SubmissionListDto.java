package com.clashcode.backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionListDto {

    private String submissionStatus;

    private int timeTaken;

    private int memoryTaken;

    private String submittedAt;

}
