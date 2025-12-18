package com.clashcode.backend.dto;

import com.clashcode.backend.enums.ProblemTags;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemRequestDto {
    private long id;
    private String title;
    private String inputFormat;
    private String outputFormat;
    private String statement;
    private String notes;
    private String mainSolution;
    private String solutionLanguage;
    private int timeLimit;
    private int memoryLimit;
    private int rate;
    private String author;

    @Builder.Default
    private List<ProblemTags> tags = new ArrayList<>();

    @Builder.Default
    private List<Boolean> visibleFlags = new ArrayList<>();
}
