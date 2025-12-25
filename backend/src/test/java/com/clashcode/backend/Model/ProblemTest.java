package com.clashcode.backend.Model;

import com.clashcode.backend.enums.*;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.Solution;
import com.clashcode.backend.model.TestCase;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProblemTest {

    @Test
    void testProblemGettersAndSetters() {
        Problem problem = new Problem();
        Solution solution = new Solution();
        List<ProblemTags> tags = new ArrayList<>();
        tags.add(ProblemTags.DP);
        List<TestCase> testCases = new ArrayList<>();

        problem.setId(1L);
        problem.setSubmissionsCount(100L);
        problem.setTitle("Test Problem");
        problem.setInputFormat("Input format");
        problem.setOutputFormat("Output format");
        problem.setStatement("Problem statement");
        problem.setNotes("Some notes");
        problem.setAuthor("John Doe");
        problem.setTimeLimit(1000);
        problem.setMemoryLimit(256);
        problem.setProblemStatus(ProblemStatus.APPROVED);
        problem.setSolution(solution);
        problem.setRate(1500);
        problem.setTags(tags);
        problem.setTestCases(testCases);

        assertEquals(1L, problem.getId());
        assertEquals(100L, problem.getSubmissionsCount());
        assertEquals("Test Problem", problem.getTitle());
        assertEquals("Input format", problem.getInputFormat());
        assertEquals("Output format", problem.getOutputFormat());
        assertEquals("Problem statement", problem.getStatement());
        assertEquals("Some notes", problem.getNotes());
        assertEquals("John Doe", problem.getAuthor());
        assertEquals(1000, problem.getTimeLimit());
        assertEquals(256, problem.getMemoryLimit());
        assertEquals(ProblemStatus.APPROVED, problem.getProblemStatus());
        assertEquals(solution, problem.getSolution());
        assertEquals(1500, problem.getRate());
        assertEquals(tags, problem.getTags());
        assertEquals(testCases, problem.getTestCases());
    }

    @Test
    void testProblemBuilder() {
        Solution solution = new Solution();
        List<ProblemTags> tags = List.of(ProblemTags.DP);

        Problem problem = Problem.builder()
                .id(1L)
                .submissionsCount(50L)
                .title("DP Problem")
                .inputFormat("Input")
                .outputFormat("Output")
                .statement("Statement")
                .notes("Notes")
                .author("Jane")
                .timeLimit(2000)
                .memoryLimit(512)
                .problemStatus(ProblemStatus.REJECTED)
                .solution(solution)
                .rate(1800)
                .tags(tags)
                .build();

        assertEquals(1L, problem.getId());
        assertEquals(50L, problem.getSubmissionsCount());
        assertEquals("DP Problem", problem.getTitle());
        assertEquals(ProblemStatus.REJECTED, problem.getProblemStatus());
        assertEquals(1800, problem.getRate());
    }

    @Test
    void testProblemBuilderDefaults() {
        Problem problem = Problem.builder()
                .title("Test")
                .inputFormat("In")
                .outputFormat("Out")
                .statement("Stmt")
                .timeLimit(1000)
                .memoryLimit(128)
                .rate(1200)
                .build();

        assertEquals(0L, problem.getSubmissionsCount());
        assertEquals(ProblemStatus.PENDING_APPROVAL, problem.getProblemStatus());
        assertNotNull(problem.getTags());
        assertTrue(problem.getTags().isEmpty());
        assertNotNull(problem.getTestCases());
        assertTrue(problem.getTestCases().isEmpty());
    }

    @Test
    void testProblemNoArgsConstructor() {
        Problem problem = new Problem();
        assertNotNull(problem);
    }

    @Test
    void testProblemAllArgsConstructor() {
        Solution solution = new Solution();
        List<ProblemTags> tags = new ArrayList<>();
        List<TestCase> testCases = new ArrayList<>();

        Problem problem = new Problem(1L, 100L, "Title", "Input", "Output", "Statement", "Notes",
                "Author", 1000, 256, ProblemStatus.APPROVED, solution, 1500, tags, testCases);

        assertEquals(1L, problem.getId());
        assertEquals(100L, problem.getSubmissionsCount());
        assertEquals("Title", problem.getTitle());
        assertEquals(ProblemStatus.APPROVED, problem.getProblemStatus());
        assertEquals(1500, problem.getRate());
    }
}
