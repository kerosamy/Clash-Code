package com.clashcode.backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionDetailsDto {
    private String submissionLang;
    private String submissionCode;
    private String problemTitle;
    private String username;
    private String submissionStatus;
}
