package com.clashcode.matching_service.service;

import com.clashcode.matching_service.dto.UserMatchingDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingWorkerTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private MatchingService matchingService;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private MatchingWorker matchingWorker;

    private static final String KEY_FOR_Z_SET = "matching:queue";

    @Test
    void testRunMatchingService_WithWaitingUsers_ShouldProcessAll() {
        // Arrange
        Set<String> waitingUsers = new LinkedHashSet<>();
        waitingUsers.add("user:1");
        waitingUsers.add("user:2");
        waitingUsers.add("user:3");

        UserMatchingDto user1 = UserMatchingDto.builder()
                .userId(1L)
                .userRating(1500)
                .startTime(System.currentTimeMillis())
                .build();

        UserMatchingDto user2 = UserMatchingDto.builder()
                .userId(2L)
                .userRating(1600)
                .startTime(System.currentTimeMillis())
                .build();

        UserMatchingDto user3 = UserMatchingDto.builder()
                .userId(3L)
                .userRating(1400)
                .startTime(System.currentTimeMillis())
                .build();

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.range(KEY_FOR_Z_SET, 0, -1)).thenReturn(waitingUsers);
        when(zSetOperations.score(KEY_FOR_Z_SET, "user:1")).thenReturn(1500.0);
        when(zSetOperations.score(KEY_FOR_Z_SET, "user:2")).thenReturn(1600.0);
        when(zSetOperations.score(KEY_FOR_Z_SET, "user:3")).thenReturn(1400.0);

        when(matchingService.getUserIdFromString("user:1")).thenReturn(1L);
        when(matchingService.getUserIdFromString("user:2")).thenReturn(2L);
        when(matchingService.getUserIdFromString("user:3")).thenReturn(3L);

        when(matchingService.getUserData(1L)).thenReturn(user1);
        when(matchingService.getUserData(2L)).thenReturn(user2);
        when(matchingService.getUserData(3L)).thenReturn(user3);

        // Act
        matchingWorker.runMatchingService();

        // Assert
        verify(matchingService).searchForOpponent(1L);
        verify(matchingService).searchForOpponent(2L);
        verify(matchingService).searchForOpponent(3L);
    }

    @Test
    void testRunMatchingService_WhenNoWaitingUsers_ShouldNotProcessAnything() {
        // Arrange
        Set<String> emptySet = new HashSet<>();

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.range(KEY_FOR_Z_SET, 0, -1)).thenReturn(emptySet);

        // Act
        matchingWorker.runMatchingService();

        // Assert
        verify(matchingService, never()).getUserIdFromString(anyString());
        verify(matchingService, never()).getUserData(anyLong());
        verify(matchingService, never()).searchForOpponent(anyLong());
    }

    @Test
    void testRunMatchingService_WhenWaitingUsersIsNull_ShouldReturnEarly() {
        // Arrange
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.range(KEY_FOR_Z_SET, 0, -1)).thenReturn(null);

        // Act
        matchingWorker.runMatchingService();

        // Assert
        verify(matchingService, never()).getUserIdFromString(anyString());
        verify(matchingService, never()).getUserData(anyLong());
        verify(matchingService, never()).searchForOpponent(anyLong());
    }

    @Test
    void testRunMatchingService_WhenUserDataIsNull_ShouldSkipThatUser() {
        // Arrange
        Set<String> waitingUsers = new LinkedHashSet<>();
        waitingUsers.add("user:1");
        waitingUsers.add("user:2");

        UserMatchingDto user2 = UserMatchingDto.builder()
                .userId(2L)
                .userRating(1600)
                .startTime(System.currentTimeMillis())
                .build();

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.range(KEY_FOR_Z_SET, 0, -1)).thenReturn(waitingUsers);
        when(zSetOperations.score(KEY_FOR_Z_SET, "user:2")).thenReturn(1600.0);

        when(matchingService.getUserIdFromString("user:1")).thenReturn(1L);
        when(matchingService.getUserIdFromString("user:2")).thenReturn(2L);

        when(matchingService.getUserData(1L)).thenReturn(null); // User 1 data is missing
        when(matchingService.getUserData(2L)).thenReturn(user2);

        // Act
        matchingWorker.runMatchingService();

        // Assert
        verify(matchingService, never()).searchForOpponent(1L); // Skipped user 1
        verify(matchingService).searchForOpponent(2L); // Processed user 2
    }

    @Test
    void testRunMatchingService_WhenUserNoLongerWaiting_ShouldSkipThatUser() {
        // Arrange
        Set<String> waitingUsers = new LinkedHashSet<>();
        waitingUsers.add("user:1");
        waitingUsers.add("user:2");

        UserMatchingDto user1 = UserMatchingDto.builder()
                .userId(1L)
                .userRating(1500)
                .startTime(System.currentTimeMillis())
                .build();

        UserMatchingDto user2 = UserMatchingDto.builder()
                .userId(2L)
                .userRating(1600)
                .startTime(System.currentTimeMillis())
                .build();

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.range(KEY_FOR_Z_SET, 0, -1)).thenReturn(waitingUsers);
        when(zSetOperations.score(KEY_FOR_Z_SET, "user:1")).thenReturn(null); // User 1 no longer waiting
        when(zSetOperations.score(KEY_FOR_Z_SET, "user:2")).thenReturn(1600.0);

        when(matchingService.getUserIdFromString("user:1")).thenReturn(1L);
        when(matchingService.getUserIdFromString("user:2")).thenReturn(2L);

        when(matchingService.getUserData(1L)).thenReturn(user1);
        when(matchingService.getUserData(2L)).thenReturn(user2);

        // Act
        matchingWorker.runMatchingService();

        // Assert
        verify(matchingService, never()).searchForOpponent(1L); // Skipped user 1
        verify(matchingService).searchForOpponent(2L); // Processed user 2
    }

    @Test
    void testRunMatchingService_WithMixedScenarios_ShouldHandleCorrectly() {
        // Arrange
        Set<String> waitingUsers = new LinkedHashSet<>();
        waitingUsers.add("user:1"); // Valid user
        waitingUsers.add("user:2"); // User data is null
        waitingUsers.add("user:3"); // No longer waiting
        waitingUsers.add("user:4"); // Valid user

        UserMatchingDto user1 = UserMatchingDto.builder()
                .userId(1L)
                .userRating(1500)
                .startTime(System.currentTimeMillis())
                .build();

        UserMatchingDto user4 = UserMatchingDto.builder()
                .userId(4L)
                .userRating(1700)
                .startTime(System.currentTimeMillis())
                .build();

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.range(KEY_FOR_Z_SET, 0, -1)).thenReturn(waitingUsers);
        when(zSetOperations.score(KEY_FOR_Z_SET, "user:1")).thenReturn(1500.0);
        when(zSetOperations.score(KEY_FOR_Z_SET, "user:3")).thenReturn(null); // No longer waiting
        when(zSetOperations.score(KEY_FOR_Z_SET, "user:4")).thenReturn(1700.0);

        when(matchingService.getUserIdFromString("user:1")).thenReturn(1L);
        when(matchingService.getUserIdFromString("user:2")).thenReturn(2L);
        when(matchingService.getUserIdFromString("user:3")).thenReturn(3L);
        when(matchingService.getUserIdFromString("user:4")).thenReturn(4L);

        when(matchingService.getUserData(1L)).thenReturn(user1);
        when(matchingService.getUserData(2L)).thenReturn(null); // User data missing
        when(matchingService.getUserData(3L)).thenReturn(UserMatchingDto.builder().userId(3L).build());
        when(matchingService.getUserData(4L)).thenReturn(user4);

        // Act
        matchingWorker.runMatchingService();

        // Assert
        verify(matchingService).searchForOpponent(1L); // Processed
        verify(matchingService, never()).searchForOpponent(2L); // Skipped - no user data
        verify(matchingService, never()).searchForOpponent(3L); // Skipped - no longer waiting
        verify(matchingService).searchForOpponent(4L); // Processed
    }

    @Test
    void testRunMatchingService_WithSingleUser_ShouldProcessCorrectly() {
        // Arrange
        Set<String> waitingUsers = new LinkedHashSet<>();
        waitingUsers.add("user:1");

        UserMatchingDto user1 = UserMatchingDto.builder()
                .userId(1L)
                .userRating(1500)
                .startTime(System.currentTimeMillis())
                .build();

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.range(KEY_FOR_Z_SET, 0, -1)).thenReturn(waitingUsers);
        when(zSetOperations.score(KEY_FOR_Z_SET, "user:1")).thenReturn(1500.0);

        when(matchingService.getUserIdFromString("user:1")).thenReturn(1L);
        when(matchingService.getUserData(1L)).thenReturn(user1);

        // Act
        matchingWorker.runMatchingService();

        // Assert
        verify(matchingService).searchForOpponent(1L);
    }

    @Test
    void testRunMatchingService_VerifiesCorrectRedisOperations() {
        // Arrange
        Set<String> waitingUsers = new LinkedHashSet<>();
        waitingUsers.add("user:1");

        UserMatchingDto user1 = UserMatchingDto.builder()
                .userId(1L)
                .userRating(1500)
                .startTime(System.currentTimeMillis())
                .build();

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.range(KEY_FOR_Z_SET, 0, -1)).thenReturn(waitingUsers);
        when(zSetOperations.score(KEY_FOR_Z_SET, "user:1")).thenReturn(1500.0);

        when(matchingService.getUserIdFromString("user:1")).thenReturn(1L);
        when(matchingService.getUserData(1L)).thenReturn(user1);

        // Act
        matchingWorker.runMatchingService();

        // Assert
        verify(zSetOperations).range(KEY_FOR_Z_SET, 0, -1);
        verify(zSetOperations).score(KEY_FOR_Z_SET, "user:1");
    }

    @Test
    void testRunMatchingService_ProcessesUsersInOrder() {
        // Arrange
        Set<String> waitingUsers = new LinkedHashSet<>();
        waitingUsers.add("user:1");
        waitingUsers.add("user:2");
        waitingUsers.add("user:3");

        UserMatchingDto user1 = UserMatchingDto.builder().userId(1L).userRating(1500).startTime(System.currentTimeMillis()).build();
        UserMatchingDto user2 = UserMatchingDto.builder().userId(2L).userRating(1600).startTime(System.currentTimeMillis()).build();
        UserMatchingDto user3 = UserMatchingDto.builder().userId(3L).userRating(1400).startTime(System.currentTimeMillis()).build();

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.range(KEY_FOR_Z_SET, 0, -1)).thenReturn(waitingUsers);
        when(zSetOperations.score(KEY_FOR_Z_SET, "user:1")).thenReturn(1500.0);
        when(zSetOperations.score(KEY_FOR_Z_SET, "user:2")).thenReturn(1600.0);
        when(zSetOperations.score(KEY_FOR_Z_SET, "user:3")).thenReturn(1400.0);

        when(matchingService.getUserIdFromString("user:1")).thenReturn(1L);
        when(matchingService.getUserIdFromString("user:2")).thenReturn(2L);
        when(matchingService.getUserIdFromString("user:3")).thenReturn(3L);

        when(matchingService.getUserData(1L)).thenReturn(user1);
        when(matchingService.getUserData(2L)).thenReturn(user2);
        when(matchingService.getUserData(3L)).thenReturn(user3);

        // Act
        matchingWorker.runMatchingService();

        // Assert - verify calls were made in order
        var inOrder = inOrder(matchingService);
        inOrder.verify(matchingService).searchForOpponent(1L);
        inOrder.verify(matchingService).searchForOpponent(2L);
        inOrder.verify(matchingService).searchForOpponent(3L);
    }

    @Test
    void testRunMatchingService_WhenAllUsersInvalid_ShouldNotCallSearchForOpponent() {
        // Arrange
        Set<String> waitingUsers = new LinkedHashSet<>();
        waitingUsers.add("user:1");
        waitingUsers.add("user:2");

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.range(KEY_FOR_Z_SET, 0, -1)).thenReturn(waitingUsers);
        when(zSetOperations.score(KEY_FOR_Z_SET, "user:1")).thenReturn(null);

        when(matchingService.getUserIdFromString("user:1")).thenReturn(1L);
        when(matchingService.getUserIdFromString("user:2")).thenReturn(2L);

        when(matchingService.getUserData(1L)).thenReturn(UserMatchingDto.builder().userId(1L).build());
        when(matchingService.getUserData(2L)).thenReturn(null);

        // Act
        matchingWorker.runMatchingService();

        // Assert
        verify(matchingService, never()).searchForOpponent(anyLong());
    }
}