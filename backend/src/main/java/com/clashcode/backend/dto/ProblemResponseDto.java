package com.clashcode.backend.dto;

import com.clashcode.backend.enums.ProblemRate;
import com.clashcode.backend.enums.ProblemTags;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemResponseDto {

    private Long id;

    private Long submissionsCount;

    private String title;

    private String inputFormat;

    private String outputFormat;

    private String statement;

    private String notes;

    private Integer timeLimit;

    private Integer memoryLimit;

    private ProblemRate rate;

    @Builder.Default
    private List<ProblemTags> tags = new ArrayList<>();

    @Builder.Default
    private List<TestCaseResponseDto> visibleTestCases = new ArrayList<>();

}
