package com.clashcode.backend.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestcaseRunRequestDto {
    private List<String> stdin;
    private String sourceCode;
    private String language;
    private Integer timeLimit;
    private Integer memoryLimit;
}
