package com.clashcode.backend.Dto;

import com.clashcode.backend.dto.CreateMatchRequestDto;
import com.clashcode.backend.enums.GameMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;


import static org.assertj.core.api.Assertions.assertThat;

class CreateMatchRequestDtoTest {

    @Test
    @DisplayName("Should create CreateMatchRequestDto with all fields")
    void shouldCreateDtoWithAllFields() {
        CreateMatchRequestDto dto = new CreateMatchRequestDto();
        dto.setPlayer1Id(1L);
        dto.setPlayer2Id(2L);
        dto.setGameMode(GameMode.RATED);
        dto.setProblemId(100L);
        dto.setDuration(3600);

        assertThat(dto.getPlayer1Id()).isEqualTo(1L);
        assertThat(dto.getPlayer2Id()).isEqualTo(2L);
        assertThat(dto.getGameMode()).isEqualTo(GameMode.RATED);
        assertThat(dto.getProblemId()).isEqualTo(100L);
        assertThat(dto.getDuration()).isEqualTo(3600);
    }

    @Test
    @DisplayName("Should handle null values")
    void shouldHandleNullValues() {
        CreateMatchRequestDto dto = new CreateMatchRequestDto();

        assertThat(dto.getPlayer1Id()).isNull();
        assertThat(dto.getPlayer2Id()).isNull();
        assertThat(dto.getGameMode()).isNull();
        assertThat(dto.getProblemId()).isNull();
        assertThat(dto.getDuration()).isNull();
    }

}

