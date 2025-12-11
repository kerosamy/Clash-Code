package com.clashcode.backend.service;

import com.clashcode.backend.dto.CreateMatchRequestDto;
import com.clashcode.backend.dto.MatchResponseDto;
import com.clashcode.backend.dto.MatchSubmissionLogDto;
import com.clashcode.backend.enums.MatchState;
import com.clashcode.backend.mapper.MatchMapper;
import com.clashcode.backend.mapper.RankMapper;
import com.clashcode.backend.model.*;
import com.clashcode.backend.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private ProblemRepository problemRepository;
    @Mock private MatchRepository matchRepository;
    @Mock private MatchParticipantRepository matchParticipantRepository;
    @Mock private MatchMapper matchMapper;
    @Mock private MatchScheduler matchScheduler;
    @Mock private SubmissionRepository submissionRepository;
    @Mock private RankMapper rankMapper;
    @InjectMocks
    private MatchService matchService;

    @Test
    void createMatch_success() {
        Long p1Id = 1L;
        Long p2Id = 2L;
        Long problemId = 10L;
        Long matchId = 100L;

        CreateMatchRequestDto reqDto = new CreateMatchRequestDto();
        reqDto.setPlayer1Id(p1Id);
        reqDto.setPlayer2Id(p2Id);
        reqDto.setProblemId(problemId);

        User user1 = User.builder().id(p1Id).build();
        User user2 = User.builder().id(p2Id).build();
        Problem problem = new Problem();
        problem.setId(problemId);

        Match matchBeforeSave = new Match();
        Match matchSaved = Match.builder().id(matchId).build();

        MatchParticipant p1 = MatchParticipant.builder().user(user1).build();
        MatchParticipant p2 = MatchParticipant.builder().user(user2).build();

        MatchResponseDto expectedResponse = MatchResponseDto.builder().id(matchId).build();

        when(userRepository.findById(p1Id)).thenReturn(Optional.of(user1));
        when(userRepository.findById(p2Id)).thenReturn(Optional.of(user2));
        when(problemRepository.findById(problemId)).thenReturn(Optional.of(problem));
        when(matchMapper.toMatchEntity(reqDto, problem)).thenReturn(matchBeforeSave);
        when(matchRepository.save(matchBeforeSave)).thenReturn(matchSaved);
        when(matchMapper.createParticipant(user1, matchSaved)).thenReturn(p1);
        when(matchMapper.createParticipant(user2, matchSaved)).thenReturn(p2);
        when(matchMapper.toResponseDto(matchSaved)).thenReturn(expectedResponse);

        // ✅ stub scheduler correctly
        when(matchScheduler.scheduleMatchEnd(matchSaved)).thenReturn(null);

        // Act
        MatchResponseDto result = matchService.createMatch(reqDto);

        // Assert
        assertNotNull(result);
        assertEquals(matchId, result.getId());

        verify(matchRepository).save(matchBeforeSave);
        verify(matchParticipantRepository).saveAll(anyList());
        verify(matchScheduler).scheduleMatchEnd(matchSaved); // verify scheduler called
    }

    @Test
    void createMatch_Player1DoesNotExist() {
        // Arrange
        CreateMatchRequestDto reqDto = new CreateMatchRequestDto();
        reqDto.setPlayer1Id(99L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> matchService.createMatch(reqDto));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Player 1 does not exist", ex.getReason());

        verify(userRepository, never()).findById(reqDto.getPlayer2Id());
        verifyNoInteractions(matchRepository);
    }

    @Test
    void createMatch_Player2DoesNotExist() {
        // Arrange
        CreateMatchRequestDto reqDto = new CreateMatchRequestDto();
        reqDto.setPlayer1Id(1L);
        reqDto.setPlayer2Id(99L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> matchService.createMatch(reqDto));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Player 2 does not exist", ex.getReason());

        verifyNoInteractions(matchRepository);
    }

    @Test
    void createMatch_ProblemDoesNotExist() {
        // Arrange
        CreateMatchRequestDto reqDto = new CreateMatchRequestDto();
        reqDto.setPlayer1Id(1L);
        reqDto.setPlayer2Id(2L);
        reqDto.setProblemId(99L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(problemRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> matchService.createMatch(reqDto));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Problem does not exist", ex.getReason());

        verifyNoInteractions(matchRepository);
    }

    @Test
    void validateMatch_success() {
        User user = User.builder().id(1L).username("caroline").build();
        MatchParticipant participant = MatchParticipant.builder().user(user).build();
        Match match = Match.builder()
                .id(123L)
                .matchState(MatchState.ONGOING)
                .participants(List.of(participant))
                .build();

        when(matchRepository.findById(123L)).thenReturn(Optional.of(match));

        Match result = matchService.validateMatch(123L, user);

        assertEquals(match, result);
    }

    @Test
    void validateMatch_notOngoingThrows() {
        User user = User.builder().id(1L).build();
        Match match = Match.builder()
                .id(123L)
                .matchState(MatchState.COMPLETED)
                .participants(List.of(MatchParticipant.builder().user(user).build()))
                .build();

        when(matchRepository.findById(123L)).thenReturn(Optional.of(match));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> matchService.validateMatch(123L, user));
        assertTrue(ex.getMessage().contains("not ongoing"));
    }

    @Test
    void validateMatch_notParticipantThrows() {
        User user = User.builder().id(1L).username("caroline").build();
        User other = User.builder().id(2L).build();
        Match match = Match.builder()
                .id(123L)
                .matchState(MatchState.ONGOING)
                .participants(List.of(MatchParticipant.builder().user(other).build()))
                .build();

        when(matchRepository.findById(123L)).thenReturn(Optional.of(match));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> matchService.validateMatch(123L, user));
        assertTrue(ex.getMessage().contains("not a participant"));
    }

    @Test
    void completeMatch_setsWinnerLoserRanksCorrectly() {
        // Arrange
        User winner = new User();
        winner.setId(1L);

        User loser = new User();
        loser.setId(2L);

        MatchParticipant winnerParticipant = new MatchParticipant();
        winnerParticipant.setUser(winner);

        MatchParticipant loserParticipant = new MatchParticipant();
        loserParticipant.setUser(loser);

        Match match = new Match();
        match.setMatchState(MatchState.ONGOING);
        match.setParticipants(List.of(winnerParticipant, loserParticipant));

        when(rankMapper.toRank("winner")).thenReturn(2);
        when(rankMapper.toRank("loser")).thenReturn(1);

        matchService.completeMatch(match, winner);

        // Assert match state
        assertEquals(MatchState.COMPLETED, match.getMatchState());

        // Assert ranks
        assertEquals(2, winnerParticipant.getRank());
        assertEquals(1, loserParticipant.getRank());

        // Verify saving interactions
        verify(matchRepository).save(match);
        verify(matchParticipantRepository).saveAll(match.getParticipants());
    }
}