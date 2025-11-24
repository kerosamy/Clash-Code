package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.ProblemListDto;
import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.ProblemResponseDto;
import com.clashcode.backend.dto.TestCaseResponseDto;
import com.clashcode.backend.enums.LanguageVersion;
import com.clashcode.backend.enums.ProblemTags;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.Solution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProblemMapperTest {

    private ProblemMapper problemMapper;

    @BeforeEach
    void setUp() {
        problemMapper = new ProblemMapper();
    }

    @Test
    void testToProblem() {
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
        request.setSolutionLanguage("PYTHON_3_8"); // must match enum constant

        // Act
        Problem problem = problemMapper.toProblem(request);

        // Assert
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
    void testToResponseDto() {
        // Arrange
        Problem problem = Problem.builder()
                .id(1L)
                .title("Sample Problem")
                .memoryLimit(128)
                .timeLimit(2)
                .rate(300)
                .tags(Collections.singletonList(ProblemTags.MATH))
                .submissionsCount(10L)
                .build();

        List<TestCaseResponseDto> testCases = List.of(
                new TestCaseResponseDto("input1"),
                new TestCaseResponseDto("input2")
        );

        // Act
        ProblemResponseDto dto = problemMapper.toResponseDto(problem, testCases);

        // Assert
        assertEquals(1L, dto.getId());
        assertEquals("Sample Problem", dto.getTitle());
        assertEquals(128, dto.getMemoryLimit());
        assertEquals(2, dto.getTimeLimit());
        assertEquals(300, dto.getRate());
        assertEquals(Collections.singletonList(ProblemTags.MATH), dto.getTags());
        assertEquals(10, dto.getSubmissionsCount());
        assertEquals(testCases, dto.getVisibleTestCases());
    }

    @Test
    void testToListDto() {

        Problem problem = Problem.builder()
                .id(5L)
                .title("Two Sum")
                .submissionsCount(42L)
                .tags(Collections.singletonList(ProblemTags.MATH))
                .rate(150)
                .build();


        ProblemListDto dto = problemMapper.toListDto(problem);


        assertEquals(5L, dto.getId());
        assertEquals("Two Sum", dto.getTitle());
        assertEquals(42L, dto.getSubmissionsCount());
        assertEquals(Collections.singletonList(ProblemTags.MATH), dto.getTags());
        assertEquals(150, dto.getRate());
        assertEquals("unsolved", dto.getAttempted()); // matches the placeholder
    }

}
