package com.clashcode.backend.service;

import com.clashcode.backend.Notification.Dtos.MatchNotificationDto;
import com.clashcode.backend.dto.MatchCreationDto;
import com.clashcode.backend.dto.MatchResponseDto;
import com.clashcode.backend.dto.MatchSubmissionLogDto;
import com.clashcode.backend.dto.SubmissionRequestDto;
import com.clashcode.backend.enums.MatchState;
import com.clashcode.backend.enums.GameMode;
import com.clashcode.backend.enums.SubmissionStatus;
import com.clashcode.backend.mapper.MatchMapper;
import com.clashcode.backend.mapper.MatchNotificationMapper;
import com.clashcode.backend.mapper.RankMapper;
import com.clashcode.backend.matching.MatchingServiceClient;
import com.clashcode.backend.model.*;
import com.clashcode.backend.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    @Mock private NotificationService notificationService;
    @Mock private UserRepository userRepository;
    @Mock private ProblemRepository problemRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private SubmissionService submissionService;
    @Mock private MatchNotificationMapper matchNotificationMapper;
    @Mock private MatchingServiceClient matchingServiceClient;


    @InjectMocks
    private MatchService matchService;

    @Test
    void test_createMatch_success() {
        Long matchId = 100L;
        int duration = 30;
        GameMode gameMode = GameMode.UNRATED;

        User player1 = User.builder().id(1L).username("p1").build();
        User player2 = User.builder().id(2L).username("p2").build();
        Problem problem = new Problem();
        problem.setId(10L);

        Match savedMatch = Match.builder().id(matchId).build();

        MatchParticipant p1 = MatchParticipant.builder().user(player1).build();
        MatchParticipant p2 = MatchParticipant.builder().user(player2).build();

        Match savedMatch = Match.builder()
                .id(matchId)
                .participants(new ArrayList<>(List.of(p1, p2)))
                .build();

        MatchResponseDto expectedResponse =
                MatchResponseDto.builder().id(matchId).build();

        when(matchMapper.createParticipant(eq(player1), any(Match.class))).thenReturn(p1);
        when(matchMapper.createParticipant(eq(player2), any(Match.class))).thenReturn(p2);

        when(matchRepository.save(any(Match.class))).thenReturn(savedMatch);
        when(matchMapper.toResponseDto(savedMatch)).thenReturn(expectedResponse);

        when(matchNotificationMapper.mapMatchStarted(any(), any()))
                .thenReturn(new MatchNotificationDto());

        ArgumentCaptor<Match> matchCaptor = ArgumentCaptor.forClass(Match.class);

<<<<<<< HEAD
        MatchResponseDto result = matchService.createMatch(player1, player2, problem, duration, gameMode);

=======
        // Act
        MatchResponseDto result =
                matchService.createMatch(player1, player2, problem, duration, gameMode);

        // Assert
>>>>>>> 8c5e9c7 ([CLASHCODE-180] - add testcases for match controller and match service)
        assertNotNull(result);
        assertEquals(matchId, result.getId());

        verify(matchRepository).save(matchCaptor.capture());
        Match constructed = matchCaptor.getValue();

        assertEquals(problem, constructed.getProblem());
        assertEquals(duration, constructed.getDuration());
        assertEquals(gameMode, constructed.getGameMode());
        assertEquals(MatchState.ONGOING, constructed.getMatchState());
<<<<<<< HEAD

        verify(matchParticipantRepository).saveAll(argThat(iterable -> {
            List<MatchParticipant> participants = new ArrayList<>();
            iterable.forEach(participants::add);
            return participants.size() == 2 && participants.containsAll(List.of(p1, p2));
        }));

        verify(matchScheduler).scheduleMatchEnd(savedMatch);

        assertEquals(2, savedMatch.getParticipants().size());
=======
        assertEquals(2, constructed.getParticipants().size());

        verify(matchScheduler).scheduleMatchEnd(savedMatch);

        verify(notificationService, times(2))
                .send(eq(player1.getId()), anyLong(), anyString(), any());

        verify(matchMapper).toResponseDto(savedMatch);
>>>>>>> 8c5e9c7 ([CLASHCODE-180] - add testcases for match controller and match service)
    }


    @Test
    void test_validateMatch_success() {
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
    void test_validateMatch_notOngoingThrows() {
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
    void test_validateMatch_notParticipantThrows() {
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
    void test_completeMatch_setsWinnerLoserRanksCorrectly() {
        // Arrange
        User winner = User.builder().id(1L).username("winnerUser").currentRate(1200).maxRate(1200).build();
        User loser = User.builder().id(2L).username("loserUser").currentRate(1100).maxRate(1100).build();

        MatchParticipant winnerParticipant = MatchParticipant.builder().user(winner).build();
        MatchParticipant loserParticipant = MatchParticipant.builder().user(loser).build();

        Match match = new Match();
        match.setId(500L);
        match.setMatchState(MatchState.ONGOING);
        match.setGameMode(GameMode.UNRATED);
        match.setParticipants(List.of(winnerParticipant, loserParticipant));

        when(rankMapper.toRank("winner")).thenReturn(1);
        when(rankMapper.toRank("loser")).thenReturn(2);
        when(matchNotificationMapper.mapMatchEnded(match)).thenReturn(new MatchNotificationDto());

        // Act
        matchService.completeMatch(match, winner);

        // Assert: State and Ranks
        assertEquals(MatchState.COMPLETED, match.getMatchState());
        assertEquals(1, winnerParticipant.getRank());
        assertEquals(2, loserParticipant.getRank());

        verify(notificationService, times(2)).send(
                eq(match.getId()),
                anyLong(),
                anyString(),
                any(MatchNotificationDto.class)
        );

        verify(matchRepository, atLeastOnce()).save(match);
        verify(matchParticipantRepository).saveAll(match.getParticipants());
        verify(userRepository).saveAll(anyList());
    }

    @Test
    void test_sendMatchInvite_success() {
        User sender = User.builder().id(1L).username("caro").build();
        User recipient = User.builder().id(2L).username("mina").build();

        when(userRepository.findByUsername("mina")).thenReturn(java.util.Optional.of(recipient));
        when(matchNotificationMapper.mapMatchInvite(sender))
                .thenReturn(new MatchNotificationDto());

        matchService.sendMatchInvite(sender, "mina");

        verify(notificationService).send(
                eq(sender.getId()),
                eq(recipient.getId()),
                eq(recipient.getUsername()),
                any(MatchNotificationDto.class)
        );
    }


    @Test
    void test_acceptMatchInvite_success() {
        User player1 = User.builder().id(1L).build();
        User player2 = User.builder().id(2L).build();

        Notification invite = new Notification();
        invite.setSenderId(player2.getId());
        invite.setRecipientId(player1.getId());

        Problem problem = new Problem();
        problem.setId(10L);

        when(notificationRepository.findById(100L))
                .thenReturn(Optional.of(invite));
        when(userRepository.findById(player2.getId()))
                .thenReturn(Optional.of(player2));

        MatchService spyService = Mockito.spy(matchService);

        doReturn(problem)
                .when(spyService)
                .selectProblem(player1, player2);

        doReturn(new MatchResponseDto())
                .when(spyService)
                .createMatch(
                        eq(player1),
                        eq(player2),
                        eq(problem),
                        anyInt(),
                        eq(GameMode.UNRATED)
                );

        MatchResponseDto result = spyService.acceptMatchInvite(player1, 100L);

        assertNotNull(result);

        verify(spyService)
                .createMatch(eq(player1), eq(player2), eq(problem), anyInt(), eq(GameMode.UNRATED));
    }


    @Test
    void test_getMatchSubmissionLog_returnsLogs() {
        User user = User.builder().id(1L).build();
        MatchParticipant participant = MatchParticipant.builder().user(user).build();
        Match match = Match.builder().id(100L).participants(List.of(participant)).build();
        Submission submission = new Submission();
        List<Submission> submissions = List.of(submission);

        when(matchRepository.findById(100L)).thenReturn(java.util.Optional.of(match));
        when(submissionRepository.findByUserIdAndMatchId(user.getId(), 100L)).thenReturn(submissions);
        when(matchMapper.toMatchSubmissionLogDto(participant, submissions)).thenReturn(new MatchSubmissionLogDto());

        List<MatchSubmissionLogDto> logs = matchService.getMatchSubmissionLog(100L);

        assertEquals(1, logs.size());
    }

    @Test
    void test_submitCode_callsNotificationsAndCompleteIfAccepted() {
        // Arrange
        User player = User.builder().id(1L).username("player").build();
        User opponent = User.builder().id(2L).username("opponent").build();

        MatchParticipant participant = MatchParticipant.builder().user(player).build();
        MatchParticipant opponentParticipant = MatchParticipant.builder().user(opponent).build();

        Match match = Match.builder()
                .id(10L)
                .matchState(MatchState.ONGOING)
                .gameMode(GameMode.UNRATED)
                .participants(List.of(participant, opponentParticipant))
                .build();

        SubmissionRequestDto dto = new SubmissionRequestDto();
        dto.setMatchId(10L);

        Submission submission = new Submission();
        submission.setStatus(SubmissionStatus.ACCEPTED);
        submission.setNumberOfPassedTestCases(5);

        MatchService spyService = Mockito.spy(matchService);
        doReturn(match).when(spyService).validateMatch(dto.getMatchId(), player);
        doReturn(submission).when(submissionService).submitCode(dto, player);

        doReturn(new MatchNotificationDto()).when(matchNotificationMapper).mapSubmissionReceived(eq(match), eq(player));
        doReturn(new MatchNotificationDto()).when(matchNotificationMapper).mapSubmissionResult(eq(match), eq(submission));
        doReturn(new MatchNotificationDto()).when(matchNotificationMapper).mapMatchEnded(eq(match));

        spyService.submitCode(dto, player);
        verify(spyService).completeMatch(match, player);
        verify(notificationService, atLeast(2)).send(any(), any(), any(), any());
    }

    @Test
    void test_resignMatch_completesMatchWithOtherParticipant() {
        User resigning = User.builder().id(1L).build();
        User other = User.builder().id(2L).build();

        MatchParticipant p1 = MatchParticipant.builder().user(resigning).build();
        MatchParticipant p2 = MatchParticipant.builder().user(other).build();

        Match match = Match.builder()
                .id(10L)
                .matchState(MatchState.ONGOING)
                .participants(List.of(p1, p2))
                .build();

        when(matchRepository.findById(10L)).thenReturn(java.util.Optional.of(match));

        MatchService spyService = Mockito.spy(matchService);
        doNothing().when(spyService).completeMatch(match, other);

        spyService.resignMatch(10L, resigning);

        verify(spyService).completeMatch(match, other);
    }

    @Test
    void test_startRatedMatch_success() {
        // Arrange
        MatchCreationDto dto = new MatchCreationDto();
        dto.setPlayerIdA(1L);
        dto.setPlayerIdB(2L);

        User userA = User.builder().id(1L).build();
        User userB = User.builder().id(2L).build();

        Problem problem = new Problem();
        problem.setId(10L);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(userA));
        when(userRepository.findById(2L)).thenReturn(java.util.Optional.of(userB));

        MatchService spyService = Mockito.spy(matchService);
        doReturn(problem).when(spyService).selectProblem(userA, userB);
        doReturn(new MatchResponseDto()).when(spyService)
                .createMatch(userA, userB, problem, 15, GameMode.RATED);

        // Act
        spyService.startRatedMatch(dto);

        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(spyService).selectProblem(userA, userB);
        verify(spyService).createMatch(userA, userB, problem, 15, GameMode.RATED);
    }
    @Test
    void test_startRatedMatch_playerNotFound_throwsException() {
        MatchCreationDto dto = new MatchCreationDto();
        dto.setPlayerIdA(1L);
        dto.setPlayerIdB(2L);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> matchService.startRatedMatch(dto)
        );

        assertTrue(ex.getMessage().contains("Player not found"));
    }
    @Test
    void test_searchForOpponent_callsMatchingService() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .currentRate(1500)
                .build();

        // Act
        matchService.searchForOpponent(user);

        // Assert
        verify(matchingServiceClient).requestMatching(
                argThat(req ->
                        req.getUserId() == 1 &&
                                req.getUserRating() == 1500   // 🔥 FIX: primitive comparison
                )
        );
    }
    @Test
    void test_cancelSearchForOpponent_callsMatchingService() {
        // Arrange
        User user = User.builder().id(1L).build();

        // Act
        matchService.cancelSearchForOpponent(user);

        // Assert
        verify(matchingServiceClient).deleteMatching(1L);
    }





}
