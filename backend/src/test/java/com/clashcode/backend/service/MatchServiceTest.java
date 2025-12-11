package com.clashcode.backend.service;

import com.clashcode.backend.dto.MatchResponseDto;
import com.clashcode.backend.enums.MatchState;
import com.clashcode.backend.enums.GameMode;
import com.clashcode.backend.mapper.MatchMapper;
import com.clashcode.backend.mapper.RankMapper;
import com.clashcode.backend.model.Match;
import com.clashcode.backend.model.MatchParticipant;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.MatchParticipantRepository;
import com.clashcode.backend.repository.MatchRepository;
import com.clashcode.backend.repository.SubmissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock private MatchRepository matchRepository;
    @Mock private MatchParticipantRepository matchParticipantRepository;
    @Mock private MatchMapper matchMapper;
    @Mock private MatchScheduler matchScheduler;
    @Mock private SubmissionRepository submissionRepository;
    @Mock private RankMapper rankMapper;

    @InjectMocks
    private MatchService matchService;

    @Test
    void createMatch_withEntities_success() {
        // Arrange
        Long matchId = 100L;
        int duration = 30;
        GameMode gameMode = GameMode.UNRATED;

        User player1 = User.builder().id(1L).username("p1").build();
        User player2 = User.builder().id(2L).username("p2").build();
        Problem problem = new Problem();
        problem.setId(10L);

        // The service constructs a new Match internally and calls save
        Match savedMatch = Match.builder().id(matchId).build();

        MatchParticipant p1 = MatchParticipant.builder().user(player1).build();
        MatchParticipant p2 = MatchParticipant.builder().user(player2).build();

        MatchResponseDto expectedResponse = MatchResponseDto.builder().id(matchId).build();

        when(matchRepository.save(any(Match.class))).thenReturn(savedMatch);
        when(matchMapper.createParticipant(player1, savedMatch)).thenReturn(p1);
        when(matchMapper.createParticipant(player2, savedMatch)).thenReturn(p2);
        when(matchMapper.toResponseDto(savedMatch)).thenReturn(expectedResponse);
        when(matchScheduler.scheduleMatchEnd(savedMatch)).thenReturn(null);

        ArgumentCaptor<Match> matchCaptor = ArgumentCaptor.forClass(Match.class);

        // Act
        MatchResponseDto result = matchService.createMatch(player1, player2, problem, duration, gameMode);

        // Assert response
        assertNotNull(result);
        assertEquals(matchId, result.getId());

        // Verify save and capture the constructed match to assert fields
        verify(matchRepository).save(matchCaptor.capture());
        Match constructed = matchCaptor.getValue();
        assertEquals(problem, constructed.getProblem());
        assertEquals(duration, constructed.getDuration());
        assertEquals(gameMode, constructed.getGameMode());
        assertEquals(MatchState.ONGOING, constructed.getMatchState());

        // Verify participants persisted and scheduler invoked
        verify(matchParticipantRepository).saveAll(argThat(iterable -> {
            List<MatchParticipant> participants = new ArrayList<>();
            iterable.forEach(participants::add);
            return participants.size() == 2 && participants.containsAll(List.of(p1, p2));
        }));

        verify(matchScheduler).scheduleMatchEnd(savedMatch);

        // Ensure participants set on saved match before mapping
        assertEquals(2, savedMatch.getParticipants().size());
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

        when(matchRepository.findById(123L)).thenReturn(java.util.Optional.of(match));

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

        when(matchRepository.findById(123L)).thenReturn(java.util.Optional.of(match));

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

        when(matchRepository.findById(123L)).thenReturn(java.util.Optional.of(match));

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
