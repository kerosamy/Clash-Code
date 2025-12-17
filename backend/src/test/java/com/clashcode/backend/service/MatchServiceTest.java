package com.clashcode.backend.service;

import com.clashcode.backend.dto.MatchNotificationDto;
import com.clashcode.backend.dto.MatchResponseDto;
import com.clashcode.backend.dto.MatchSubmissionLogDto;
import com.clashcode.backend.dto.SubmissionRequestDto;
import com.clashcode.backend.enums.MatchState;
import com.clashcode.backend.enums.GameMode;
import com.clashcode.backend.enums.NotificationType;
import com.clashcode.backend.enums.SubmissionStatus;
import com.clashcode.backend.mapper.MatchMapper;
import com.clashcode.backend.mapper.MatchNotificationMapper;
import com.clashcode.backend.mapper.RankMapper;
import com.clashcode.backend.model.*;
import com.clashcode.backend.repository.*;
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
    @Mock private NotificationService notificationService;
    @Mock private UserRepository userRepository;
    @Mock private ProblemRepository problemRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private SubmissionService submissionService;
    @Mock private MatchNotificationMapper matchNotificationMapper;


    @InjectMocks
    private MatchService matchService;

    @Test
    void test_createMatch_success() {
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

    @Test
    void test_sendMatchInvite_success() {
        User sender = User.builder().id(1L).username("caro").build();
        User recipient = User.builder().id(2L).username("mina").build();

        when(userRepository.findByUsername("mina")).thenReturn(java.util.Optional.of(recipient));
        when(matchNotificationMapper.mapMatchInvite(sender, recipient))
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

        when(notificationRepository.findById(100L)).thenReturn(java.util.Optional.of(invite));
        when(userRepository.findById(player2.getId())).thenReturn(java.util.Optional.of(player2));

        MatchService spyService = Mockito.spy(matchService);
        doReturn(problem).when(spyService).selectProblem(player1, player2);
        doReturn(new MatchResponseDto()).when(spyService).createMatch(player1, player2, problem, 30, GameMode.UNRATED);

        MatchResponseDto result = spyService.acceptMatchInvite(player1, 100L);

        assertNotNull(result);
        verify(spyService).createMatch(player1, player2, problem, 30, GameMode.UNRATED);
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
        MatchParticipant participant = MatchParticipant.builder().user(player).build();
        Match match = Match.builder()
                .id(10L)
                .matchState(MatchState.ONGOING)
                .participants(List.of(participant))
                .build();

        SubmissionRequestDto dto = new SubmissionRequestDto();
        dto.setMatchId(10L);

        Submission submission = new Submission();
        submission.setStatus(SubmissionStatus.ACCEPTED);
        submission.setNumberOfPassedTestCases(5);

        // Spy on service to mock validateMatch and submissionService
        MatchService spyService = Mockito.spy(matchService);
        doReturn(match).when(spyService).validateMatch(dto.getMatchId(), player);
        doReturn(submission).when(submissionService).submitCode(dto, player);

        // Mock MatchNotificationMapper to return dummy DTOs
        MatchNotificationDto receivedDto = new MatchNotificationDto();
        MatchNotificationDto resultDto = new MatchNotificationDto();
        doReturn(receivedDto).when(matchNotificationMapper).mapSubmissionReceived(eq(match), eq(player), any(User.class));
        doReturn(resultDto).when(matchNotificationMapper).mapSubmissionResult(eq(match), eq(submission), any(User.class));

        // Act
        spyService.submitCode(dto, player);

        // Assert: verify both notifications separately
        verify(notificationService).send(eq(player.getId()), eq(player.getId()), eq(player.getUsername()), eq(receivedDto)); // submission received
        verify(notificationService).send(eq(player.getId()), eq(player.getId()), eq(player.getUsername()), eq(resultDto));   // submission result

        // Verify match completion
        verify(spyService).completeMatch(match, player);
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

}
