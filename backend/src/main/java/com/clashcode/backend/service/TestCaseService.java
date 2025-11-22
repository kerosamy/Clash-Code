package com.clashcode.backend.service;

import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.TestCaseRequestDto;
import com.clashcode.backend.dto.TestCaseResponseDto;
import com.clashcode.backend.mapper.TestCaseMapper;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.TestCase;
import com.clashcode.backend.repository.TestCaseRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final TestCaseMapper testCaseMapper;
    public TestCaseService(TestCaseRepository testCaseRepository , TestCaseMapper testCaseMapper) {
        this.testCaseRepository = testCaseRepository;
        this.testCaseMapper = testCaseMapper;
    }

    public List<TestCaseResponseDto> getVisbleTestCasesForProblem(Problem problem) {
        List<TestCaseResponseDto> visibleTestCases = new ArrayList<>();

        for (TestCase testCase : testCaseRepository.findByProblemAndVisibleTrue(problem)) {
                visibleTestCases.add(testCaseMapper.toResponseDto(testCase));
        }

        return visibleTestCases;
    }

    public List<TestCase> getTestCasesFromRequestDto (ProblemRequestDto problemRequestDto , Problem problem) {
        List<TestCase> testCases = new ArrayList<>();

        for (TestCaseRequestDto testCaseRequestDto : problemRequestDto.getTestCases()) {
                testCases.add(testCaseMapper.toEntity(testCaseRequestDto,problem));
        }

        return testCases;

    }

}
