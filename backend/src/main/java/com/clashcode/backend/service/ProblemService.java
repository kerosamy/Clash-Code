package com.clashcode.backend.service;

import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.ProblemResponsDto;
import com.clashcode.backend.enums.Judge0Language;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.TestCase;
import com.clashcode.backend.repository.ProblemRepository;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class ProblemService {


    private final ProblemRepository problemRepository;
    private final TestCaseService testCaseService;

    public ProblemService(ProblemRepository problemRepository , TestCaseService testCaseService) {
        this.problemRepository = problemRepository;
        this.testCaseService = testCaseService;
    }

    public ProblemResponsDto getProblemById (Long id) {
        return mappingProblemToResponsDto(problemRepository.getReferenceById(id));
    }

    public ProblemResponsDto mappingProblemToResponsDto (Problem problem) {
        return ProblemResponsDto
                        .builder()
                        .id(problem.getId())
                        .submissionsCount(problem.getSubmissionsCount())
                        .title(problem.getTitle())
                        .inputFormat(problem.getInputFormat())
                        .outputFormat(problem.getOutputFormat())
                        .statement(problem.getStatement())
                        .notes(problem.getNotes())
                        .memoryLimit(problem.getMemoryLimit())
                        .timeLimit(problem.getTimeLimit())
                        .rate(problem.getRate())
                        .tags(problem.getTags())
                        .visibleTestCases(testCaseService.getVisbleTestCasesForProblem(problem))
                        .build();
    }

    public void addProblem (ProblemRequestDto problemRequestDto) {
        Problem problem = mappingRequestDtoToProblem(problemRequestDto);
        List<TestCase> testCases = testCaseService.getTestCasesFromRequestDto(problemRequestDto , problem);
        problem.setTestCases(testCases);
        problemRepository.save(problem);
    }

    public Problem mappingRequestDtoToProblem (ProblemRequestDto problemRequestDto) {
        return Problem.builder()
                .title(problemRequestDto.getTitle())
                .inputFormat(problemRequestDto.getInputFormat())
                .outputFormat(problemRequestDto.getOutputFormat())
                .statement(problemRequestDto.getStatement())
                .notes(problemRequestDto.getNotes())
                .memoryLimit(problemRequestDto.getMemoryLimit())
                .timeLimit(problemRequestDto.getTimeLimit())
                .rate(problemRequestDto.getRate())
                .tags(problemRequestDto.getTags())
                .mainSolution(problemRequestDto.getMainSolution())
                .judge0Language(Judge0Language.fromLabel(problemRequestDto.getSolutionLanguage()))
                .build();
    }

}
