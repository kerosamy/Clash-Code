package com.clashcode.backend.service;

import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.TestCaseRequestDto;
import com.clashcode.backend.dto.TestCaseResponsDto;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.TestCase;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestCaseService {

    public List<TestCaseResponsDto> getVisbleTestCasesForProblem(Problem problem) {
        List<TestCaseResponsDto> visibleTestCases = new ArrayList<>();

        for (TestCase testCase : problem.getTestCases()) {
            if(testCase.isVisible()){
                visibleTestCases.add(TestCaseResponsDto.builder().
                        input(testCase.getInput()).
                        build());
            }
        }
        return visibleTestCases;
    }

    public List<TestCase> getTestCasesFromRequestDto (ProblemRequestDto problemRequestDto , Problem problem) {
        List<TestCase> testCases = new ArrayList<>();
        for (TestCaseRequestDto testCaseRequestDto : problemRequestDto.getTestCases()) {
                testCases.add(TestCase.builder().
                        input(testCaseRequestDto.getInput())
                        .visible(testCaseRequestDto.isVisible())
                        .problem(problem)
                        .build());

        }
        return testCases;

    }

}
