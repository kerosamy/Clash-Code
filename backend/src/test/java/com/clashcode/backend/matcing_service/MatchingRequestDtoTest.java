package com.clashcode.backend.matcing_service;

import com.clashcode.backend.matching.dto.MatchingRequestDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchingRequestDtoTest {

    @Test
    void allArgsConstructor_SetsFieldsCorrectly() {
        MatchingRequestDto dto = new MatchingRequestDto(42L, 1500);

        assertEquals(42L, dto.getUserId());
        assertEquals(1500, dto.getUserRating());
    }

    @Test
    void setters_UpdateFieldsCorrectly() {
        MatchingRequestDto dto = new MatchingRequestDto(1L, 1000);

        dto.setUserId(99L);
        dto.setUserRating(2000);

        assertEquals(99L, dto.getUserId());
        assertEquals(2000, dto.getUserRating());
    }

    @Test
    void getters_ReturnCurrentValues() {
        MatchingRequestDto dto = new MatchingRequestDto(5L, 1200);

        long userId = dto.getUserId();
        int rating = dto.getUserRating();

        assertEquals(5L, userId);
        assertEquals(1200, rating);
    }
}
