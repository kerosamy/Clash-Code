package com.clashcode.backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionRequestDto {

    private Long userId;

    private Long problemId;

    private String code;

    private String codeLanguage;
}
