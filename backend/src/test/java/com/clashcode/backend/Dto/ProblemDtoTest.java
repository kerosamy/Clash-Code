package com.clashcode.backend.Dto;

import com.clashcode.backend.dto.ProblemFilterDto;
import com.clashcode.backend.dto.ProblemListDto;
import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.ProblemResponseDto;
import com.clashcode.backend.enums.ProblemTags;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ProblemDtoTest {

    @Test
    void problemFilterDto_gettersAndSetters() {
        ProblemFilterDto dto = new ProblemFilterDto();
        List<ProblemTags> tags = List.of(ProblemTags.MATH, ProblemTags.DP);

        dto.setTags(tags);
        dto.setMinRate(100);
        dto.setMaxRate(500);

        assertEquals(tags, dto.getTags());
        assertEquals(100, dto.getMinRate());
        assertEquals(500, dto.getMaxRate());
    }

    @Test
    void problemListDto_builderAndDefaults() {
        ProblemListDto dto = ProblemListDto.builder()
                .id(1L)
                .title("Sample Problem")
                .submissionsCount(5L)
                .tags(List.of(ProblemTags.BRUTE_FORCE))
                .rate(1200)
                .author("authorName")
                .build();

        assertEquals(1L, dto.getId());
        assertEquals("Sample Problem", dto.getTitle());
        assertEquals(5L, dto.getSubmissionsCount());
        assertEquals(List.of(ProblemTags.BRUTE_FORCE), dto.getTags());
        assertEquals(1200, dto.getRate());
        assertEquals("authorName", dto.getAuthor());
    }

    @Test
    void problemRequestDto_builderAndDefaults() {
        ProblemRequestDto dto = ProblemRequestDto.builder()
                .title("Title")
                .inputFormat("input")
                .outputFormat("output")
                .statement("statement")
                .notes("notes")
                .mainSolution("solution")
                .solutionLanguage("java")
                .timeLimit(1000)
                .memoryLimit(256)
                .rate(500)
                .author("author")
                .tags(List.of(ProblemTags.DP))
                .visibleFlags(List.of(true, false))
                .build();

        assertEquals("Title", dto.getTitle());
        assertEquals("input", dto.getInputFormat());
        assertEquals("output", dto.getOutputFormat());
        assertEquals("statement", dto.getStatement());
        assertEquals("notes", dto.getNotes());
        assertEquals("solution", dto.getMainSolution());
        assertEquals("java", dto.getSolutionLanguage());
        assertEquals(1000, dto.getTimeLimit());
        assertEquals(256, dto.getMemoryLimit());
        assertEquals(500, dto.getRate());
        assertEquals("author", dto.getAuthor());
        assertEquals(List.of(ProblemTags.DP), dto.getTags());
        assertEquals(List.of(true, false), dto.getVisibleFlags());
    }

    @Test
    void problemResponseDto_builderAndDefaults() {
        ProblemResponseDto dto = ProblemResponseDto.builder()
                .id(1L)
                .title("Title")
                .inputFormat("input")
                .outputFormat("output")
                .statement("statement")
                .notes("notes")
                .timeLimit(1000)
                .memoryLimit(256)
                .rate(1500)
                .author("author")
                .tags(List.of(ProblemTags.BRUTE_FORCE))
                .visibleTestCases(new ArrayList<>())
                .build();

        assertEquals(1L, dto.getId());
        assertEquals("Title", dto.getTitle());
        assertEquals("input", dto.getInputFormat());
        assertEquals("output", dto.getOutputFormat());
        assertEquals("statement", dto.getStatement());
        assertEquals("notes", dto.getNotes());
        assertEquals(1000, dto.getTimeLimit());
        assertEquals(256, dto.getMemoryLimit());
        assertEquals(1500, dto.getRate());
        assertEquals("author", dto.getAuthor());
        assertEquals(List.of(ProblemTags.BRUTE_FORCE), dto.getTags());
        assertNotNull(dto.getVisibleTestCases()); // default empty list
        assertTrue(dto.getVisibleTestCases().isEmpty());
    }
}
