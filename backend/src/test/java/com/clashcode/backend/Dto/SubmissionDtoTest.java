package com.clashcode.backend.Dto;

import com.clashcode.backend.dto.SubmissionDetailsDto;
import com.clashcode.backend.dto.SubmissionListDto;
import com.clashcode.backend.dto.SubmissionLogEntryDto;
import com.clashcode.backend.dto.SubmissionRequestDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubmissionDtoTest {

    @Test
    void submissionDetailsDto_builderAndSetters() {
        SubmissionDetailsDto dto = SubmissionDetailsDto.builder()
                .submissionLang("Java")
                .submissionCode("System.out.println(\"Hello\");")
                .problemTitle("Sample Problem")
                .username("user1")
                .submissionStatus("PENDING")
                .build();

        assertEquals("Java", dto.getSubmissionLang());
        assertEquals("System.out.println(\"Hello\");", dto.getSubmissionCode());
        assertEquals("Sample Problem", dto.getProblemTitle());
        assertEquals("user1", dto.getUsername());
        assertEquals("PENDING", dto.getSubmissionStatus());

        // Test setters
        dto.setSubmissionStatus("ACCEPTED");
        assertEquals("ACCEPTED", dto.getSubmissionStatus());
    }

    @Test
    void submissionListDto_builderAndSetters() {
        SubmissionListDto dto = SubmissionListDto.builder()
                .submissionId(1L)
                .problemId(101L)
                .timeTaken(500)
                .memoryTaken(128)
                .numberOfPassedTestCases(5)
                .numberOfTotalTestCases(10)
                .numberOfCurrentTestCase(3)
                .submittedAt("2025-12-11T10:00:00")
                .problemTitle("Problem A")
                .submissionStatus("ACCEPTED")
                .build();

        assertEquals(1L, dto.getSubmissionId());
        assertEquals(101L, dto.getProblemId());
        assertEquals(500, dto.getTimeTaken());
        assertEquals(128, dto.getMemoryTaken());
        assertEquals(5, dto.getNumberOfPassedTestCases());
        assertEquals(10, dto.getNumberOfTotalTestCases());
        assertEquals(3, dto.getNumberOfCurrentTestCase());
        assertEquals("2025-12-11T10:00:00", dto.getSubmittedAt());
        assertEquals("Problem A", dto.getProblemTitle());
        assertEquals("ACCEPTED", dto.getSubmissionStatus());

        // Test setters
        dto.setNumberOfCurrentTestCase(4);
        assertEquals(4, dto.getNumberOfCurrentTestCase());
    }

    @Test
    void submissionLogEntryDto_builderAndGetters() {
        SubmissionLogEntryDto dto = SubmissionLogEntryDto.builder()
                .submittedAt("2025-12-11T12:00:00")
                .status("REJECTED")
                .numberOfPassedTestCases(2)
                .numberOfTotalTestCases(10)
                .build();

        assertEquals("2025-12-11T12:00:00", dto.getSubmittedAt());
        assertEquals("REJECTED", dto.getStatus());
        assertEquals(2, dto.getNumberOfPassedTestCases());
        assertEquals(10, dto.getNumberOfTotalTestCases());
    }

    @Test
    void submissionRequestDto_builderAndSetters() {
        SubmissionRequestDto dto = SubmissionRequestDto.builder()
                .problemId(101L)
                .code("print('Hello')")
                .codeLanguage("Python")
                .matchId(10L)
                .build();

        assertEquals(101L, dto.getProblemId());
        assertEquals("print('Hello')", dto.getCode());
        assertEquals("Python", dto.getCodeLanguage());
        assertEquals(10L, dto.getMatchId());

        // Test setters
        dto.setCode("print('World')");
        assertEquals("print('World')", dto.getCode());
    }
}
