package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.LanguageVersion;
import com.clashcode.backend.enums.ProblemStatus;
import com.clashcode.backend.enums.ProblemTags;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.Solution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProblemMapperTest {

    private ProblemMapper problemMapper;

    @BeforeEach
    void setUp() {
        problemMapper = new ProblemMapper();
    }

    @Test
    void test_toProblem() {
        ProblemRequestDto request = new ProblemRequestDto();
        request.setTitle("Add Two Numbers");
        request.setInputFormat("Two integers a and b.");
        request.setOutputFormat("Print a + b.");
        request.setStatement("Given two integers a and b, print their sum.");
        request.setNotes("Notes about problem.");
        request.setMemoryLimit(64);
        request.setTimeLimit(1);
        request.setRate(200);
        request.setTags(Collections.singletonList(ProblemTags.MATH));
        request.setMainSolution("print(a + b)");
        request.setSolutionLanguage("PYTHON_3_8");

        Problem problem = problemMapper.toProblem(request);

        assertEquals("Add Two Numbers", problem.getTitle());
        assertEquals("Two integers a and b.", problem.getInputFormat());
        assertEquals("Print a + b.", problem.getOutputFormat());
        assertEquals("Given two integers a and b, print their sum.", problem.getStatement());
        assertEquals("Notes about problem.", problem.getNotes());
        assertEquals(64, problem.getMemoryLimit());
        assertEquals(1, problem.getTimeLimit());
        assertEquals(200, problem.getRate());
        assertEquals(Collections.singletonList(ProblemTags.MATH), problem.getTags());

        Solution solution = problem.getSolution();
        assertEquals("print(a + b)", solution.getSolutionCode());
        assertEquals(LanguageVersion.PYTHON_3_8, solution.getLanguageVersion());
    }

    @Test
    void test_toProblem_withNullFields() {
        ProblemRequestDto request = new ProblemRequestDto();
        request.setTitle("Empty Problem");
        request.setMainSolution(null);
        request.setSolutionLanguage("PYTHON_3_8");

        Problem problem = problemMapper.toProblem(request);

        assertEquals("Empty Problem", problem.getTitle());
        assertNull(problem.getSolution().getSolutionCode());
        assertEquals(LanguageVersion.PYTHON_3_8, problem.getSolution().getLanguageVersion());
    }

    @Test
    void test_toPartialResponseDto() {
        Problem problem = Problem.builder()
                .id(1L)
                .title("Sample Problem")
                .inputFormat("Input format")
                .outputFormat("Output format")
                .statement("Statement")
                .notes("Notes")
                .memoryLimit(128)
                .timeLimit(2)
                .rate(300)
                .tags(Collections.singletonList(ProblemTags.MATH))
                .submissionsCount(10L)
                .author("Admin")
                .build();

        List<TestCaseResponseDto> testCases = List.of(
                new TestCaseResponseDto("input1", "output1"),
                new TestCaseResponseDto("input2", "output2")
        );

        PartialProblemResponseDto dto = problemMapper.toPartialResponseDto(problem, testCases);

        assertEquals(1L, dto.getId());
        assertEquals("Sample Problem", dto.getTitle());
        assertEquals("Input format", dto.getInputFormat());
        assertEquals("Output format", dto.getOutputFormat());
        assertEquals("Statement", dto.getStatement());
        assertEquals("Notes", dto.getNotes());
        assertEquals(128, dto.getMemoryLimit());
        assertEquals(2, dto.getTimeLimit());
        assertEquals(300, dto.getRate());
        assertEquals(Collections.singletonList(ProblemTags.MATH), dto.getTags());
        assertEquals(10, dto.getSubmissionsCount());
        assertEquals("Admin", dto.getAuthor());
        assertEquals(testCases, dto.getVisibleTestCases());
    }

    @Test
    void test_toPartialResponseDto_emptyTestCases() {
        Problem problem = Problem.builder()
                .id(2L)
                .title("No TestCases")
                .memoryLimit(0)
                .timeLimit(0)
                .rate(0)
                .tags(Collections.emptyList())
                .submissionsCount(0L)
                .build();

        PartialProblemResponseDto dto = problemMapper.toPartialResponseDto(problem, Collections.emptyList());

        assertEquals(2L, dto.getId());
        assertEquals("No TestCases", dto.getTitle());
        assertEquals(0, dto.getVisibleTestCases().size());
    }

    @Test
    void test_toFullResponseDto() {
        Solution solution = Solution.builder()
                .solutionCode("def solve(): pass")
                .languageVersion(LanguageVersion.PYTHON_3_8)
                .build();

        Problem problem = Problem.builder()
                .id(3L)
                .title("Full Problem")
                .inputFormat("Input")
                .outputFormat("Output")
                .statement("Statement")
                .notes("Notes")
                .memoryLimit(256)
                .timeLimit(3)
                .rate(500)
                .tags(List.of(ProblemTags.DP, ProblemTags.GRAPH_THEORY))
                .submissionsCount(50L)
                .author("Creator")
                .solution(solution)
                .build();

        List<TestCaseResponseDto> testCases = List.of(
                new TestCaseResponseDto("in", "out")
        );

        FullProblemResponseDto dto = problemMapper.toFullResponseDto(problem, testCases);

        assertEquals(3L, dto.getId());
        assertEquals("Full Problem", dto.getTitle());
        assertEquals(500, dto.getRate());
        assertEquals("def solve(): pass", dto.getSolutionCode());
        assertEquals(LanguageVersion.PYTHON_3_8, dto.getSolutionLanguage());
        assertEquals("Creator", dto.getAuthor());
    }

    @Test
    void test_toFullResponseDto_withNullSolution() {
        Problem problem = Problem.builder()
                .id(4L)
                .title("No Solution")
                .submissionsCount(0L)
                .solution(null)
                .build();

        FullProblemResponseDto dto = problemMapper.toFullResponseDto(problem, Collections.emptyList());

        assertNull(dto.getSolutionCode());
        assertNull(dto.getSolutionLanguage());
    }

    @Test
    void test_toListDto() {
        Problem problem = Problem.builder()
                .id(5L)
                .title("Two Sum")
                .submissionsCount(42L)
                .tags(Collections.singletonList(ProblemTags.MATH))
                .rate(150)
                .author("Admin")
                .problemStatus(ProblemStatus.APPROVED)
                .build();

        ProblemListDto dto = problemMapper.toListDto(problem);

        assertEquals(5L, dto.getId());
        assertEquals("Two Sum", dto.getTitle());
        assertEquals(42L, dto.getSubmissionsCount());
        assertEquals(Collections.singletonList(ProblemTags.MATH), dto.getTags());
        assertEquals(150, dto.getRate());
        assertEquals("unsolved", dto.getAttempted());
        assertEquals("Admin", dto.getAuthor());
        assertNull(dto.getRejectionNote());
        assertEquals("APPROVED", dto.getStatus());
    }

    @Test
    void test_toListDto_withRejectionNote() {
        Problem problem = Problem.builder()
                .id(6L)
                .title("Rejected Problem")
                .submissionsCount(0L)
                .tags(null)
                .rate(100)
                .problemStatus(ProblemStatus.REJECTED)
                .build();

        ProblemListDto dto = problemMapper.toListDto(problem, "Too easy");

        assertEquals("Too easy", dto.getRejectionNote());
        assertEquals("REJECTED", dto.getStatus());
    }

    @Test
    void test_toListDto_emptyFields() {
        Problem problem = Problem.builder()
                .id(10L)
                .title("Edge Case")
                .submissionsCount(0L)
                .tags(null)
                .rate(0)
                .build();

        ProblemListDto dto = problemMapper.toListDto(problem);

        assertEquals(10L, dto.getId());
        assertEquals("Edge Case", dto.getTitle());
        assertEquals(0L, dto.getSubmissionsCount());
        assertNull(dto.getTags());
        assertEquals(0, dto.getRate());
        assertEquals("unsolved", dto.getAttempted());
    }

    @Test
    void test_updateProblem() {
        Solution existingSolution = Solution.builder()
                .solutionCode("old code")
                .languageVersion(LanguageVersion.JAVA_OPENJDK_11)
                .build();

        Problem problem = Problem.builder()
                .id(1L)
                .title("Old Title")
                .statement("Old Statement")
                .inputFormat("Old Input")
                .outputFormat("Old Output")
                .notes("Old Notes")
                .memoryLimit(64)
                .timeLimit(1)
                .rate(100)
                .tags(List.of(ProblemTags.MATH))
                .solution(existingSolution)
                .build();

        ProblemRequestDto dto = new ProblemRequestDto();
        dto.setTitle("New Title");
        dto.setStatement("New Statement");
        dto.setInputFormat("New Input");
        dto.setOutputFormat("New Output");
        dto.setNotes("New Notes");
        dto.setMemoryLimit(128);
        dto.setTimeLimit(2);
        dto.setRate(200);
        dto.setTags(List.of(ProblemTags.DP));
        dto.setMainSolution("new code");
        dto.setSolutionLanguage("PYTHON_3_8");

        problemMapper.updateProblem(problem, dto);

        assertEquals("New Title", problem.getTitle());
        assertEquals("New Statement", problem.getStatement());
        assertEquals("New Input", problem.getInputFormat());
        assertEquals("New Output", problem.getOutputFormat());
        assertEquals("New Notes", problem.getNotes());
        assertEquals(128, problem.getMemoryLimit());
        assertEquals(2, problem.getTimeLimit());
        assertEquals(200, problem.getRate());
        assertEquals(List.of(ProblemTags.DP), problem.getTags());
        assertEquals("new code", problem.getSolution().getSolutionCode());
        assertEquals(LanguageVersion.PYTHON_3_8, problem.getSolution().getLanguageVersion());
    }

    @Test
    void test_toProblem_invalidLanguage() {
        ProblemRequestDto request = new ProblemRequestDto();
        request.setTitle("Invalid Lang");
        request.setMainSolution("code");
        request.setSolutionLanguage("NON_EXISTENT");

        assertThrows(IllegalArgumentException.class, () -> problemMapper.toProblem(request));
    }
}
