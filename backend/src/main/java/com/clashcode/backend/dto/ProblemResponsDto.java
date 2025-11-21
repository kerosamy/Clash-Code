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
public class ProblemResponsDto {

    private Long id;

    private Long submissionsCount ;

    private String title ;

    private String inputFormat ;

    private String outputFormat ;

    private String statement ;

    private String notes ;

    private Integer timeLimit ;

    private Integer memoryLimit ;

    private ProblemRate rate ;

    private List<ProblemTags> tags = new ArrayList<>();

    private List<TestCaseResponsDto> visibleTestCases = new ArrayList<>();

}
