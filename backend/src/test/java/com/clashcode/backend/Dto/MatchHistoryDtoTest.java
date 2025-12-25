package com.clashcode.backend.Dto;

import com.clashcode.backend.dto.MatchHistoryDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MatchHistoryDtoTest {

    @Test
    void testBuilderAndGetters() {
        LocalDateTime now = LocalDateTime.now();

        MatchHistoryDto dto = MatchHistoryDto.builder()
                .matchId(123L)
                .time(now)
                .opponent("Alice")
                .problem("Graph Problem")
                .rank(1)
                .rateChange(20)
                .newRating(1500)
                .isRated(true)
                .build();

        assertEquals(123L, dto.getMatchId());
        assertEquals(now, dto.getTime());
        assertEquals("Alice", dto.getOpponent());
        assertEquals("Graph Problem", dto.getProblem());
        assertEquals(1, dto.getRank());
        assertEquals(20, dto.getRateChange());
        assertEquals(1500, dto.getNewRating());
        assertTrue(dto.isRated());
    }

    @Test
    void testSetters() {
        MatchHistoryDto dto = new MatchHistoryDto();
        LocalDateTime now = LocalDateTime.now();

        dto.setMatchId(456L);
        dto.setTime(now);
        dto.setOpponent("Bob");
        dto.setProblem("DP Problem");
        dto.setRank(2);
        dto.setRateChange(-5);
        dto.setNewRating(1495);
        dto.setRated(false);

        assertEquals(456L, dto.getMatchId());
        assertEquals(now, dto.getTime());
        assertEquals("Bob", dto.getOpponent());
        assertEquals("DP Problem", dto.getProblem());
        assertEquals(2, dto.getRank());
        assertEquals(-5, dto.getRateChange());
        assertEquals(1495, dto.getNewRating());
        assertFalse(dto.isRated());
    }

}
