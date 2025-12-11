package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.ProblemListDto;
import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.ProblemResponseDto;
import com.clashcode.backend.dto.TestCaseResponseDto;
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

    public ProblemResponseDto toResponseDto (Problem problem , List<TestCaseResponseDto> visibleTestCases) {
        return ProblemResponseDto
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

    public ProblemListDto toListDto(Problem problem) {
        return ProblemListDto.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .submissionsCount(problem.getSubmissionsCount())
                .tags(problem.getTags())
                .rate(problem.getRate())
                .attempted("unsolved")   // placeholder
                .author(problem.getAuthor())
                .build();
    }
}
