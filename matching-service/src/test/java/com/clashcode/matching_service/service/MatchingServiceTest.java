package com.clashcode.matching_service.service;

import com.clashcode.matching_service.dto.MatchingRequestDto;
import com.clashcode.matching_service.dto.UserMatchingDto;
import com.clashcode.matching_service.main_backend.MainBackendClient;
import com.clashcode.matching_service.main_backend.dto.MatchCreationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private MainBackendClient mainBackendClient;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @InjectMocks
    private MatchingService matchingService;

    private static final String KEY_FOR_Z_SET = "matching:queue";

    @BeforeEach
    void setUp() {
        // Remove unnecessary stubbings from here - only stub when needed in specific tests
    }

    @Test
    void testAddUserToMatchingService_ShouldAddUserToRedis() {
        // Arrange
        Long userId = 1L;
        int userRating = 1500;
        MatchingRequestDto dto = new MatchingRequestDto(userId, userRating);

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        // Act
        matchingService.addUserToMatchingService(dto);

        // Assert
        verify(zSetOperations).add(KEY_FOR_Z_SET, "user:1", 1500.0);
        verify(hashOperations).put("matching:user:1", "rate", "1500");
        verify(hashOperations).put(eq("matching:user:1"), eq("joinedAt"), anyString());
    }

    @Test
    void testRemoveUserFromMatchingService_ShouldRemoveUserFromRedis() {
        // Arrange
        Long userId = 1L;

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        // Act
        matchingService.removeUserFromMatchingService(userId);

        // Assert
        verify(zSetOperations).remove(KEY_FOR_Z_SET, "user:1");
        verify(redisTemplate).delete("matching:user:1");
    }



    @Test
    void testSearchForOpponent_WhenNoCandidatesFound_ShouldNotMatch() {
        // Arrange
        Long userId = 1L;
        long currentTime = System.currentTimeMillis();

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(hashOperations.get("matching:user:1", "rate")).thenReturn("1500");
        when(hashOperations.get("matching:user:1", "joinedAt")).thenReturn(String.valueOf(currentTime));
        when(zSetOperations.rangeByScore(anyString(), anyDouble(), anyDouble())).thenReturn(new HashSet<>());

        // Act
        matchingService.searchForOpponent(userId);

        // Assert
        verify(mainBackendClient, never()).MatchingTwoPlayers(any());
    }

    @Test
    void testSearchForOpponent_WhenOnlySelfInQueue_ShouldNotMatch() {
        // Arrange
        Long userId = 1L;
        long currentTime = System.currentTimeMillis();

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(hashOperations.get("matching:user:1", "rate")).thenReturn("1500");
        when(hashOperations.get("matching:user:1", "joinedAt")).thenReturn(String.valueOf(currentTime));

        Set<String> candidates = new HashSet<>();
        candidates.add("user:1");
        when(zSetOperations.rangeByScore(anyString(), anyDouble(), anyDouble())).thenReturn(candidates);

        // Act
        matchingService.searchForOpponent(userId);

        // Assert
        verify(mainBackendClient, never()).MatchingTwoPlayers(any());
    }

    @Test
    void testSearchForOpponent_WhenOpponentFound_ShouldMatchUsers() {
        // Arrange
        Long userId = 1L;
        Long opponentId = 2L;
        long currentTime = System.currentTimeMillis();

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(hashOperations.get("matching:user:1", "rate")).thenReturn("1500");
        when(hashOperations.get("matching:user:1", "joinedAt")).thenReturn(String.valueOf(currentTime));

        Set<String> candidates = new HashSet<>();
        candidates.add("user:1");
        candidates.add("user:2");
        when(zSetOperations.rangeByScore(anyString(), anyDouble(), anyDouble())).thenReturn(candidates);

        // Act
        matchingService.searchForOpponent(userId);

        // Assert
        ArgumentCaptor<MatchCreationDto> captor = ArgumentCaptor.forClass(MatchCreationDto.class);
        verify(mainBackendClient).MatchingTwoPlayers(captor.capture());

        MatchCreationDto capturedDto = captor.getValue();
        assertEquals(userId, capturedDto.getPlayerIdA());
        assertEquals(opponentId, capturedDto.getPlayerIdB());
    }

    @Test
    void testMatchTwoUsers_ShouldRemoveBothUsersAndCallBackend() {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        // Act
        matchingService.matchTwoUsers(userId1, userId2);

        // Assert
        verify(zSetOperations).remove(KEY_FOR_Z_SET, "user:1");
        verify(zSetOperations).remove(KEY_FOR_Z_SET, "user:2");
        verify(redisTemplate).delete("matching:user:1");
        verify(redisTemplate).delete("matching:user:2");

        ArgumentCaptor<MatchCreationDto> captor = ArgumentCaptor.forClass(MatchCreationDto.class);
        verify(mainBackendClient).MatchingTwoPlayers(captor.capture());

        MatchCreationDto dto = captor.getValue();
        assertEquals(userId1, dto.getPlayerIdA());
        assertEquals(userId2, dto.getPlayerIdB());
    }

    @Test
    void testGetUserData_WhenUserExists_ShouldReturnUserMatchingDto() {
        // Arrange
        Long userId = 1L;
        long joinedAt = System.currentTimeMillis();

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get("matching:user:1", "rate")).thenReturn("1500");
        when(hashOperations.get("matching:user:1", "joinedAt")).thenReturn(String.valueOf(joinedAt));

        // Act
        UserMatchingDto result = matchingService.getUserData(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(1500, result.getUserRating());
        assertEquals(joinedAt, result.getStartTime());
    }

    @Test
    void testGetUserData_WhenRatingMissing_ShouldReturnNull() {
        // Arrange
        Long userId = 1L;

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get("matching:user:1", "rate")).thenReturn(null);
        when(hashOperations.get("matching:user:1", "joinedAt")).thenReturn("123456789");

        // Act
        UserMatchingDto result = matchingService.getUserData(userId);

        // Assert
        assertNull(result);
    }

    @Test
    void testGetUserData_WhenJoinedAtMissing_ShouldReturnNull() {
        // Arrange
        Long userId = 1L;

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get("matching:user:1", "rate")).thenReturn("1500");
        when(hashOperations.get("matching:user:1", "joinedAt")).thenReturn(null);

        // Act
        UserMatchingDto result = matchingService.getUserData(userId);

        // Assert
        assertNull(result);
    }

    @Test
    void testGetZSetMember_ShouldFormatCorrectly() {
        // Act
        String result = matchingService.getZSetMember(123L);

        // Assert
        assertEquals("user:123", result);
    }

    @Test
    void testGetHashKey_ShouldFormatCorrectly() {
        // Act
        String result = matchingService.getHashKey(123L);

        // Assert
        assertEquals("matching:user:123", result);
    }

    @Test
    void testGetUserIdFromString_ShouldParseCorrectly() {
        // Act
        Long result = matchingService.getUserIdFromString("user:123");

        // Assert
        assertEquals(123L, result);
    }

    @Test
    void testSearchForOpponent_WithMultipleCandidates_ShouldMatchFirstValidCandidate() {
        // Arrange
        Long userId = 1L;
        long currentTime = System.currentTimeMillis();

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(hashOperations.get("matching:user:1", "rate")).thenReturn("1500");
        when(hashOperations.get("matching:user:1", "joinedAt")).thenReturn(String.valueOf(currentTime));

        Set<String> candidates = new HashSet<>();
        candidates.add("user:1");
        candidates.add("user:2");
        candidates.add("user:3");
        when(zSetOperations.rangeByScore(anyString(), anyDouble(), anyDouble())).thenReturn(candidates);

        // Act
        matchingService.searchForOpponent(userId);

        // Assert
        verify(mainBackendClient, times(1)).MatchingTwoPlayers(any());
    }

    @Test
    void testAddUserToMatchingService_WithDifferentRatings() {
        // Arrange
        MatchingRequestDto dto1 = new MatchingRequestDto(1L, 1000);
        MatchingRequestDto dto2 = new MatchingRequestDto(2L, 2000);

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        // Act
        matchingService.addUserToMatchingService(dto1);
        matchingService.addUserToMatchingService(dto2);

        // Assert
        verify(zSetOperations).add(KEY_FOR_Z_SET, "user:1", 1000.0);
        verify(zSetOperations).add(KEY_FOR_Z_SET, "user:2", 2000.0);
        verify(hashOperations).put("matching:user:1", "rate", "1000");
        verify(hashOperations).put("matching:user:2", "rate", "2000");
    }


    @Test
    void testSearchForOpponent_CandidatesNull_ShouldNotMatch() {
        // Arrange
        Long userId = 1L;
        long currentTime = System.currentTimeMillis();

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(hashOperations.get("matching:user:1", "rate")).thenReturn("1500");
        when(hashOperations.get("matching:user:1", "joinedAt")).thenReturn(String.valueOf(currentTime));
        when(zSetOperations.rangeByScore(anyString(), anyDouble(), anyDouble())).thenReturn(null);

        // Act
        matchingService.searchForOpponent(userId);

        // Assert
        verify(mainBackendClient, never()).MatchingTwoPlayers(any());
    }
    @Test
    void testSearchForOpponent_UserDataNull_ShouldNotMatch() {
        // Arrange
        Long userId = 1L;

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        // Simulate missing user data in Redis
        when(hashOperations.get("matching:user:1", "rate")).thenReturn(null);
        when(hashOperations.get("matching:user:1", "joinedAt")).thenReturn(null);

        // Act
        matchingService.searchForOpponent(userId);

        // Assert
        // Ensure no matching attempt is made
        verify(mainBackendClient, never()).MatchingTwoPlayers(any());
    }


}