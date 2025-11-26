package com.clashcode.backend.service;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.mapper.ProblemMapper;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.TestCase;
import com.clashcode.backend.enums.ProblemTags;
import com.clashcode.backend.repository.ProblemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

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

    @Test
    void testGetAllProblems_EmptyResult() {
        Page<Problem> emptyPage = new PageImpl<>(Collections.emptyList());
        when(problemRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

        Page<ProblemListDto> result = problemService.getAllProblems(0, 10);

        assertTrue(result.isEmpty());
        verify(problemRepository).findAll(any(PageRequest.class));
    }


    @Test
    void testGetFilteredProblems_WithTagsAndRate() {
        Problem problem = new Problem();
        problem.setId(1L);

        List<Problem> problems = List.of(problem);
        Page<Problem> page = new PageImpl<>(problems, PageRequest.of(0, 10), problems.size());

        when(problemRepository.findByTagsAndRate(anyList(), anyInt(), any(PageRequest.class))).thenReturn(page);

        ProblemListDto dto = new ProblemListDto();
        dto.setId(1L);
        when(problemMapper.toListDto(problem)).thenReturn(dto);

        Page<ProblemListDto> result = problemService.getFilteredProblems(
                List.of(ProblemTags.MATH, ProblemTags.IMPLEMENTATION), 200, 0, 10
        );

        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().getFirst().getId());

        verify(problemRepository, times(1))
                .findByTagsAndRate(anyList(), anyInt(), any(PageRequest.class));
        verify(problemMapper, times(1)).toListDto(problem);
    }

    @Test
    void testGetFilteredProblems_EmptyTags() {
        // Given: empty tags list with a rate value
        Problem problem = new Problem();
        problem.setId(2L);

        Page<Problem> page = new PageImpl<>(List.of(problem));

        // When tags are empty, they become null, so with rate provided, findByRate is called
        when(problemRepository.findByRate(200, PageRequest.of(0, 10))).thenReturn(page);

        ProblemListDto dto = new ProblemListDto();
        dto.setId(2L);
        when(problemMapper.toListDto(problem)).thenReturn(dto);

        // When: Pass empty list for tags → should be treated as null and call findByRate
        Page<ProblemListDto> result = problemService.getFilteredProblems(Collections.emptyList(), 200, 0, 10);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(2L, result.getContent().getFirst().getId());

        verify(problemRepository).findByRate(200, PageRequest.of(0, 10));
        verify(problemRepository, never()).findByTagsAndRate(any(), any(), any());
    }

    @Test
    void testGetFilteredProblems_OnlyTags() {
        // Given: tags without rate
        Problem problem = new Problem();
        problem.setId(3L);

        Page<Problem> page = new PageImpl<>(List.of(problem));
        List<ProblemTags> tags = List.of(ProblemTags.MATH);

        when(problemRepository.findByTags(tags, PageRequest.of(0, 10))).thenReturn(page);

        ProblemListDto dto = new ProblemListDto();
        dto.setId(3L);
        when(problemMapper.toListDto(problem)).thenReturn(dto);

        // When: Pass only tags, no rate
        Page<ProblemListDto> result = problemService.getFilteredProblems(tags, null, 0, 10);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(3L, result.getContent().getFirst().getId());

        verify(problemRepository).findByTags(tags, PageRequest.of(0, 10));
    }

    @Test
    void testGetFilteredProblems_OnlyRate() {
        // Given: rate without tags
        Problem problem = new Problem();
        problem.setId(4L);

        Page<Problem> page = new PageImpl<>(List.of(problem));

        when(problemRepository.findByRate(300, PageRequest.of(0, 10))).thenReturn(page);

        ProblemListDto dto = new ProblemListDto();
        dto.setId(4L);
        when(problemMapper.toListDto(problem)).thenReturn(dto);

        // When: Pass only rate, no tags
        Page<ProblemListDto> result = problemService.getFilteredProblems(null, 300, 0, 10);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(4L, result.getContent().getFirst().getId());

        verify(problemRepository).findByRate(300, PageRequest.of(0, 10));
    }

    @Test
    void testGetFilteredProblems_NoFilters() {
        // Given: no tags and no rate
        Problem problem = new Problem();
        problem.setId(5L);

        Page<Problem> page = new PageImpl<>(List.of(problem));

        when(problemRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        ProblemListDto dto = new ProblemListDto();
        dto.setId(5L);
        when(problemMapper.toListDto(problem)).thenReturn(dto);

        // When: Pass null for both tags and rate
        Page<ProblemListDto> result = problemService.getFilteredProblems(null, null, 0, 10);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(5L, result.getContent().getFirst().getId());

        verify(problemRepository).findAll(PageRequest.of(0, 10));
    }


}
