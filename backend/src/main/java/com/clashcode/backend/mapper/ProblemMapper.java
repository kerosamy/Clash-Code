package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.LanguageVersion;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.Solution;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProblemMapper {

    public Problem toProblem (ProblemRequestDto problemRequestDto) {
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
                .solution(
                        Solution.builder()
                                .solutionCode(problemRequestDto.getMainSolution())
                                .languageVersion(LanguageVersion.valueOf(problemRequestDto.getSolutionLanguage()))
                                .build()
                )
                .build();
    }

    public PracticeProblemResponseDto toResponseDto (Problem problem , List<TestCaseResponseDto> visibleTestCases) {
        Solution solution = problem.getSolution();
        return PracticeProblemResponseDto
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
                .visibleTestCases(visibleTestCases)
                .author(problem.getAuthor())
                .build();
    }

    public FullProblemResponseDto toFullResponseDto (Problem problem , List<TestCaseResponseDto> visibleTestCases) {
        Solution solution = problem.getSolution();
        return FullProblemResponseDto
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
                .visibleTestCases(visibleTestCases)
                .author(problem.getAuthor())
                .solutionCode(solution != null ? solution.getSolutionCode() : null)
                .solutionLanguage(solution != null ? solution.getLanguageVersion() : null)
                .build();
    }

    public ProblemListDto toListDto(Problem problem) {
        return toListDto(problem, null);
    }

    // Call this when you have a rejection note
    public ProblemListDto toListDto(Problem problem, String rejectionNote) {
        return ProblemListDto.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .submissionsCount(problem.getSubmissionsCount())
                .tags(problem.getTags())
                .rate(problem.getRate())
                .attempted("unsolved")   // placeholder
                .author(problem.getAuthor())
                .rejectionNote(rejectionNote) // nullable
                .build();
    }

    public void updateProblem(Problem problem, ProblemRequestDto dto) {

        problem.setTitle(dto.getTitle());
        problem.setStatement(dto.getStatement());
        problem.setInputFormat(dto.getInputFormat());
        problem.setOutputFormat(dto.getOutputFormat());
        problem.setNotes(dto.getNotes());
        problem.setMemoryLimit(dto.getMemoryLimit());
        problem.setTimeLimit(dto.getTimeLimit());
        problem.setRate(dto.getRate());
        problem.setTags(dto.getTags());
        Solution solution = problem.getSolution();
        solution.setSolutionCode(dto.getMainSolution());
        solution.setLanguageVersion(
                LanguageVersion.valueOf(dto.getSolutionLanguage())
        );
        problem.setSolution(solution);
    }
}