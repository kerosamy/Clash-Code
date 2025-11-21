package com.clashcode.backend.service;

import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.ProblemResponsDto;
import com.clashcode.backend.enums.Judge0Language;
import com.clashcode.backend.enums.ProblemRate;
import com.clashcode.backend.enums.ProblemTags;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.repository.ProblemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProblemServiceTest {

    @Autowired
    private ProblemService problemService;

    @MockitoBean
    private ProblemRepository problemRepository;

    @MockitoBean
    private TestCaseService testCaseService;

    @Test
    void testAddProblem() {
        // Prepare request with all required fields
        ProblemRequestDto request = new ProblemRequestDto();
        request.setTitle("Add Two Numbers");
        request.setInputFormat("Two integers a and b.");
        request.setOutputFormat("Print a + b.");
        request.setStatement("Given two integers a and b, print their sum.");
        request.setNotes("a and b can be negative.");
        request.setMemoryLimit(64);
        request.setTimeLimit(1);
        request.setRate(ProblemRate.RATE_200);
        request.setTags(Collections.singletonList(ProblemTags.MATH));
        request.setMainSolution("#include <bits/stdc++.h>\nusing namespace std;\nint main() { int a, b; cin >> a >> b; cout << a + b; return 0; }");
        request.setSolutionLanguage("C (Clang 7.0.1)"); // Must match Judge0Language label

        // Mock repository save
        Problem savedProblem = new Problem();
        savedProblem.setId(1L);
        savedProblem.setTitle(request.getTitle());
        when(problemRepository.save(any())).thenReturn(savedProblem);

        // Mock test case service
        when(testCaseService.getTestCasesFromRequestDto(any(), any())).thenReturn(Collections.emptyList());

        // Call service
        problemService.addProblem(request);

        // Verify save called exactly once
        verify(problemRepository, times(1)).save(any());

        // Verify test case service called
        verify(testCaseService, times(1)).getTestCasesFromRequestDto(any(), any());
    }
    @Test
    void testMappingRequestDtoToProblem() {
        ProblemRequestDto request = new ProblemRequestDto();
        request.setTitle("Test Problem");
        request.setInputFormat("input");
        request.setOutputFormat("output");
        request.setStatement("statement");
        request.setNotes("notes");
        request.setMemoryLimit(128);
        request.setTimeLimit(2);
        request.setRate(ProblemRate.RATE_200);
        request.setTags(Collections.singletonList(ProblemTags.MATH));
        request.setMainSolution("solution");
        request.setSolutionLanguage("C++ (GCC 9.2.0)");

        Problem problem = problemService.mappingRequestDtoToProblem(request);

        assertEquals("Test Problem", problem.getTitle());
        assertEquals("input", problem.getInputFormat());
        assertEquals("output", problem.getOutputFormat());
        assertEquals("statement", problem.getStatement());
        assertEquals("notes", problem.getNotes());
        assertEquals(128, problem.getMemoryLimit());
        assertEquals(2, problem.getTimeLimit());
        assertEquals(ProblemRate.RATE_200, problem.getRate());
        assertEquals(Collections.singletonList(ProblemTags.MATH), problem.getTags());
        assertEquals("solution", problem.getMainSolution());
        assertEquals(Judge0Language.CPP_GCC_9_2_0, problem.getJudge0Language());
    }
    @Test
    void testMappingProblemToResponsDto() {
        Problem problem = new Problem();
        problem.setId(1L);
        problem.setTitle("Test Problem");
        problem.setMemoryLimit(64);
        problem.setTimeLimit(1);
        problem.setRate(ProblemRate.RATE_200);
        problem.setTags(Collections.singletonList(ProblemTags.MATH));

        ProblemResponsDto dto = problemService.mappingProblemToResponsDto(problem);

        assertEquals(1L, dto.getId());
        assertEquals("Test Problem", dto.getTitle());
        assertEquals(64, dto.getMemoryLimit());
        assertEquals(1, dto.getTimeLimit());
        assertEquals(ProblemRate.RATE_200, dto.getRate());
        assertEquals(Collections.singletonList(ProblemTags.MATH), dto.getTags());
    }
}
