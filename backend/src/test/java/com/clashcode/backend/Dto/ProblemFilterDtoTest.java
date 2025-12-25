package com.clashcode.backend.Dto;

import com.clashcode.backend.dto.ProblemFilterDto;
import com.clashcode.backend.enums.ProblemTags;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class ProblemFilterDtoTest {

    @Test
    @DisplayName("Should create ProblemFilterDto with all fields")
    void shouldCreateDtoWithAllFields() {
        ProblemFilterDto dto = new ProblemFilterDto();
        List<ProblemTags> tags = Arrays.asList(ProblemTags.BFS, ProblemTags.SORTING);

        dto.setTags(tags);
        dto.setMinRate(800);
        dto.setMaxRate(1600);

        assertThat(dto.getTags()).hasSize(2);
        assertThat(dto.getTags()).contains(ProblemTags.BFS, ProblemTags.SORTING);
        assertThat(dto.getMinRate()).isEqualTo(800);
        assertThat(dto.getMaxRate()).isEqualTo(1600);
    }

    @Test
    @DisplayName("Should handle null values")
    void shouldHandleNullValues() {
        ProblemFilterDto dto = new ProblemFilterDto();

        assertThat(dto.getTags()).isNull();
        assertThat(dto.getMinRate()).isNull();
        assertThat(dto.getMaxRate()).isNull();
    }

    @Test
    @DisplayName("Should handle empty tags list")
    void shouldHandleEmptyTagsList() {
        ProblemFilterDto dto = new ProblemFilterDto();
        dto.setTags(new ArrayList<>());

        assertThat(dto.getTags()).isEmpty();
    }

    @Test
    @DisplayName("Should handle only minRate filter")
    void shouldHandleOnlyMinRateFilter() {
        ProblemFilterDto dto = new ProblemFilterDto();
        dto.setMinRate(1000);

        assertThat(dto.getMinRate()).isEqualTo(1000);
        assertThat(dto.getMaxRate()).isNull();
        assertThat(dto.getTags()).isNull();
    }

    @Test
    @DisplayName("Should handle only maxRate filter")
    void shouldHandleOnlyMaxRateFilter() {
        ProblemFilterDto dto = new ProblemFilterDto();
        dto.setMaxRate(1500);

        assertThat(dto.getMaxRate()).isEqualTo(1500);
        assertThat(dto.getMinRate()).isNull();
        assertThat(dto.getTags()).isNull();
    }
}