package com.clashcode.backend.service;

import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.ProblemResponseDto;
import com.clashcode.backend.dto.TestCaseResponseDto;
import com.clashcode.backend.mapper.ProblemMapper;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.repository.ProblemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProblemServiceTest {

    private ProblemRepository problemRepository;
    private TestCaseService testCaseService;
    private ProblemMapper problemMapper;
    private ProblemService problemService;

    @BeforeEach
    void setUp() {
        problemRepository = Mockito.mock(ProblemRepository.class);
        testCaseService = Mockito.mock(TestCaseService.class);
        problemMapper = Mockito.mock(ProblemMapper.class);
        problemService = new ProblemService(problemRepository, testCaseService, problemMapper);
    }

    @Test
    void testAddProblem() {
        ProblemRequestDto request = new ProblemRequestDto();
        request.setTitle("Add Two Numbers");
        request.setMainSolution("solution code");
        request.setSolutionLanguage("C++ (GCC 9.2.0)");

        Problem problem = new Problem();
        when(problemMapper.toProblem(request)).thenReturn(problem);
        when(testCaseService.getTestCasesFromRequestDto(request, problem)).thenReturn(Collections.emptyList());
        when(problemRepository.save(problem)).thenReturn(problem);

        problemService.addProblem(request);

        verify(problemMapper, times(1)).toProblem(request);
        verify(testCaseService, times(1)).getTestCasesFromRequestDto(request, problem);
        verify(problemRepository, times(1)).save(problem);
    }

    @Test
    void testGetProblemById() {
        Problem problem = new Problem();
        problem.setId(1L);

        List<TestCaseResponseDto> visibleTestCases = Collections.emptyList();

        when(problemRepository.findById(1L)).thenReturn(Optional.of(problem));
        when(testCaseService.getVisbleTestCasesForProblem(problem)).thenReturn(visibleTestCases);
        when(problemMapper.toResponseDto(problem, visibleTestCases)).thenReturn(
                ProblemResponseDto.builder()
                        .id(1L)
                        .build()
        );

        ProblemResponseDto dto = problemService.getProblemById(1L);

        assertEquals(1L, dto.getId());
        verify(problemRepository, times(1)).findById(1L);
        verify(testCaseService, times(1)).getVisbleTestCasesForProblem(problem);
        verify(problemMapper, times(1)).toResponseDto(problem, visibleTestCases);
    }
}
