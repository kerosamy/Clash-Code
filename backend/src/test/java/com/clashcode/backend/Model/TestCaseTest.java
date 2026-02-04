package com.clashcode.backend.Model;

import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestCaseTest {

    @Test
    void testTestCaseGettersAndSetters() {
        TestCase testCase = new TestCase();
        Problem problem = new Problem();

        testCase.setId(1L);
        testCase.setInputPath("/path/to/input.txt");
        testCase.setOutputPath("/path/to/output.txt");
        testCase.setVisible(true);
        testCase.setProblem(problem);

        assertEquals(1L, testCase.getId());
        assertEquals("/path/to/input.txt", testCase.getInputPath());
        assertEquals("/path/to/output.txt", testCase.getOutputPath());
        assertTrue(testCase.isVisible());
        assertEquals(problem, testCase.getProblem());
    }

    @Test
    void testTestCaseBuilder() {
        Problem problem = new Problem();

        TestCase testCase = TestCase.builder()
                .id(2L)
                .inputPath("/input2.txt")
                .outputPath("/output2.txt")
                .visible(false)
                .problem(problem)
                .build();

        assertEquals(2L, testCase.getId());
        assertEquals("/input2.txt", testCase.getInputPath());
        assertEquals("/output2.txt", testCase.getOutputPath());
        assertFalse(testCase.isVisible());
        assertEquals(problem, testCase.getProblem());
    }

    @Test
    void testTestCaseNoArgsConstructor() {
        TestCase testCase = new TestCase();
        assertNotNull(testCase);
    }

    @Test
    void testTestCaseAllArgsConstructor() {
        Problem problem = new Problem();

        TestCase testCase = new TestCase(1L, "/input.txt", "/output.txt", true, problem);

        assertEquals(1L, testCase.getId());
        assertEquals("/input.txt", testCase.getInputPath());
        assertEquals("/output.txt", testCase.getOutputPath());
        assertTrue(testCase.isVisible());
        assertEquals(problem, testCase.getProblem());
    }
}
