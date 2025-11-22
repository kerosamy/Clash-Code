package com.clashcode.backend.service;
import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.ProblemResponseDto;
import com.clashcode.backend.dto.TestCaseResponseDto;
import com.clashcode.backend.mapper.ProblemMapper;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.TestCase;
import com.clashcode.backend.repository.ProblemRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final TestCaseService testCaseService;
    private final ProblemMapper problemMapper;

    public ProblemService(ProblemRepository problemRepository,
                          TestCaseService testCaseService,
                          ProblemMapper problemMapper) {

        this.problemMapper = problemMapper;
        this.problemRepository = problemRepository;
        this.testCaseService = testCaseService;
    }

    public ProblemResponseDto getProblemById (Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));
        List<TestCaseResponseDto> visibleTestCases = testCaseService.getVisbleTestCasesForProblem(problem);
        return problemMapper.toResponseDto(problem, visibleTestCases);
    }

    public void addProblem (ProblemRequestDto problemRequestDto) {

        Problem problem = problemMapper.toProblem(problemRequestDto);
        List<TestCase> testCases = testCaseService.getTestCasesFromRequestDto(problemRequestDto , problem);
        problem.setTestCases(testCases);
        problemRepository.save(problem);
    }



}
