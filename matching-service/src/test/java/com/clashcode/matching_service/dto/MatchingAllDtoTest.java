package com.clashcode.matching_service.dto;

import com.clashcode.matching_service.main_backend.dto.MatchCreationDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MatchingAllDtoTest {

    @Test
    void MatchingRequestDtoTestConstructorAndGetters() {
        // Arrange & Act
        MatchingRequestDto dto = new MatchingRequestDto(123L, 1500);

        // Assert
        assertEquals(123L, dto.getUserId());
        assertEquals(1500, dto.getUserRating());
    }

    @Test
    void MatchingRequestDtoTestSetters() {
        // Arrange
        MatchingRequestDto dto = new MatchingRequestDto(1L, 1000);

        // Act
        dto.setUserId(456L);
        dto.setUserRating(2000);

        // Assert
        assertEquals(456L, dto.getUserId());
        assertEquals(2000, dto.getUserRating());
    }

    @Test
    void MatchingRequestDtoTestWithDifferentValues() {
        // Test with various values to ensure coverage
        MatchingRequestDto dto1 = new MatchingRequestDto(0L, 0);
        assertEquals(0L, dto1.getUserId());
        assertEquals(0, dto1.getUserRating());

        MatchingRequestDto dto2 = new MatchingRequestDto(999999L, 5000);
        assertEquals(999999L, dto2.getUserId());
        assertEquals(5000, dto2.getUserRating());
    }

    @Test
    void UserMatchingDtoTestBuilder() {
        // Arrange & Act
        UserMatchingDto dto = UserMatchingDto.builder()
                .userId(123L)
                .startTime(1000000L)
                .userRating(1500)
                .build();

        // Assert
        assertEquals(123L, dto.getUserId());
        assertEquals(1000000L, dto.getStartTime());
        assertEquals(1500, dto.getUserRating());
    }

    @Test
    void UserMatchingDtoTestAllArgsConstructor() {
        // Arrange & Act
        UserMatchingDto dto = new UserMatchingDto(456L, 2000000L, 1800);

        // Assert
        assertEquals(456L, dto.getUserId());
        assertEquals(2000000L, dto.getStartTime());
        assertEquals(1800, dto.getUserRating());
    }

    @Test
    void UserMatchingDtoTestBuilderToString() {
        // This covers the UserMatchingDtoBuilder.toString() method
        UserMatchingDto.UserMatchingDtoBuilder builder = UserMatchingDto.builder()
                .userId(123L)
                .startTime(1000L)
                .userRating(1500);

        String builderString = builder.toString();

        assertNotNull(builderString);
        assertTrue(builderString.contains("UserMatchingDto.UserMatchingDtoBuilder"));
        assertTrue(builderString.contains("userId") || builderString.contains("123"));
    }

    @Test
    void UserMatchingDtoTestSetters() {
        // Arrange
        UserMatchingDto dto = UserMatchingDto.builder()
                .userId(1L)
                .startTime(1000L)
                .userRating(1000)
                .build();

        // Act
        dto.setUserId(999L);
        dto.setStartTime(5000L);
        dto.setUserRating(2500);

        // Assert
        assertEquals(999L, dto.getUserId());
        assertEquals(5000L, dto.getStartTime());
        assertEquals(2500, dto.getUserRating());
    }

    @Test
    void UserMatchingDtoTestBuilderWithDifferentValues() {
        UserMatchingDto dto = UserMatchingDto.builder()
                .userId(0L)
                .startTime(0L)
                .userRating(0)
                .build();

        assertEquals(0L, dto.getUserId());
        assertEquals(0L, dto.getStartTime());
        assertEquals(0, dto.getUserRating());
    }

    @Test
    void MatchCreationDtoTestConstructorAndGetters() {
        // Arrange & Act
        MatchCreationDto dto = new MatchCreationDto(123L, 456L);

        // Assert
        assertEquals(123L, dto.getPlayerIdA());
        assertEquals(456L, dto.getPlayerIdB());
    }

    @Test
    void MatchCreationDtoTestSetters() {
        // Arrange
        MatchCreationDto dto = new MatchCreationDto(1L, 2L);

        // Act
        dto.setPlayerIdA(999L);
        dto.setPlayerIdB(888L);

        // Assert
        assertEquals(999L, dto.getPlayerIdA());
        assertEquals(888L, dto.getPlayerIdB());
    }

    @Test
    void MatchCreationDtoTestWithDifferentValues() {
        MatchCreationDto dto1 = new MatchCreationDto(0L, 0L);
        assertEquals(0L, dto1.getPlayerIdA());
        assertEquals(0L, dto1.getPlayerIdB());

        MatchCreationDto dto2 = new MatchCreationDto(Long.MAX_VALUE, Long.MIN_VALUE);
        assertEquals(Long.MAX_VALUE, dto2.getPlayerIdA());
        assertEquals(Long.MIN_VALUE, dto2.getPlayerIdB());
    }

    @Test
    void MatchCreationDtoTestSetterChaining() {
        // Arrange
        MatchCreationDto dto = new MatchCreationDto(1L, 2L);

        // Act
        dto.setPlayerIdA(100L);
        dto.setPlayerIdB(200L);

        // Assert
        assertEquals(100L, dto.getPlayerIdA());
        assertEquals(200L, dto.getPlayerIdB());
    }
}
