package com.clashcode.backend.service;

import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.TestCaseRequestDto;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.TestCase;
import com.clashcode.backend.mapper.TestCaseMapper;
import com.clashcode.backend.repository.TestCaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestCaseServiceTest {

    private TestCaseRepository testCaseRepository;
    private TestCaseMapper testCaseMapper;
    private TestCaseService testCaseService;

    @BeforeEach
    void setUp() {
        testCaseRepository = Mockito.mock(TestCaseRepository.class);
        testCaseMapper = Mockito.mock(TestCaseMapper.class);
        testCaseService = new TestCaseService(testCaseRepository, testCaseMapper);
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

        // Mock mapper behavior
        TestCase tc1 = new TestCase();
        tc1.setInput("10 20");
        tc1.setVisible(true);
        tc1.setProblem(problem);

        TestCase tc2 = new TestCase();
        tc2.setInput("30 40");
        tc2.setVisible(false);
        tc2.setProblem(problem);

        when(testCaseMapper.toEntity(tcReq1, problem)).thenReturn(tc1);
        when(testCaseMapper.toEntity(tcReq2, problem)).thenReturn(tc2);

        List<TestCase> testCases = testCaseService.getTestCasesFromRequestDto(request, problem);

        assertEquals(2, testCases.size());

        assertEquals("10 20", testCases.get(0).getInput());
        assertTrue(testCases.get(0).isVisible());
        assertEquals(problem, testCases.get(0).getProblem());

        assertEquals("30 40", testCases.get(1).getInput());
        assertFalse(testCases.get(1).isVisible());
        assertEquals(problem, testCases.get(1).getProblem());

        // Verify mapper calls
        verify(testCaseMapper, times(1)).toEntity(tcReq1, problem);
        verify(testCaseMapper, times(1)).toEntity(tcReq2, problem);
    }
}
