package com.clashcode.backend.service;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.mapper.ProblemMapper;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.TestCase;
import com.clashcode.backend.repository.ProblemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

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
        problemRepository = mock(ProblemRepository.class);
        testCaseService = mock(TestCaseService.class);
        problemMapper = mock(ProblemMapper.class);
        problemService = new ProblemService(problemRepository, testCaseService, problemMapper);
    }

    // ---------------- Test: getProblemById ----------------
    @Test
    void testGetProblemById_Success() {
        Problem problem = new Problem();
        problem.setId(1L);
        ProblemResponseDto responseDto = new ProblemResponseDto();
        responseDto.setId(1L);

        when(problemRepository.findById(1L)).thenReturn(Optional.of(problem));
        when(testCaseService.getVisibleTestCasesForProblem(problem)).thenReturn(List.of());
        when(problemMapper.toResponseDto(problem, List.of())).thenReturn(responseDto);

        ProblemResponseDto result = problemService.getProblemById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(problemRepository).findById(1L);
        verify(testCaseService).getVisibleTestCasesForProblem(problem);
        verify(problemMapper).toResponseDto(problem, List.of());
    }

    @Test
    void testGetProblemById_NotFound() {
        when(problemRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            problemService.getProblemById(99L);
        });

        assertEquals("Problem not found", exception.getMessage());
        verify(problemRepository).findById(99L);
    }

    // ---------------- Test: addProblem ----------------
    @Test
    void testAddProblem() {
        ProblemRequestDto requestDto = new ProblemRequestDto();
        requestDto.setTitle("Add Two Integers");

        Problem problem = new Problem();
        Problem savedProblem = new Problem();
        savedProblem.setId(1L);

        TestCase testCase = new TestCase();
        List<TestCase> testCases = List.of(testCase);

        when(problemMapper.toProblem(requestDto)).thenReturn(problem);
        when(problemRepository.save(problem)).thenReturn(savedProblem);
        when(testCaseService.addTestCases(anyList(), eq(problem), any())).thenReturn(testCases);

        problemService.addProblem(requestDto, List.of(mock(MultipartFile.class)));

        // verify save called twice: first for problem, then after setting test cases
        verify(problemRepository, times(2)).save(any(Problem.class));
        verify(testCaseService).addTestCases(anyList(), eq(problem), any());
        verify(problemMapper).toProblem(requestDto);
    }

    // ---------------- Test: getAllProblems ----------------
    @Test
    void testGetAllProblems() {
        Problem problem1 = new Problem();
        problem1.setId(1L);
        Problem problem2 = new Problem();
        problem2.setId(2L);

        ProblemListDto dto1 = new ProblemListDto();
        dto1.setId(1L);
        ProblemListDto dto2 = new ProblemListDto();
        dto2.setId(2L);

        Page<Problem> page = new PageImpl<>(List.of(problem1, problem2));

        when(problemRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);
        when(problemMapper.toListDto(problem1)).thenReturn(dto1);
        when(problemMapper.toListDto(problem2)).thenReturn(dto2);

        Page<ProblemListDto> result = problemService.getAllProblems(0, 10);

        assertEquals(2, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(2L, result.getContent().get(1).getId());

        verify(problemRepository).findAll(PageRequest.of(0, 10));
        verify(problemMapper).toListDto(problem1);
        verify(problemMapper).toListDto(problem2);
    }
}
