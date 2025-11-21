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
public class ProblemRequestDto {

    private String title ;

    private String inputFormat ;

    private String outputFormat ;

    private String statement ;

    private String notes ;

    private String mainSolution ;

    private Integer timeLimit ;

    private Integer memoryLimit ;

    private ProblemRate rate ;

    private List<ProblemTags> topics = new ArrayList<>();

    private List<TestCaseRequestDto> TestCases = new ArrayList<>();
}
