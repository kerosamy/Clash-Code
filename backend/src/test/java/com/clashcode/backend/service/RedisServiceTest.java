package com.clashcode.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisService redisService;

    @Test
    void addUserToRedis_ShouldSetUserStatusWithCorrectTTL() {
        // Arrange
        Long userId = 123L;
        String status = "online";
        String expectedKey = "online:user:123";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Act
        redisService.addUserToRedis(userId, status);

        // Assert
        verify(valueOperations).set(
                eq(expectedKey),
                eq(status),
                eq(Duration.ofSeconds(45))
        );
    }

    @Test
    void addUserToRedis_WithInMatchStatus_ShouldSetCorrectly() {
        // Arrange
        Long userId = 456L;
        String status = "in-match";
        String expectedKey = "online:user:456";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Act
        redisService.addUserToRedis(userId, status);

        // Assert
        verify(valueOperations).set(
                eq(expectedKey),
                eq(status),
                eq(Duration.ofSeconds(45))
        );
    }

    @Test
    void searchUserFromRedis_WhenUserExists_ShouldReturnTrue() {
        // Arrange
        Long userId = 123L;
        String expectedKey = "online:user:123";
        when(redisTemplate.hasKey(expectedKey)).thenReturn(true);

        // Act
        Boolean result = redisService.searchUserFromRedis(userId);

        // Assert
        assertTrue(result);
        verify(redisTemplate).hasKey(expectedKey);
    }

    @Test
    void searchUserFromRedis_WhenUserDoesNotExist_ShouldReturnFalse() {
        // Arrange
        Long userId = 123L;
        String expectedKey = "online:user:123";
        when(redisTemplate.hasKey(expectedKey)).thenReturn(false);

        // Act
        Boolean result = redisService.searchUserFromRedis(userId);

        // Assert
        assertFalse(result);
        verify(redisTemplate).hasKey(expectedKey);
    }

    @Test
    void searchUserFromRedis_WhenKeyCheckReturnsNull_ShouldReturnFalse() {
        // Arrange
        Long userId = 123L;
        String expectedKey = "online:user:123";
        when(redisTemplate.hasKey(expectedKey)).thenReturn(null);

        // Act
        Boolean result = redisService.searchUserFromRedis(userId);

        // Assert
        assertFalse(result);
        verify(redisTemplate).hasKey(expectedKey);
    }

    @Test
    void getUserStatus_WhenUserIsOnline_ShouldReturnOnlineStatus() {
        // Arrange
        Long userId = 123L;
        String expectedKey = "online:user:123";
        String expectedStatus = "online";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(expectedKey)).thenReturn(expectedStatus);

        // Act
        String result = redisService.getUserStatus(userId);

        // Assert
        assertEquals(expectedStatus, result);
        verify(valueOperations).get(expectedKey);
    }

    @Test
    void getUserStatus_WhenUserIsInMatch_ShouldReturnInMatchStatus() {
        // Arrange
        Long userId = 456L;
        String expectedKey = "online:user:456";
        String expectedStatus = "in-match";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(expectedKey)).thenReturn(expectedStatus);

        // Act
        String result = redisService.getUserStatus(userId);

        // Assert
        assertEquals(expectedStatus, result);
        verify(valueOperations).get(expectedKey);
    }

    @Test
    void getUserStatus_WhenUserNotFound_ShouldReturnNull() {
        // Arrange
        Long userId = 789L;
        String expectedKey = "online:user:789";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(expectedKey)).thenReturn(null);

        // Act
        String result = redisService.getUserStatus(userId);

        // Assert
        assertNull(result);
        verify(valueOperations).get(expectedKey);
    }

    @Test
    void addUserToRedis_WithDifferentUserIds_ShouldGenerateCorrectKeys() {
        // Arrange
        Long userId1 = 100L;
        Long userId2 = 200L;
        String status = "online";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Act
        redisService.addUserToRedis(userId1, status);
        redisService.addUserToRedis(userId2, status);

        // Assert
        verify(valueOperations).set(
                eq("online:user:100"),
                eq(status),
                any(Duration.class)
        );
        verify(valueOperations).set(
                eq("online:user:200"),
                eq(status),
                any(Duration.class)
        );
    }

    @Test
    void getUserStatus_AfterAddingUser_ShouldReturnCorrectStatus() {
        // Arrange
        Long userId = 999L;
        String expectedKey = "online:user:999";
        String status = "online";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(expectedKey)).thenReturn(status);

        // Act
        redisService.addUserToRedis(userId, status);
        String result = redisService.getUserStatus(userId);

        // Assert
        assertEquals(status, result);
        verify(valueOperations).set(eq(expectedKey), eq(status), any(Duration.class));
        verify(valueOperations).get(expectedKey);
    }
}