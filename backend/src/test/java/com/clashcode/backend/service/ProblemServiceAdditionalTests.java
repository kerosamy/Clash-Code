package com.clashcode.backend.service;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.ProblemStatus;
import com.clashcode.backend.enums.ProblemTags;
import com.clashcode.backend.mapper.ProblemMapper;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.ProblemReview;
import com.clashcode.backend.model.TestCase;
import com.clashcode.backend.repository.ProblemRepository;
import com.clashcode.backend.repository.ProblemReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProblemServiceAdditionalTests {

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

        problemService = new ProblemService(
                problemRepository,
                testCaseService,
                problemMapper,
                problemReviewRepository
        );
    }

    // ==================== GET FULL PROBLEM BY ID TESTS ====================

    @Nested
    @DisplayName("getFullProblemById Tests")
    class GetFullProblemByIdTests {

        @Test
        @DisplayName("Should get full problem by ID successfully")
        void test_getFullProblemById_success() {
            Long problemId = 1L;
            Problem problem = new Problem();
            problem.setId(problemId);
            problem.setTitle("Two Sum");
            problem.setStatement("Find two numbers that add up to target");

            TestCaseResponseDto testCase1 = new TestCaseResponseDto();
            TestCaseResponseDto testCase2 = new TestCaseResponseDto();
            List<TestCaseResponseDto> visibleTestCases = List.of(testCase1, testCase2);

            FullProblemResponseDto expectedDto = new FullProblemResponseDto();
            expectedDto.setId(problemId);
            expectedDto.setTitle("Two Sum");

            when(problemRepository.findById(problemId)).thenReturn(Optional.of(problem));
            when(testCaseService.getVisibleTestCasesForProblem(problem)).thenReturn(visibleTestCases);
            when(problemMapper.toFullResponseDto(problem, visibleTestCases)).thenReturn(expectedDto);

            FullProblemResponseDto result = problemService.getFullProblemById(problemId);

            assertNotNull(result);
            assertEquals(problemId, result.getId());
            assertEquals("Two Sum", result.getTitle());
            verify(problemRepository).findById(problemId);
            verify(testCaseService).getVisibleTestCasesForProblem(problem);
            verify(problemMapper).toFullResponseDto(problem, visibleTestCases);
        }

        @Test
        @DisplayName("Should throw exception when problem not found")
        void test_getFullProblemById_notFound() {
            Long problemId = 999L;

            when(problemRepository.findById(problemId)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    problemService.getFullProblemById(problemId)
            );

            assertEquals("Problem not found", exception.getMessage());
            verify(problemRepository).findById(problemId);
            verify(testCaseService, never()).getVisibleTestCasesForProblem(any());
            verify(problemMapper, never()).toFullResponseDto(any(), any());
        }

        @Test
        @DisplayName("Should get full problem with empty visible test cases")
        void test_getFullProblemById_emptyTestCases() {
            Long problemId = 5L;
            Problem problem = new Problem();
            problem.setId(problemId);

            List<TestCaseResponseDto> emptyTestCases = Collections.emptyList();
            FullProblemResponseDto expectedDto = new FullProblemResponseDto();
            expectedDto.setId(problemId);

            when(problemRepository.findById(problemId)).thenReturn(Optional.of(problem));
            when(testCaseService.getVisibleTestCasesForProblem(problem)).thenReturn(emptyTestCases);
            when(problemMapper.toFullResponseDto(problem, emptyTestCases)).thenReturn(expectedDto);

            FullProblemResponseDto result = problemService.getFullProblemById(problemId);

            assertNotNull(result);
            verify(testCaseService).getVisibleTestCasesForProblem(problem);
        }

        @Test
        @DisplayName("Should get full problem with multiple visible test cases")
        void test_getFullProblemById_multipleTestCases() {
            Long problemId = 10L;
            Problem problem = new Problem();
            problem.setId(problemId);

            List<TestCaseResponseDto> testCases = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                TestCaseResponseDto tc = new TestCaseResponseDto();
                testCases.add(tc);
            }

            FullProblemResponseDto expectedDto = new FullProblemResponseDto();
            expectedDto.setId(problemId);

            when(problemRepository.findById(problemId)).thenReturn(Optional.of(problem));
            when(testCaseService.getVisibleTestCasesForProblem(problem)).thenReturn(testCases);
            when(problemMapper.toFullResponseDto(problem, testCases)).thenReturn(expectedDto);

            FullProblemResponseDto result = problemService.getFullProblemById(problemId);

            assertNotNull(result);
            verify(problemMapper).toFullResponseDto(problem, testCases);
        }
    }

    // ==================== GET PENDING PROBLEMS TESTS ====================

    @Nested
    @DisplayName("getPendingProblems Tests")
    class GetPendingProblemsTests {

        @Test
        @DisplayName("Should get pending problems successfully")
        void test_getPendingProblems_success() {
            int page = 0;
            int size = 10;

            Problem problem1 = new Problem();
            problem1.setId(1L);
            problem1.setProblemStatus(ProblemStatus.PENDING_APPROVAL);
            problem1.setTitle("Problem 1");

            Problem problem2 = new Problem();
            problem2.setId(2L);
            problem2.setProblemStatus(ProblemStatus.PENDING_APPROVAL);
            problem2.setTitle("Problem 2");

            List<Problem> problems = List.of(problem1, problem2);
            Page<Problem> problemPage = new PageImpl<>(problems);

            ProblemListDto dto1 = new ProblemListDto();
            dto1.setId(1L);
            ProblemListDto dto2 = new ProblemListDto();
            dto2.setId(2L);

            when(problemRepository.findByProblemStatus(
                    ProblemStatus.PENDING_APPROVAL,
                    PageRequest.of(page, size)
            )).thenReturn(problemPage);
            when(problemMapper.toListDto(problem1)).thenReturn(dto1);
            when(problemMapper.toListDto(problem2)).thenReturn(dto2);

            Page<ProblemListDto> result = problemService.getPendingProblems(page, size);

            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            assertEquals(1L, result.getContent().get(0).getId());
            assertEquals(2L, result.getContent().get(1).getId());
            verify(problemRepository).findByProblemStatus(
                    ProblemStatus.PENDING_APPROVAL,
                    PageRequest.of(page, size)
            );
        }

        @Test
        @DisplayName("Should return empty page when no pending problems")
        void test_getPendingProblems_emptyResult() {
            int page = 0;
            int size = 10;

            Page<Problem> emptyPage = new PageImpl<>(Collections.emptyList());

            when(problemRepository.findByProblemStatus(
                    ProblemStatus.PENDING_APPROVAL,
                    PageRequest.of(page, size)
            )).thenReturn(emptyPage);

            Page<ProblemListDto> result = problemService.getPendingProblems(page, size);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            assertEquals(0, result.getTotalElements());
        }

        @Test
        @DisplayName("Should handle pagination correctly")
        void test_getPendingProblems_pagination() {
            int page = 2;
            int size = 5;

            List<Problem> problems = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Problem p = new Problem();
                p.setId((long) (i + 10));
                p.setProblemStatus(ProblemStatus.PENDING_APPROVAL);
                problems.add(p);
            }

            Page<Problem> problemPage = new PageImpl<>(problems, PageRequest.of(page, size), 20);

            when(problemRepository.findByProblemStatus(
                    ProblemStatus.PENDING_APPROVAL,
                    PageRequest.of(page, size)
            )).thenReturn(problemPage);
            when(problemMapper.toListDto(any(Problem.class))).thenReturn(new ProblemListDto());

            Page<ProblemListDto> result = problemService.getPendingProblems(page, size);

            assertNotNull(result);
            assertEquals(5, result.getNumberOfElements());
            assertEquals(20, result.getTotalElements());
            assertEquals(2, result.getNumber());
            assertEquals(4, result.getTotalPages());
            verify(problemMapper, times(5)).toListDto(any(Problem.class));
        }

        @Test
        @DisplayName("Should get first page of pending problems")
        void test_getPendingProblems_firstPage() {
            int page = 0;
            int size = 3;

            Problem problem = new Problem();
            problem.setId(100L);
            problem.setProblemStatus(ProblemStatus.PENDING_APPROVAL);

            Page<Problem> problemPage = new PageImpl<>(List.of(problem));
            ProblemListDto dto = new ProblemListDto();
            dto.setId(100L);

            when(problemRepository.findByProblemStatus(
                    ProblemStatus.PENDING_APPROVAL,
                    PageRequest.of(page, size)
            )).thenReturn(problemPage);
            when(problemMapper.toListDto(problem)).thenReturn(dto);

            Page<ProblemListDto> result = problemService.getPendingProblems(page, size);

            assertEquals(1, result.getContent().size());
            assertEquals(100L, result.getContent().get(0).getId());
        }

        @Test
        @DisplayName("Should handle large page size")
        void test_getPendingProblems_largePageSize() {
            int page = 0;
            int size = 100;

            List<Problem> problems = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                Problem p = new Problem();
                p.setId((long) i);
                p.setProblemStatus(ProblemStatus.PENDING_APPROVAL);
                problems.add(p);
            }

            Page<Problem> problemPage = new PageImpl<>(problems);

            when(problemRepository.findByProblemStatus(
                    ProblemStatus.PENDING_APPROVAL,
                    PageRequest.of(page, size)
            )).thenReturn(problemPage);
            when(problemMapper.toListDto(any(Problem.class))).thenReturn(new ProblemListDto());

            Page<ProblemListDto> result = problemService.getPendingProblems(page, size);

            assertEquals(50, result.getNumberOfElements());
        }
    }

    // ==================== ADD PROBLEM TESTS (UPDATE FLOW) ====================

    @Nested
    @DisplayName("addProblem Update Flow Tests")
    class AddProblemUpdateFlowTests {

        @Test
        @DisplayName("Should throw exception when updating non-existent problem")
        void test_addProblem_updateNonExistent() {
            Long nonExistentId = 999L;
            String username = "author";

            ProblemRequestDto requestDto = new ProblemRequestDto();
            requestDto.setId(nonExistentId);

            when(problemRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    problemService.addProblem(requestDto, List.of(), username)
            );

            assertEquals("Problem not found", exception.getMessage());
            verify(problemRepository).findById(nonExistentId);
        }

        @Test
        @DisplayName("Should clear existing test cases when updating problem")
        void test_addProblem_clearExistingTestCases() {
            Long problemId = 10L;
            String username = "user";

            ProblemRequestDto requestDto = new ProblemRequestDto();
            requestDto.setId(problemId);
            requestDto.setVisibleFlags(List.of(true));

            Problem existingProblem = new Problem();
            existingProblem.setId(problemId);

            TestCase oldTestCase = new TestCase();
            List<TestCase> oldTestCases = new ArrayList<>();
            oldTestCases.add(oldTestCase);
            existingProblem.setTestCases(oldTestCases);

            TestCase newTestCase = new TestCase();
            List<TestCase> newTestCases = List.of(newTestCase);

            when(problemRepository.findById(problemId)).thenReturn(Optional.of(existingProblem));
            when(testCaseService.addTestCases(anyList(), eq(existingProblem), anyList()))
                    .thenReturn(newTestCases);

            problemService.addProblem(requestDto, List.of(mock(MultipartFile.class)), username);

            verify(testCaseService).deleteByProblem(existingProblem);
            assertTrue(existingProblem.getTestCases().isEmpty() ||
                    existingProblem.getTestCases().equals(newTestCases));
        }
    }

    // ==================== ADD PROBLEM TESTS (CREATE FLOW) ====================

    @Nested
    @DisplayName("addProblem Create Flow Tests")
    class AddProblemCreateFlowTests {

        @Test
        @DisplayName("Should create new problem with author set")
        void test_addProblem_createNewWithAuthor() {
            String username = "problemAuthor";

            ProblemRequestDto requestDto = new ProblemRequestDto();
            requestDto.setId(0L); // ID = 0 indicates new problem
            requestDto.setTitle("New Problem");
            requestDto.setStatement("New description");
            requestDto.setVisibleFlags(List.of(true, true));

            Problem newProblem = new Problem();
            newProblem.setTitle(requestDto.getTitle());

            Problem savedProblem = new Problem();
            savedProblem.setId(1L);
            savedProblem.setAuthor(username);

            TestCase testCase = new TestCase();
            List<TestCase> testCases = List.of(testCase);

            MultipartFile file = mock(MultipartFile.class);
            List<MultipartFile> files = List.of(file);

            when(problemMapper.toProblem(requestDto)).thenReturn(newProblem);
            when(problemRepository.save(newProblem)).thenReturn(savedProblem);
            when(testCaseService.addTestCases(files, newProblem, requestDto.getVisibleFlags()))
                    .thenReturn(testCases);

            problemService.addProblem(requestDto, files, username);

            verify(problemMapper).toProblem(requestDto);
            verify(problemRepository, times(2)).save(any(Problem.class));
            verify(testCaseService).addTestCases(files, newProblem, requestDto.getVisibleFlags());
            assertEquals(username, newProblem.getAuthor());
        }

        @Test
        @DisplayName("Should create problem with empty test cases list")
        void test_addProblem_createWithEmptyTestCases() {
            String username = "author";

            ProblemRequestDto requestDto = new ProblemRequestDto();
            requestDto.setId(0L);
            requestDto.setVisibleFlags(Collections.emptyList());

            Problem problem = new Problem();
            Problem savedProblem = new Problem();
            savedProblem.setId(1L);

            List<TestCase> emptyTestCases = Collections.emptyList();

            when(problemMapper.toProblem(requestDto)).thenReturn(problem);
            when(problemRepository.save(problem)).thenReturn(savedProblem);
            when(testCaseService.addTestCases(anyList(), eq(problem), anyList()))
                    .thenReturn(emptyTestCases);

            problemService.addProblem(requestDto, Collections.emptyList(), username);

            verify(testCaseService).addTestCases(anyList(), eq(problem), anyList());
            verify(problemRepository, times(2)).save(any(Problem.class));
        }

        @Test
        @DisplayName("Should create problem with multiple test cases")
        void test_addProblem_createWithMultipleTestCases() {
            String username = "multiTestAuthor";

            ProblemRequestDto requestDto = new ProblemRequestDto();
            requestDto.setId(0L);
            requestDto.setVisibleFlags(List.of(true, false, true, false));

            Problem problem = new Problem();
            Problem savedProblem = new Problem();
            savedProblem.setId(100L);

            List<TestCase> testCases = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                testCases.add(new TestCase());
            }

            List<MultipartFile> files = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                files.add(mock(MultipartFile.class));
            }

            when(problemMapper.toProblem(requestDto)).thenReturn(problem);
            when(problemRepository.save(problem)).thenReturn(savedProblem);
            when(testCaseService.addTestCases(files, problem, requestDto.getVisibleFlags()))
                    .thenReturn(testCases);

            problemService.addProblem(requestDto, files, username);

            verify(testCaseService).addTestCases(files, problem, requestDto.getVisibleFlags());
            assertEquals(testCases, problem.getTestCases());
        }
    }

    // ==================== INTEGRATION TESTS ====================

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should handle full workflow: create -> update -> approve")
        void test_fullWorkflow_createUpdateApprove() {
            String username = "integrationUser";

            // Step 1: Create problem
            ProblemRequestDto createDto = new ProblemRequestDto();
            createDto.setId(0L);
            createDto.setTitle("Integration Test Problem");

            Problem newProblem = new Problem();
            Problem savedProblem = new Problem();
            savedProblem.setId(50L);

            when(problemMapper.toProblem(createDto)).thenReturn(newProblem);
            when(problemRepository.save(any(Problem.class))).thenReturn(savedProblem);
            when(testCaseService.addTestCases(anyList(), any(), anyList())).thenReturn(List.of());

            problemService.addProblem(createDto, List.of(), username);

            // Step 2: Update problem
            ProblemRequestDto updateDto = new ProblemRequestDto();
            updateDto.setId(50L);
            updateDto.setTitle("Updated Title");

            Problem existingProblem = new Problem();
            existingProblem.setId(50L);
            existingProblem.setTestCases(new ArrayList<>());

            when(problemRepository.findById(50L)).thenReturn(Optional.of(existingProblem));

            problemService.addProblem(updateDto, List.of(), username);

            // Step 3: Approve problem
            when(problemRepository.findById(50L)).thenReturn(Optional.of(existingProblem));
            when(problemReviewRepository.findByProblemId(50L)).thenReturn(Optional.empty());

            problemService.acceptProblem(50L);

            assertEquals(ProblemStatus.APPROVED, existingProblem.getProblemStatus());
        }

        @Test
        @DisplayName("Should handle problem visibility correctly across different operations")
        void test_problemVisibility_acrossOperations() {
            // Create problem
            Problem problem = new Problem();
            problem.setId(75L);
            problem.setProblemStatus(ProblemStatus.APPROVED);

            when(problemRepository.findById(75L)).thenReturn(Optional.of(problem));
            when(testCaseService.getVisibleTestCasesForProblem(problem))
                    .thenReturn(List.of(new TestCaseResponseDto()));
            when(problemMapper.toPartialResponseDto(any(), anyList()))
                    .thenReturn(new PartialProblemResponseDto());
            when(problemMapper.toFullResponseDto(any(), anyList()))
                    .thenReturn(new FullProblemResponseDto());

            // Get partial view
            problemService.getPartialProblemById(75L);
            verify(problemMapper).toPartialResponseDto(eq(problem), anyList());

            // Get full view
            problemService.getFullProblemById(75L);
            verify(problemMapper).toFullResponseDto(eq(problem), anyList());
        }
    }
}