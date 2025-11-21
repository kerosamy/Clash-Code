package com.clashcode.backend.service;
import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.TestCaseRequestDto;
import com.clashcode.backend.dto.TestCaseResponsDto;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.TestCase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestCaseServiceTest {

    private final TestCaseService testCaseService = new TestCaseService();

    @Test
    void testGetVisibleTestCasesForProblem() {
        Problem problem = new Problem();
        TestCase tc1 = new TestCase();
        tc1.setInput("1 2");
        tc1.setVisible(true);

        TestCase tc2 = new TestCase();
        tc2.setInput("3 4");
        tc2.setVisible(false);

        TestCase tc3 = new TestCase();
        tc3.setInput("5 6");
        tc3.setVisible(true);

        problem.setTestCases(Arrays.asList(tc1, tc2, tc3));

        List<TestCaseResponsDto> visible = testCaseService.getVisbleTestCasesForProblem(problem);

        assertEquals(2, visible.size());
        assertEquals("1 2", visible.get(0).getInput());
        assertEquals("5 6", visible.get(1).getInput());
    }

    @Test
    void testGetTestCasesFromRequestDto() {
        ProblemRequestDto request = new ProblemRequestDto();

        TestCaseRequestDto tcReq1 = new TestCaseRequestDto();
        tcReq1.setInput("10 20");
        tcReq1.setVisible(true);

        TestCaseRequestDto tcReq2 = new TestCaseRequestDto();
        tcReq2.setInput("30 40");
        tcReq2.setVisible(false);

        request.setTestCases(Arrays.asList(tcReq1, tcReq2));

        Problem problem = new Problem();

        List<TestCase> testCases = testCaseService.getTestCasesFromRequestDto(request, problem);

        assertEquals(2, testCases.size());

        assertEquals("10 20", testCases.get(0).getInput());
        assertTrue(testCases.get(0).isVisible());
        assertEquals(problem, testCases.get(0).getProblem());

        assertEquals("30 40", testCases.get(1).getInput());
        assertFalse(testCases.get(1).isVisible());
        assertEquals(problem, testCases.get(1).getProblem());
    }
}
