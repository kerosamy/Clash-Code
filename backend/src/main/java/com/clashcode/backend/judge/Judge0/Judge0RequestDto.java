package com.clashcode.backend.judge.Judge0;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Judge0RequestDto {
    @JsonProperty("source_code")
    private String sourceCode;

    @JsonProperty("stdin")
    private String stdin;

    @JsonProperty("language_id")
    private Integer languageId;

    @JsonProperty("expected_output")
    private String expectedOutput;

    @JsonProperty("time_limit")
    private Integer timeLimit;

    @JsonProperty("memory_limit")
    private Integer memoryLimit;
}
