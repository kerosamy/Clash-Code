package com.clashcode.backend.judge.Judge0;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Judge0ResponseDto {
    @JsonProperty("stdout")
    private String stdout;

    @JsonProperty("stderr")
    private String stderr;

    @JsonProperty("compile_output")
    private String compileOutput;

    @JsonProperty("time")
    private double time;

    @JsonProperty("memory")
    private double memory;

    @JsonProperty("status")
    private Judge0StatusDto status;

}
