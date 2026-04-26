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

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private RedisService redisService;

    @Mock
    private MainBackendClient mainBackendClient;

    @InjectMocks
    private MatchingService matchingService;

    @BeforeEach
    void setUp() {
        // No setup required here
    }

    @Test
    void testAddUserToMatchingService_ShouldCallRedisService() {
        MatchingRequestDto dto = new MatchingRequestDto(1L, 1500);

        matchingService.addUserToMatchingService(dto);

        verify(redisService).insertUser(1L, 1500);
    }

    @Test
    void testRemoveUserFromMatchingService_ShouldCallRedisService() {
        matchingService.removeUserFromMatchingService(1L);

        verify(redisService).removeUser(1L);
    }

    @Test
    void testSearchForOpponent_WhenUserDataIsNull_ShouldNotMatch() {
        Long userId = 1L;
        when(redisService.getUserData(userId)).thenReturn(null);

        matchingService.searchForOpponent(userId);

        verify(mainBackendClient, never()).MatchingTwoPlayers(any());
    }

    @Test
    void testSearchForOpponent_WhenNoCandidatesFound_ShouldNotMatch() {
        Long userId = 1L;
        long now = System.currentTimeMillis();
        UserMatchingDto userDto = UserMatchingDto.builder()
                .userId(userId)
                .userRating(1500)
                .startTime(now)
                .build();

        when(redisService.getUserData(userId)).thenReturn(userDto);
        when(redisService.getUsersInRage(anyInt(), anyInt())).thenReturn(new HashSet<>());

        matchingService.searchForOpponent(userId);

        verify(mainBackendClient, never()).MatchingTwoPlayers(any());
    }

    @Test
    void testSearchForOpponent_WhenOnlySelfInCandidates_ShouldNotMatch() {
        Long userId = 1L;
        long now = System.currentTimeMillis();
        UserMatchingDto userDto = UserMatchingDto.builder()
                .userId(userId)
                .userRating(1500)
                .startTime(now)
                .build();

        Set<String> candidates = new HashSet<>();
        candidates.add("user:1");

        when(redisService.getUserData(userId)).thenReturn(userDto);
        when(redisService.getUsersInRage(anyInt(), anyInt())).thenReturn(candidates);
        when(redisService.getUserIdFromString("user:1")).thenReturn(1L);

        matchingService.searchForOpponent(userId);

        verify(mainBackendClient, never()).MatchingTwoPlayers(any());
    }

    @Test
    void testSearchForOpponent_WhenOpponentFound_ShouldMatchFirstOpponent() {
        Long userId = 1L;
        long now = System.currentTimeMillis();
        UserMatchingDto userDto = UserMatchingDto.builder()
                .userId(userId)
                .userRating(1500)
                .startTime(now)
                .build();

        Set<String> candidates = new HashSet<>();
        candidates.add("user:1");
        candidates.add("user:2");
        candidates.add("user:3");

        when(redisService.getUserData(userId)).thenReturn(userDto);
        when(redisService.getUsersInRage(anyInt(), anyInt())).thenReturn(candidates);
        when(redisService.getUserIdFromString("user:2")).thenReturn(2L); // only necessary stub

        matchingService.searchForOpponent(userId);

        ArgumentCaptor<MatchCreationDto> captor = ArgumentCaptor.forClass(MatchCreationDto.class);
        verify(mainBackendClient).MatchingTwoPlayers(captor.capture());

        MatchCreationDto dto = captor.getValue();
        assertEquals(userId, dto.getPlayerIdA());
        assertEquals(2L, dto.getPlayerIdB());
    }


    @Test
    void testMatchTwoUsers_ShouldCallRedisServiceAndBackend() {
        Long userId1 = 1L;
        Long userId2 = 2L;

        matchingService.matchTwoUsers(userId1, userId2);

        verify(redisService).removeUser(userId1);
        verify(redisService).removeUser(userId2);

        ArgumentCaptor<MatchCreationDto> captor = ArgumentCaptor.forClass(MatchCreationDto.class);
        verify(mainBackendClient).MatchingTwoPlayers(captor.capture());

        MatchCreationDto dto = captor.getValue();
        assertEquals(userId1, dto.getPlayerIdA());
        assertEquals(userId2, dto.getPlayerIdB());
    }
}
