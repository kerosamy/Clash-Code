package com.clashcode.backend.service;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.ProblemStatus;
import com.clashcode.backend.mapper.ProblemMapper;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.ProblemReview;
import com.clashcode.backend.model.TestCase;
import com.clashcode.backend.enums.ProblemTags;
import com.clashcode.backend.repository.ProblemRepository;
import com.clashcode.backend.repository.ProblemReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    private ProblemReviewRepository problemReviewRepository;

    @BeforeEach
    void setUp() {
        problemRepository = mock(ProblemRepository.class);
        testCaseService = mock(TestCaseService.class);
        problemMapper = mock(ProblemMapper.class);
        problemReviewRepository = mock(ProblemReviewRepository.class);

        problemService = new ProblemService(problemRepository, testCaseService, problemMapper,problemReviewRepository);
    }

    // ---------------- Test: getProblemById ----------------
    @Test
    void testGetProblemById_Success() {
        Problem problem = new Problem();
        problem.setId(1L);
        PartialProblemResponseDto responseDto = new PartialProblemResponseDto();
        responseDto.setId(1L);

        when(problemRepository.findById(1L)).thenReturn(Optional.of(problem));
        when(testCaseService.getVisibleTestCasesForProblem(problem)).thenReturn(List.of());
        when(problemMapper.toPartialResponseDto(problem, List.of())).thenReturn(responseDto);

        PartialProblemResponseDto result = problemService.getPartialProblemById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(problemRepository).findById(1L);
        verify(testCaseService).getVisibleTestCasesForProblem(problem);
        verify(problemMapper).toPartialResponseDto(problem, List.of());
    }

    @Test
    void testGetProblemById_NotFound() {
        when(problemRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            problemService.getPartialProblemById(99L);
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

        problemService.addProblem(requestDto, List.of(mock(MultipartFile.class)),any());

        // verify save called twice: first for problem, then after setting test cases
        verify(problemRepository, times(2)).save(any(Problem.class));
        verify(testCaseService).addTestCases(anyList(), eq(problem), any());
        verify(problemMapper).toProblem(requestDto);
    }

    // ---------------- Test: getAllProblems ----------------
    @Test
    void testGetApprovedProblems() {
        Problem problem1 = new Problem();
        problem1.setId(1L);
        problem1.setProblemStatus(ProblemStatus.APPROVED);

        Problem problem2 = new Problem();
        problem2.setId(2L);
        problem2.setProblemStatus(ProblemStatus.APPROVED);

        ProblemListDto dto1 = new ProblemListDto();
        dto1.setId(1L);
        ProblemListDto dto2 = new ProblemListDto();
        dto2.setId(2L);

        Page<Problem> page = new PageImpl<>(List.of(problem1, problem2));

        when(problemRepository.findByProblemStatus(ProblemStatus.APPROVED, PageRequest.of(0, 10)))
                .thenReturn(page);
        when(problemMapper.toListDto(problem1)).thenReturn(dto1);
        when(problemMapper.toListDto(problem2)).thenReturn(dto2);

        Page<ProblemListDto> result = problemService.getApprovedProblems(0, 10);

        assertEquals(2, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(2L, result.getContent().get(1).getId());

        verify(problemRepository).findByProblemStatus(ProblemStatus.APPROVED, PageRequest.of(0, 10));
        verify(problemMapper).toListDto(problem1);
        verify(problemMapper).toListDto(problem2);
    }


    @Test
    void testGetApprovedProblems_EmptyResult() {
        Page<Problem> emptyPage = new PageImpl<>(Collections.emptyList());
        when(problemRepository.findByProblemStatus(eq(ProblemStatus.APPROVED), any(PageRequest.class)))
                .thenReturn(emptyPage);

        Page<ProblemListDto> result = problemService.getApprovedProblems(0, 10);

        assertTrue(result.isEmpty());
        verify(problemRepository).findByProblemStatus(eq(ProblemStatus.APPROVED), any(PageRequest.class));
    }


    @Test
    void testGetFilteredProblems_WithTagsAndRateRange() {
        Problem problem = new Problem();
        problem.setId(1L);

        List<Problem> problems = List.of(problem);
        Page<Problem> page = new PageImpl<>(problems, PageRequest.of(0, 10), problems.size());

        // Updated repository call to match new method
        when(problemRepository.findByTagsAndRateRange(anyList(), anyLong(), anyInt(), anyInt(), any(PageRequest.class)))
                .thenReturn(page);

        ProblemListDto dto = new ProblemListDto();
        dto.setId(1L);
        when(problemMapper.toListDto(problem)).thenReturn(dto);

        Page<ProblemListDto> result = problemService.getFilteredProblems(
                List.of(ProblemTags.MATH, ProblemTags.IMPLEMENTATION),
                100, // minRate
                200, // maxRate
                0,
                10
        );

        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());

        verify(problemRepository, times(1))
                .findByTagsAndRateRange(anyList(), eq(2L), eq(100), eq(200), any(PageRequest.class));
        verify(problemMapper, times(1)).toListDto(problem);
    }

    @Test
    void testGetFilteredProblems_EmptyTags() {
        Problem problem = new Problem();
        problem.setId(2L);

        Page<Problem> page = new PageImpl<>(List.of(problem));
        // Updated repository call for rate range
        when(problemRepository.findByRateBetween(eq(200), eq(200), any(PageRequest.class))).thenReturn(page);

        ProblemListDto dto = new ProblemListDto();
        dto.setId(2L);
        when(problemMapper.toListDto(problem)).thenReturn(dto);

        Page<ProblemListDto> result = problemService.getFilteredProblems(
                Collections.emptyList(),
                200,
                200,
                0,
                10
        );

        assertEquals(1, result.getContent().size());
        assertEquals(2L, result.getContent().getFirst().getId());

        verify(problemRepository).findByRateBetween(200, 200, PageRequest.of(0, 10));
        verify(problemRepository, never()).findByTagsAndRateRange(anyList(), anyLong(), anyInt(), anyInt(), any());
    }

    @Test
    void testGetFilteredProblems_OnlyTags() {
        Problem problem = new Problem();
        problem.setId(3L);

        Page<Problem> page = new PageImpl<>(List.of(problem));
        List<ProblemTags> tags = List.of(ProblemTags.MATH);

        when(problemRepository.findByTags(tags, 1L, PageRequest.of(0, 10))).thenReturn(page);

        ProblemListDto dto = new ProblemListDto();
        dto.setId(3L);
        when(problemMapper.toListDto(problem)).thenReturn(dto);

        Page<ProblemListDto> result = problemService.getFilteredProblems(
                tags,
                null,
                null,
                0,
                10
        );

        assertEquals(1, result.getContent().size());
        assertEquals(3L, result.getContent().getFirst().getId());

        verify(problemRepository).findByTags(tags, 1L, PageRequest.of(0, 10));
    }

    @Test
    void testGetFilteredProblems_OnlyRateRange() {
        Problem problem = new Problem();
        problem.setId(4L);

        Page<Problem> page = new PageImpl<>(List.of(problem));
        when(problemRepository.findByRateBetween(eq(300), eq(300), any(PageRequest.class))).thenReturn(page);

        ProblemListDto dto = new ProblemListDto();
        dto.setId(4L);
        when(problemMapper.toListDto(problem)).thenReturn(dto);

        Page<ProblemListDto> result = problemService.getFilteredProblems(
                null,
                300,
                300,
                0,
                10
        );

        assertEquals(1, result.getContent().size());
        assertEquals(4L, result.getContent().getFirst().getId());

        verify(problemRepository).findByRateBetween(300, 300, PageRequest.of(0, 10));
    }

    @Test
    void testGetFilteredProblems_NoFilters() {
        Problem problem = new Problem();
        problem.setId(5L);

        Page<Problem> page = new PageImpl<>(List.of(problem));
        when(problemRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        ProblemListDto dto = new ProblemListDto();
        dto.setId(5L);
        when(problemMapper.toListDto(problem)).thenReturn(dto);

        Page<ProblemListDto> result = problemService.getFilteredProblems(
                null,
                null,
                null,
                0,
                10
        );

        assertEquals(1, result.getContent().size());
        assertEquals(5L, result.getContent().getFirst().getId());

        verify(problemRepository).findAll(PageRequest.of(0, 10));
    }

    @Test
    void testSearchProblemsByName() {
        Problem problem = new Problem();
        problem.setId(1L);
        problem.setTitle("Multiply Two Integers");

        Page<Problem> page = new PageImpl<>(List.of(problem));
        when(problemRepository.findByTitleContainingIgnoreCase(eq("Multiply"), any(PageRequest.class)))
                .thenReturn(page);

        ProblemListDto dto = new ProblemListDto();
        dto.setId(1L);
        when(problemMapper.toListDto(problem)).thenReturn(dto);

        Page<ProblemListDto> result = problemService.searchProblemsByName("Multiply", 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().getFirst().getId());
        verify(problemRepository, times(1))
                .findByTitleContainingIgnoreCase(eq("Multiply"), any(PageRequest.class));
        verify(problemMapper, times(1)).toListDto(problem);
    }

    @Test
    void testSearchProblemsByName_EmptyResult() {
        Page<Problem> emptyPage = new PageImpl<>(Collections.emptyList());
        when(problemRepository.findByTitleContainingIgnoreCase(eq("Nonexistent"), any(PageRequest.class)))
                .thenReturn(emptyPage);

        Page<ProblemListDto> result = problemService.searchProblemsByName("Nonexistent", 0, 10);
        assertTrue(result.isEmpty());
    }
    
    // ---------------- Test: acceptProblem ----------------
@Test
@DisplayName("Accept Problem - Success")
void testAcceptProblem() {
    Long problemId = 1L;
    Problem problem = new Problem();
    problem.setId(problemId);
    problem.setProblemStatus(ProblemStatus.PENDING_APPROVAL);

    when(problemRepository.findById(problemId)).thenReturn(Optional.of(problem));
    when(problemReviewRepository.findByProblemId(problemId)).thenReturn(Optional.empty());

    problemService.acceptProblem(problemId);

    assertEquals(ProblemStatus.APPROVED, problem.getProblemStatus());
    verify(problemRepository).save(problem);
    // Verify it tries to delete any existing review notes
    verify(problemReviewRepository).findByProblemId(problemId);
}

// ---------------- Test: rejectProblem ----------------
// ---------------- Test: acceptProblem ----------------
@Test
@DisplayName("Accept Problem - Updates status and deletes existing review")
void testAcceptProblem_Success() {
    Long problemId = 1L;
    Problem problem = new Problem();
    problem.setId(problemId);
    problem.setProblemStatus(ProblemStatus.PENDING_APPROVAL);

    when(problemRepository.findById(problemId)).thenReturn(Optional.of(problem));
    when(problemReviewRepository.findByProblemId(problemId)).thenReturn(Optional.of(new ProblemReview()));

    problemService.acceptProblem(problemId);

    assertEquals(ProblemStatus.APPROVED, problem.getProblemStatus());
    verify(problemRepository).save(problem);
    verify(problemReviewRepository).delete(any(ProblemReview.class));
}

    // ---------------- Test: rejectProblem ----------------
    @Test
    @DisplayName("Reject Problem - Create new review record")
    void testRejectProblem_NewReview() {
        Long problemId = 1L;
        String note = "Insufficient test cases.";
        Problem problem = new Problem();
        problem.setId(problemId);

        when(problemRepository.findById(problemId)).thenReturn(Optional.of(problem));
        when(problemReviewRepository.findByProblemId(problemId)).thenReturn(Optional.empty());

        problemService.rejectProblem(problemId, note);

        assertEquals(ProblemStatus.REJECTED, problem.getProblemStatus());
        verify(problemReviewRepository).save(argThat(review ->
                review.getNote().equals(note) && review.getProblemId().equals(problemId)
        ));
    }

    @Test
    @DisplayName("Reject Problem - Update existing review record")
    void testRejectProblem_UpdateExisting() {
        Long problemId = 1L;
        String newNote = "Updated rejection reason.";
        Problem problem = new Problem();
        ProblemReview existingReview = new ProblemReview();
        existingReview.setNote("Old note");

        when(problemRepository.findById(problemId)).thenReturn(Optional.of(problem));
        when(problemReviewRepository.findByProblemId(problemId)).thenReturn(Optional.of(existingReview));

        problemService.rejectProblem(problemId, newNote);

        assertEquals(newNote, existingReview.getNote());
        verify(problemReviewRepository).save(existingReview);
    }
}
