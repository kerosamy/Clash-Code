package com.clashcode.backend.service;

import com.clashcode.backend.dto.ProblemListDto;
import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.ProblemResponseDto;
import com.clashcode.backend.dto.TestCaseResponseDto;
import com.clashcode.backend.mapper.ProblemMapper;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.repository.ProblemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @Test
    void testGetProblemById_NotFound() {
        when(problemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> problemService.getProblemById(1L));

        verify(problemRepository, times(1)).findById(1L);
        verifyNoInteractions(testCaseService);
        verifyNoInteractions(problemMapper);
    }

    @Test
    void testGetAllProblems() {

        Problem problem1 = new Problem();
        problem1.setId(1L);
        Problem problem2 = new Problem();
        problem2.setId(2L);

        List<Problem> problems = List.of(problem1, problem2);

        Page<Problem> page = new PageImpl<>(problems, PageRequest.of(0, 10), problems.size());
        when(problemRepository.findAll(any(PageRequest.class))).thenReturn(page);

        ProblemListDto dto1 = new ProblemListDto();
        dto1.setId(1L);
        ProblemListDto dto2 = new ProblemListDto();
        dto2.setId(2L);

        when(problemMapper.toListDto(problem1)).thenReturn(dto1);
        when(problemMapper.toListDto(problem2)).thenReturn(dto2);

        Page<ProblemListDto> result = problemService.getAllProblems(0, 10);

        assertEquals(2, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(2L, result.getContent().get(1).getId());

        verify(problemRepository, times(1)).findAll(any(PageRequest.class));
        verify(problemMapper, times(1)).toListDto(problem1);
        verify(problemMapper, times(1)).toListDto(problem2);
    }

    @Test
    void testGetAllProblems_EmptyResult() {
        Page<Problem> emptyPage = new PageImpl<>(Collections.emptyList());
        when(problemRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

        Page<ProblemListDto> result = problemService.getAllProblems(0, 10);

        assertTrue(result.isEmpty());
        verify(problemRepository).findAll(any(PageRequest.class));
    }

    @Test
    void testGetAllProblems_UsesCorrectPageRequest() {
        int page = 3;
        int size = 20;

        Page<Problem> emptyPage = new PageImpl<>(Collections.emptyList());
        when(problemRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        problemService.getAllProblems(page, size);

        verify(problemRepository).findAll(
                argThat((Pageable p) ->
                        p.getPageNumber() == page &&
                                p.getPageSize() == size
                )
        );
    }


}
