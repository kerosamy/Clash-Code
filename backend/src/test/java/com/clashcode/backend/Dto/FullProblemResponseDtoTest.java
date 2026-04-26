package com.clashcode.backend.Dto;

import com.clashcode.backend.dto.FullProblemResponseDto;
import com.clashcode.backend.dto.TestCaseResponseDto;
import com.clashcode.backend.enums.LanguageVersion;
import com.clashcode.backend.enums.ProblemTags;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


class FullProblemResponseDtoTest {

    @Test
    @DisplayName("Should create FullProblemResponseDto using builder")
    void shouldCreateDtoUsingBuilder() {
        FullProblemResponseDto dto = FullProblemResponseDto.builder()
                .id(1L)
                .submissionsCount(150L)
                .title("Two Sum")
                .inputFormat("Integer array and target")
                .outputFormat("Indices of two numbers")
                .statement("Find two numbers that add up to target")
                .notes("Hash map approach is optimal")
                .timeLimit(2000)
                .memoryLimit(256)
                .rate(1200)
                .author("admin")
                .solutionCode("public int[] twoSum() {}")
                .solutionLanguage(LanguageVersion.PYTHON_3_8)
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getSubmissionsCount()).isEqualTo(150L);
        assertThat(dto.getTitle()).isEqualTo("Two Sum");
        assertThat(dto.getInputFormat()).isEqualTo("Integer array and target");
        assertThat(dto.getOutputFormat()).isEqualTo("Indices of two numbers");
        assertThat(dto.getStatement()).isEqualTo("Find two numbers that add up to target");
        assertThat(dto.getNotes()).isEqualTo("Hash map approach is optimal");
        assertThat(dto.getTimeLimit()).isEqualTo(2000);
        assertThat(dto.getMemoryLimit()).isEqualTo(256);
        assertThat(dto.getRate()).isEqualTo(1200);
        assertThat(dto.getAuthor()).isEqualTo("admin");
        assertThat(dto.getSolutionCode()).isEqualTo("public int[] twoSum() {}");
        assertThat(dto.getSolutionLanguage()).isEqualTo(LanguageVersion.PYTHON_3_8);
    }

    @Test
    @DisplayName("Should initialize tags as empty list by default")
    void shouldInitializeTagsAsEmptyList() {
        FullProblemResponseDto dto = FullProblemResponseDto.builder().build();

        assertThat(dto.getTags()).isNotNull();
    }

    @Test
    @DisplayName("Should initialize visibleTestCases as empty list by default")
    void shouldInitializeVisibleTestCasesAsEmptyList() {
        FullProblemResponseDto dto = FullProblemResponseDto.builder().build();

        assertThat(dto.getVisibleTestCases()).isNotNull();
    }

    @Test
    @DisplayName("Should handle tags list")
    void shouldHandleTagsList() {
        List<ProblemTags> tags = Arrays.asList(ProblemTags.DP, ProblemTags.MATH);

        FullProblemResponseDto dto = FullProblemResponseDto.builder()
                .tags(tags)
                .build();

        assertThat(dto.getTags()).hasSize(2);
        assertThat(dto.getTags()).contains(ProblemTags.DP, ProblemTags.MATH);
    }

    @Test
    @DisplayName("Should handle visible test cases list")
    void shouldHandleVisibleTestCasesList() {
        TestCaseResponseDto testCase1 = new TestCaseResponseDto();
        TestCaseResponseDto testCase2 = new TestCaseResponseDto();
        List<TestCaseResponseDto> testCases = Arrays.asList(testCase1, testCase2);

        FullProblemResponseDto dto = FullProblemResponseDto.builder()
                .visibleTestCases(testCases)
                .build();

        assertThat(dto.getVisibleTestCases()).hasSize(2);
    }

    @Test
    @DisplayName("Should support setter methods")
    void shouldSupportSetterMethods() {
        FullProblemResponseDto dto = new FullProblemResponseDto();
        dto.setId(5L);
        dto.setTitle("Binary Search");
        dto.setRate(800);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getTitle()).isEqualTo("Binary Search");
        assertThat(dto.getRate()).isEqualTo(800);
    }

    @Test
    @DisplayName("Should create using no-args constructor")
    void shouldCreateUsingNoArgsConstructor() {
        FullProblemResponseDto dto = new FullProblemResponseDto();

        assertThat(dto).isNotNull();
        assertThat(dto.getTags()).isEmpty();
        assertThat(dto.getVisibleTestCases()).isEmpty();
    }

    @Test
    @DisplayName("Should create using all-args constructor")
    void shouldCreateUsingAllArgsConstructor() {
        List<ProblemTags> tags = List.of(ProblemTags.DP);
        List<TestCaseResponseDto> testCases = new ArrayList<>();

        FullProblemResponseDto dto = new FullProblemResponseDto(
                1L, 100L, "Title", "Input", "Output", "Statement",
                "Notes", 1000, 128, 1500, "Author", "Code",
                LanguageVersion.CPP_GCC_9_2, tags, testCases
        );

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getSubmissionsCount()).isEqualTo(100L);
        assertThat(dto.getTags()).hasSize(1);
    }
}