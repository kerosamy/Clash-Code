package com.clashcode.matching_service.service;

import com.clashcode.matching_service.dto.UserMatchingDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingWorkerTest {

    @Mock
    private RedisService redisService;

    @Mock
    private MatchingService matchingService;

    @InjectMocks
    private MatchingWorker matchingWorker;

    @Test
    void testRunMatchingService_WithWaitingUsers_ShouldProcessAll() {
        Set<String> waitingUsers = new LinkedHashSet<>();
        waitingUsers.add("user:1");
        waitingUsers.add("user:2");
        waitingUsers.add("user:3");

        UserMatchingDto user1 = UserMatchingDto.builder().userId(1L).userRating(1500).startTime(System.currentTimeMillis()).build();
        UserMatchingDto user2 = UserMatchingDto.builder().userId(2L).userRating(1600).startTime(System.currentTimeMillis()).build();
        UserMatchingDto user3 = UserMatchingDto.builder().userId(3L).userRating(1400).startTime(System.currentTimeMillis()).build();

        when(redisService.getAllUsers()).thenReturn(waitingUsers);
        when(redisService.getUserIdFromString("user:1")).thenReturn(1L);
        when(redisService.getUserIdFromString("user:2")).thenReturn(2L);
        when(redisService.getUserIdFromString("user:3")).thenReturn(3L);

        when(redisService.getUserData(1L)).thenReturn(user1);
        when(redisService.getUserData(2L)).thenReturn(user2);
        when(redisService.getUserData(3L)).thenReturn(user3);

        when(redisService.isUserExist("user:1")).thenReturn(true);
        when(redisService.isUserExist("user:2")).thenReturn(true);
        when(redisService.isUserExist("user:3")).thenReturn(true);

        matchingWorker.runMatchingService();

        verify(matchingService).searchForOpponent(1L);
        verify(matchingService).searchForOpponent(2L);
        verify(matchingService).searchForOpponent(3L);
    }

    @Test
    void testRunMatchingService_WhenNoWaitingUsers_ShouldNotProcessAnything() {
        when(redisService.getAllUsers()).thenReturn(new HashSet<>());

        matchingWorker.runMatchingService();

        verify(matchingService, never()).searchForOpponent(anyLong());
    }

    @Test
    void testRunMatchingService_WhenWaitingUsersIsNull_ShouldReturnEarly() {
        when(redisService.getAllUsers()).thenReturn(null);

        matchingWorker.runMatchingService();

        verify(matchingService, never()).searchForOpponent(anyLong());
    }

    @Test
    void testRunMatchingService_WhenUserDataIsNull_ShouldSkipThatUser() {
        Set<String> waitingUsers = Set.of("user:1", "user:2");
        UserMatchingDto user2 = UserMatchingDto.builder().userId(2L).userRating(1600).startTime(System.currentTimeMillis()).build();

        when(redisService.getAllUsers()).thenReturn(waitingUsers);
        when(redisService.getUserIdFromString("user:1")).thenReturn(1L);
        when(redisService.getUserIdFromString("user:2")).thenReturn(2L);

        when(redisService.getUserData(1L)).thenReturn(null);
        when(redisService.getUserData(2L)).thenReturn(user2);

        when(redisService.isUserExist("user:2")).thenReturn(true);

        matchingWorker.runMatchingService();

        verify(matchingService, never()).searchForOpponent(1L);
        verify(matchingService).searchForOpponent(2L);
    }

    @Test
    void testRunMatchingService_WhenUserNoLongerWaiting_ShouldSkipThatUser() {
        Set<String> waitingUsers = Set.of("user:1", "user:2");
        UserMatchingDto user1 = UserMatchingDto.builder().userId(1L).userRating(1500).startTime(System.currentTimeMillis()).build();
        UserMatchingDto user2 = UserMatchingDto.builder().userId(2L).userRating(1600).startTime(System.currentTimeMillis()).build();

        when(redisService.getAllUsers()).thenReturn(waitingUsers);
        when(redisService.getUserIdFromString("user:1")).thenReturn(1L);
        when(redisService.getUserIdFromString("user:2")).thenReturn(2L);

        when(redisService.getUserData(1L)).thenReturn(user1);
        when(redisService.getUserData(2L)).thenReturn(user2);

        when(redisService.isUserExist("user:1")).thenReturn(false);
        when(redisService.isUserExist("user:2")).thenReturn(true);

        matchingWorker.runMatchingService();

        verify(matchingService, never()).searchForOpponent(1L);
        verify(matchingService).searchForOpponent(2L);
    }

    @Test
    void testRunMatchingService_ProcessesUsersInOrder() {
        Set<String> waitingUsers = new LinkedHashSet<>();
        waitingUsers.add("user:1");
        waitingUsers.add("user:2");

        UserMatchingDto user1 = UserMatchingDto.builder().userId(1L).userRating(1500).startTime(System.currentTimeMillis()).build();
        UserMatchingDto user2 = UserMatchingDto.builder().userId(2L).userRating(1600).startTime(System.currentTimeMillis()).build();

        when(redisService.getAllUsers()).thenReturn(waitingUsers);
        when(redisService.getUserIdFromString("user:1")).thenReturn(1L);
        when(redisService.getUserIdFromString("user:2")).thenReturn(2L);

        when(redisService.getUserData(1L)).thenReturn(user1);
        when(redisService.getUserData(2L)).thenReturn(user2);

        when(redisService.isUserExist("user:1")).thenReturn(true);
        when(redisService.isUserExist("user:2")).thenReturn(true);

        matchingWorker.runMatchingService();

        InOrder inOrder = inOrder(matchingService);
        inOrder.verify(matchingService).searchForOpponent(1L);
        inOrder.verify(matchingService).searchForOpponent(2L);
    }


}
