package com.clashcode.backend.service;

import com.clashcode.backend.Notification.Dtos.MatchNotificationDto;
import com.clashcode.backend.Notification.MatchCompletedPayload;
import com.clashcode.backend.Notification.MatchInvitationPayload;
import com.clashcode.backend.Notification.MatchStartedPayload;
import com.clashcode.backend.dto.MatchCreationDto;
import com.clashcode.backend.dto.MatchResponseDto;
import com.clashcode.backend.dto.MatchSubmissionLogDto;
import com.clashcode.backend.dto.SubmissionRequestDto;
import com.clashcode.backend.enums.MatchState;
import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.GameMode;
import com.clashcode.backend.enums.MatchState;
import com.clashcode.backend.enums.NotificationType;
import com.clashcode.backend.enums.SubmissionStatus;
import com.clashcode.backend.exception.UnauthorizedException;
import com.clashcode.backend.exception.UserNotFoundException;
import com.clashcode.backend.mapper.MatchMapper;
import com.clashcode.backend.mapper.MatchNotificationMapper;
import com.clashcode.backend.mapper.RankMapper;
import com.clashcode.backend.matching.MatchingServiceClient;
import com.clashcode.backend.matching.dto.MatchingRequestDto;
import com.clashcode.backend.model.*;
import com.clashcode.backend.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;
    @Mock
    private MatchParticipantRepository matchParticipantRepository;
    @Mock
    private MatchMapper matchMapper;
    @Mock
    private MatchScheduler matchScheduler;
    @Mock
    private SubmissionRepository submissionRepository;
    @Mock
    private RankMapper rankMapper;
    @Mock
    private NotificationService notificationService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProblemRepository problemRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private SubmissionService submissionService;
    @Mock
    private MatchNotificationMapper matchNotificationMapper;
    @Mock
    private MatchingServiceClient matchingServiceClient;
    @Mock
    private ProblemService problemService;

    @InjectMocks
    private MatchService matchService;

    // ==================== CREATE MATCH TESTS ====================

    @Nested
    @DisplayName("createMatch Tests")
    class CreateMatchTests {

//        @Test
//        @DisplayName("Should create match successfully with all participants")
//        void test_createMatch_success() {
//            Long matchId = 100L;
//            int duration = 30;
//            GameMode gameMode = GameMode.UNRATED;
//
//            User player1 = User.builder().id(1L).username("p1").build();
//            User player2 = User.builder().id(2L).username("p2").build();
//            Problem problem = new Problem();
//            problem.setId(10L);
//
//            User managedPlayer1 = User.builder().id(1L).username("p1").build();
//            User managedPlayer2 = User.builder().id(2L).username("p2").build();
//
//            MatchParticipant p1 = MatchParticipant.builder().user(managedPlayer1).build();
//            MatchParticipant p2 = MatchParticipant.builder().user(managedPlayer2).build();
//
//            Match savedMatch = Match.builder()
//                    .id(matchId)
//                    .participants(new ArrayList<>(List.of(p1, p2)))
//                    .problem(problem)
//                    .duration(duration)
//                    .gameMode(gameMode)
//                    .matchState(MatchState.ONGOING)
//                    .build();
//
//            MatchResponseDto expectedResponse = MatchResponseDto.builder().id(matchId).build();
//            MatchNotificationDto notificationDto = new MatchNotificationDto();
//
//            when(userRepository.findById(1L)).thenReturn(Optional.of(managedPlayer1));
//            when(userRepository.findById(2L)).thenReturn(Optional.of(managedPlayer2));
//            when(matchMapper.createParticipant(eq(managedPlayer1), any(Match.class))).thenReturn(p1);
//            when(matchMapper.createParticipant(eq(managedPlayer2), any(Match.class))).thenReturn(p2);
//            when(matchRepository.save(any(Match.class))).thenReturn(savedMatch);
//            when(matchMapper.toResponseDto(savedMatch)).thenReturn(expectedResponse);
//            when(matchNotificationMapper.mapMatchStarted(any(Match.class), any(User.class)))
//                    .thenReturn(notificationDto);
//
//            ArgumentCaptor<Match> matchCaptor = ArgumentCaptor.forClass(Match.class);
//
//            MatchResponseDto result = matchService.createMatch(player1, player2, problem, duration, gameMode);
//
//            assertNotNull(result);
//            assertEquals(matchId, result.getId());
//
//            verify(userRepository).findById(1L);
//            verify(userRepository).findById(2L);
//            verify(matchRepository).save(matchCaptor.capture());
//
//            Match constructed = matchCaptor.getValue();
//            assertEquals(problem, constructed.getProblem());
//            assertEquals(duration, constructed.getDuration());
//            assertEquals(gameMode, constructed.getGameMode());
//            assertEquals(MatchState.ONGOING, constructed.getMatchState());
//            assertEquals(2, constructed.getParticipants().size());
//
//            verify(matchScheduler).scheduleMatchEnd(savedMatch);
//            verify(notificationService, times(2)).send(anyLong(), anyLong(), anyString(), any(MatchNotificationDto.class));
//            verify(matchNotificationMapper, times(2)).mapMatchStarted(eq(savedMatch), any(User.class));
//        }

        @Test
        @DisplayName("Should throw exception when player1 not found")
        void test_createMatch_player1NotFound() {
            User player1 = User.builder().id(1L).build();
            User player2 = User.builder().id(2L).build();
            Problem problem = new Problem();

            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () ->
                    matchService.createMatch(player1, player2, problem, 30, GameMode.UNRATED)
            );
        }

        @Test
        @DisplayName("Should throw exception when player2 not found")
        void test_createMatch_player2NotFound() {
            User player1 = User.builder().id(1L).build();
            User player2 = User.builder().id(2L).build();
            Problem problem = new Problem();

            when(userRepository.findById(1L)).thenReturn(Optional.of(player1));
            when(userRepository.findById(2L)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () ->
                    matchService.createMatch(player1, player2, problem, 30, GameMode.UNRATED)
            );
        }
    }

    // ==================== VALIDATE MATCH TESTS ====================

    @Nested
    @DisplayName("validateMatch Tests")
    class ValidateMatchTests {

        @Test
        @DisplayName("Should validate match successfully")
        void test_validateMatch_success() {
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
        @DisplayName("Should throw exception when match is not ongoing")
        void test_validateMatch_notOngoingThrows() {
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
        @DisplayName("Should throw exception when user is not participant")
        void test_validateMatch_notParticipantThrows() {
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
        @DisplayName("Should return null when matchId is null")
        void test_validateMatch_nullMatchId_returnsNull() {
            User user = User.builder().id(1L).build();

            Match result = matchService.validateMatch(null, user);

            assertNull(result);
            verify(matchRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("Should throw exception when match not found")
        void test_validateMatch_matchNotFound_throwsException() {
            User user = User.builder().id(1L).build();

            when(matchRepository.findById(100L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () ->
                    matchService.validateMatch(100L, user)
            );
        }
    }

    // ==================== COMPLETE MATCH TESTS ====================

    @Nested
    @DisplayName("completeMatch Tests")
    class CompleteMatchTests {

//        @Test
//        @DisplayName("Should complete match and set winner/loser ranks correctly")
//        void test_completeMatch_setsWinnerLoserRanksCorrectly() {
//            User winner = User.builder().id(1L).username("winnerUser").currentRate(1200).maxRate(1200).build();
//            User loser = User.builder().id(2L).username("loserUser").currentRate(1100).maxRate(1100).build();
//
//            MatchParticipant winnerParticipant = MatchParticipant.builder().user(winner).build();
//            MatchParticipant loserParticipant = MatchParticipant.builder().user(loser).build();
//
//            Match match = new Match();
//            match.setId(500L);
//            match.setMatchState(MatchState.ONGOING);
//            match.setGameMode(GameMode.UNRATED);
//            match.setParticipants(List.of(winnerParticipant, loserParticipant));
//
//            when(rankMapper.toRank("winner")).thenReturn(1);
//            when(rankMapper.toRank("loser")).thenReturn(2);
//            when(matchNotificationMapper.mapMatchEnded(match)).thenReturn(new MatchNotificationDto());
//
//            matchService.completeMatch(match, winner);
//
//            assertEquals(MatchState.COMPLETED, match.getMatchState());
//            assertEquals(1, winnerParticipant.getRank());
//            assertEquals(2, loserParticipant.getRank());
//
//            verify(notificationService, times(2)).send(eq(match.getId()), anyLong(), anyString(), any(MatchNotificationDto.class));
//            verify(matchRepository, atLeastOnce()).save(match);
//            verify(matchParticipantRepository).saveAll(match.getParticipants());
//            verify(userRepository).saveAll(anyList());
//        }

//        @Test
//        @DisplayName("Should handle draw when winner is null")
//        void test_completeMatch_withNullWinner_setsDrawRanks() {
//            User player1 = User.builder().id(1L).currentRate(1200).maxRate(1200).build();
//            User player2 = User.builder().id(2L).currentRate(1100).maxRate(1100).build();
//
//            MatchParticipant p1 = MatchParticipant.builder().user(player1).build();
//            MatchParticipant p2 = MatchParticipant.builder().user(player2).build();
//
//            Match match = new Match();
//            match.setId(500L);
//            match.setMatchState(MatchState.ONGOING);
//            match.setGameMode(GameMode.UNRATED);
//            match.setParticipants(List.of(p1, p2));
//
//            when(rankMapper.toRank("draw")).thenReturn(0);
//            when(matchNotificationMapper.mapMatchEnded(match)).thenReturn(new MatchNotificationDto());
//
//            matchService.completeMatch(match, null);
//
//            assertEquals(MatchState.COMPLETED, match.getMatchState());
//            assertEquals(0, p1.getRank());
//            assertEquals(0, p2.getRank());
//
//            verify(rankMapper, times(2)).toRank("draw");
//            verify(matchRepository, atLeastOnce()).save(match);
//            verify(matchParticipantRepository).saveAll(match.getParticipants());
//        }

        @Test
        @DisplayName("Should return early if match already completed")
        void test_completeMatch_alreadyCompleted_returnsEarly() {
            Match match = new Match();
            match.setMatchState(MatchState.COMPLETED);
            User winner = User.builder().id(1L).build();

            matchService.completeMatch(match, winner);

            verify(matchRepository, never()).save(match);
            verify(matchParticipantRepository, never()).saveAll(anyList());
        }

        @Test
        @DisplayName("Should throw exception when match is null")
        void test_completeMatch_nullMatch_throwsException() {
            User winner = User.builder().id(1L).build();

            assertThrows(IllegalArgumentException.class, () ->
                    matchService.completeMatch(null, winner)
            );
        }

        @Test
        @DisplayName("Should throw exception for invalid participant count")
        void test_completeMatch_invalidParticipantCount_throwsException() {
            Match match = new Match();
            match.setMatchState(MatchState.ONGOING);
            match.setParticipants(List.of());

            User winner = User.builder().id(1L).build();

            assertThrows(IllegalStateException.class, () ->
                    matchService.completeMatch(match, winner)
            );
        }

//        @Test
//        @DisplayName("Should update ELO ratings for rated match")
//        void test_completeMatch_ratedMatch_updatesEloRatings() {
//            User winner = User.builder().id(1L).currentRate(1200).maxRate(1200).build();
//            User loser = User.builder().id(2L).currentRate(1100).maxRate(1100).build();
//
//            MatchParticipant winnerParticipant = MatchParticipant.builder().user(winner).build();
//            MatchParticipant loserParticipant = MatchParticipant.builder().user(loser).build();
//
//            Problem problem = new Problem();
//            problem.setRate(1200);
//
//            Match match = new Match();
//            match.setId(500L);
//            match.setMatchState(MatchState.ONGOING);
//            match.setGameMode(GameMode.RATED);
//            match.setProblem(problem);
//            match.setParticipants(List.of(winnerParticipant, loserParticipant));
//
//            when(rankMapper.toRank("winner")).thenReturn(1);
//            when(rankMapper.toRank("loser")).thenReturn(2);
//            when(matchNotificationMapper.mapMatchEnded(match)).thenReturn(new MatchNotificationDto());
//
//            matchService.completeMatch(match, winner);
//
//            assertEquals(MatchState.COMPLETED, match.getMatchState());
//            assertNotNull(winnerParticipant.getRateChange());
//            assertNotNull(loserParticipant.getRateChange());
//            assertTrue(winner.getCurrentRate() >= 0);
//            assertTrue(loser.getCurrentRate() >= 0);
//
//            verify(userRepository).saveAll(anyList());
//        }
    }

    // ==================== MATCH INVITATION TESTS ====================

    @Nested
    @DisplayName("Match Invitation Tests")
    class MatchInvitationTests {

//        @Test
//        @DisplayName("Should send match invite successfully")
//        void test_sendMatchInvite_success() {
//            User sender = User.builder().id(1L).username("caro").build();
//            User recipient = User.builder().id(2L).username("mina").build();
//
//            MatchNotificationDto dto = new MatchNotificationDto();
//
//            when(userRepository.findByUsername("mina")).thenReturn(Optional.of(recipient));
//            when(matchNotificationMapper.mapMatchInvite(sender)).thenReturn(dto);
//            when(notificationService.send(sender.getId(), recipient.getId(), recipient.getUsername(), dto))
//                    .thenReturn(Optional.of(100L));
//
//            Long result = matchService.sendMatchInvite(sender, "mina");
//
//            assertNotNull(result);
//            assertEquals(100L, result);
//
//            verify(userRepository).findByUsername("mina");
//            verify(matchNotificationMapper).mapMatchInvite(sender);
//            verify(notificationService).send(sender.getId(), recipient.getId(), recipient.getUsername(), dto);
//        }

        @Test
        @DisplayName("Should throw exception when recipient not found")
        void test_sendMatchInvite_recipientNotFound() {
            User sender = User.builder().id(1L).build();

            when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () ->
                    matchService.sendMatchInvite(sender, "nonexistent")
            );
        }

        @Test
        @DisplayName("Should accept match invite successfully")
        void test_acceptMatchInvite_success() {
            User player1 = User.builder().id(1L).build();
            User player2 = User.builder().id(2L).build();

            Notification invite = new Notification();
            invite.setSenderId(player2.getId());
            invite.setRecipientId(player1.getId());

            Problem problem = new Problem();
            problem.setId(10L);

            when(notificationRepository.findById(100L)).thenReturn(Optional.of(invite));
            when(userRepository.findById(player2.getId())).thenReturn(Optional.of(player2));

            MatchService spyService = Mockito.spy(matchService);

            doReturn(problem).when(spyService).selectProblem(player1, player2);
            doReturn(new MatchResponseDto()).when(spyService)
                    .createMatch(eq(player1), eq(player2), eq(problem), anyInt(), eq(GameMode.UNRATED));

            MatchResponseDto result = spyService.acceptMatchInvite(player1, 100L);

            assertNotNull(result);
            verify(spyService).createMatch(eq(player1), eq(player2), eq(problem), anyInt(), eq(GameMode.UNRATED));
        }

        @Test
        @DisplayName("Should throw exception when accepting invite with wrong recipient")
        void test_acceptMatchInvite_unauthorizedRecipient() {
            User wrongUser = User.builder().id(999L).build();

            Notification invite = new Notification();
            invite.setRecipientId(1L);

            when(notificationRepository.findById(100L)).thenReturn(Optional.of(invite));

            assertThrows(UnauthorizedException.class, () ->
                    matchService.acceptMatchInvite(wrongUser, 100L)
            );
        }

//        @Test
//        @DisplayName("Should cancel match invite successfully")
//        void test_cancelMatchInvite_success() {
//            User sender = User.builder().id(1L).username("sender").build();
//            User recipient = User.builder().id(2L).username("recipient").build();
//
//            Notification invite = new Notification();
//            invite.setId(100L);
//            invite.setSenderId(sender.getId());
//            invite.setRecipientId(recipient.getId());
//            invite.setType(NotificationType.MATCH_INVITATION);
//
//            when(notificationRepository.findById(100L)).thenReturn(Optional.of(invite));
//            when(userRepository.findById(recipient.getId())).thenReturn(Optional.of(recipient));
//            when(matchNotificationMapper.mapMatchInvitationCanceled(sender)).thenReturn(new MatchNotificationDto());
//
//            matchService.cancelMatchInvite(sender, 100L);
//
//            assertEquals(NotificationType.MATCH_INVITATION_CANCELED, invite.getType());
//            assertEquals("Match Invitation Canceled", invite.getTitle());
//            verify(notificationRepository).save(invite);
//            verify(notificationService).send(eq(sender.getId()), eq(recipient.getId()), eq(recipient.getUsername()), any(MatchNotificationDto.class));
//        }

        @Test
        @DisplayName("Should throw exception when notification not found for cancellation")
        void test_cancelMatchInvite_notificationNotFound() {
            User sender = User.builder().id(1L).build();

            when(notificationRepository.findById(100L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () ->
                    matchService.cancelMatchInvite(sender, 100L)
            );
        }

        @Test
        @DisplayName("Should throw exception when canceling with wrong sender")
        void test_cancelMatchInvite_unauthorizedSender() {
            User wrongSender = User.builder().id(999L).build();

            Notification invite = new Notification();
            invite.setSenderId(1L);

            when(notificationRepository.findById(100L)).thenReturn(Optional.of(invite));

            assertThrows(UnauthorizedException.class, () ->
                    matchService.cancelMatchInvite(wrongSender, 100L)
            );
        }

        @Test
        @DisplayName("Should throw exception when canceling non-invitation notification")
        void test_cancelMatchInvite_notMatchInvitation() {
            User sender = User.builder().id(1L).build();

            Notification notification = new Notification();
            notification.setSenderId(1L);
            notification.setType(NotificationType.MATCH_INVITATION_CANCELED);

            when(notificationRepository.findById(100L)).thenReturn(Optional.of(notification));

            assertThrows(IllegalArgumentException.class, () ->
                    matchService.cancelMatchInvite(sender, 100L)
            );
        }
    }

    // ==================== PROBLEM SELECTION TESTS ====================

//    @Nested
//    @DisplayName("selectProblem Tests")
//    class SelectProblemTests {
//
//        @Test
//        @DisplayName("Should find problem in rate range")
//        void test_selectProblem_findsInRateRange() {
//            User userA = User.builder().currentRate(1200).build();
//            User userB = User.builder().currentRate(1400).build();
//
//            Problem problem1 = new Problem();
//            problem1.setId(1L);
//            problem1.setRate(1300);
//
//            List<Problem> problems = List.of(problem1);
//
//            int avgRate = (1200 + 1400) / 2;
//            int minRate = avgRate - 300;
//            int maxRate = avgRate + 300;
//
//            when(problemRepository.findProblemsInRateRange(minRate, maxRate)).thenReturn(problems);
//
//            Problem result = matchService.selectProblem(userA, userB);
//
//            assertNotNull(result);
//            assertEquals(1L, result.getId());
//            verify(problemRepository).findProblemsInRateRange(minRate, maxRate);
//        }
//
//        @Test
//        @DisplayName("Should expand range when initial range is empty")
//        void test_selectProblem_expandsRangeWhenEmpty() {
//            User userA = User.builder().currentRate(1200).build();
//            User userB = User.builder().currentRate(1400).build();
//
//            Problem problem = new Problem();
//            problem.setId(2L);
//
//            int avgRate = 1300;
//            int initialMin = 1000;
//            int initialMax = 1600;
//
//            when(problemRepository.findProblemsInRateRange(initialMin, initialMax)).thenReturn(new ArrayList<>());
//            when(problemRepository.findProblemsInRateRange(900, 1700)).thenReturn(List.of(problem));
//
//            Problem result = matchService.selectProblem(userA, userB);
//
//            assertNotNull(result);
//            assertEquals(2L, result.getId());
//            verify(problemRepository, atLeast(2)).findProblemsInRateRange(anyInt(), anyInt());
//        }

        @Test
        @DisplayName("Should throw exception when no problems available")
        void test_selectProblem_throwsWhenNoProblemsAvailable() {
            User userA = User.builder().currentRate(1000).build();
            User userB = User.builder().currentRate(1000).build();

            when(problemRepository.findProblemsInRateRange(anyInt(), anyInt())).thenReturn(new ArrayList<>());

            assertThrows(IllegalStateException.class, () ->
                    matchService.selectProblem(userA, userB)
            );
        }

//        @Test
//        @DisplayName("Should cap min and max rates at boundaries")
//        void test_selectProblem_capsMinMaxRatesBoundaries() {
//            User userA = User.builder().currentRate(100).build();
//            User userB = User.builder().currentRate(100).build();
//
//            Problem problem = new Problem();
//            problem.setId(3L);
//
//            when(problemRepository.findProblemsInRateRange(0, 400)).thenReturn(List.of(problem));
//
//            Problem result = matchService.selectProblem(userA, userB);
//
//            assertNotNull(result);
//            verify(problemRepository).findProblemsInRateRange(0, 400);
//        }
//    }

    @Test
    void test_sendMatchInvite_success() {
        User sender = User.builder().id(1L).username("caro").build();
        User recipient = User.builder().id(2L).username("mina").build();

        when(userRepository.findByUsername("mina")).thenReturn(Optional.of(recipient));

        when(notificationService.send(
                eq(sender.getId()),
                eq(recipient.getId()),
                eq(recipient.getUsername()),
                any(MatchInvitationPayload.class)
        )).thenReturn(Optional.of(100L));

        Long result = matchService.sendMatchInvite(sender, "mina");

        assertNotNull(result);
        assertEquals(100L, result);

        verify(userRepository).findByUsername("mina");
        verify(notificationService).send(
                eq(sender.getId()),
                eq(recipient.getId()),
                eq(recipient.getUsername()),
                any(MatchInvitationPayload.class)
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

        lenient().when(matchNotificationMapper.mapSubmissionReceived(eq(match), eq(player)))
                .thenReturn(new MatchNotificationDto());
        lenient().when(matchNotificationMapper.mapSubmissionResult(eq(match), eq(submission)))
                .thenReturn(new MatchNotificationDto());
        lenient().when(matchNotificationMapper.mapMatchEnded(eq(match)))
                .thenReturn(new MatchNotificationDto());

        // Act
        spyService.submitCode(dto, player);

        // Assert
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
                                req.getUserRating() == 1500
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

    void test_getUserMatchHistory_success() {
        Long userId = 1L;
        Boolean rated = true;
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);

        MatchParticipant participant = new MatchParticipant();
        com.clashcode.backend.dto.MatchHistoryDto historyDto = new com.clashcode.backend.dto.MatchHistoryDto();

        org.springframework.data.domain.Page<MatchParticipant> pageEntity =
                new org.springframework.data.domain.PageImpl<>(List.of(participant));

        when(matchParticipantRepository.findHistoryByUserId(userId, rated, pageable))
                .thenReturn(pageEntity);
        when(matchMapper.toMatchHistoryDto(participant))
                .thenReturn(historyDto);

        org.springframework.data.domain.Page<com.clashcode.backend.dto.MatchHistoryDto> result =
                matchService.getUserMatchHistory(userId, rated, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(historyDto, result.getContent().getFirst());

        verify(matchParticipantRepository).findHistoryByUserId(userId, rated, pageable);
        verify(matchMapper).toMatchHistoryDto(participant);
    }

    @Nested
    @DisplayName("Submission Tests")
    class SubmissionTests {

//        @Test
//        @DisplayName("Should handle code submission and complete match on accepted")
//        void test_submitCode_callsNotificationsAndCompleteIfAccepted() {
//            User player = User.builder().id(1L).username("player").build();
//            User opponent = User.builder().id(2L).username("opponent").build();
//
//            MatchParticipant participant = MatchParticipant.builder().user(player).build();
//            MatchParticipant opponentParticipant = MatchParticipant.builder().user(opponent).build();
//
//            Match match = Match.builder()
//                    .id(10L)
//                    .matchState(MatchState.ONGOING)
//                    .gameMode(GameMode.UNRATED)
//                    .participants(List.of(participant, opponentParticipant))
//                    .build();
//
//            SubmissionRequestDto dto = new SubmissionRequestDto();
//            dto.setMatchId(10L);
//
//            Submission submission = new Submission();
//            submission.setStatus(SubmissionStatus.ACCEPTED);
//            submission.setNumberOfPassedTestCases(5);
//
//            MatchService spyService = Mockito.spy(matchService);
//            doReturn(match).when(spyService).validateMatch(dto.getMatchId(), player);
//            doReturn(submission).when(submissionService).submitCode(dto, player);
//            doReturn(new MatchNotificationDto()).when(matchNotificationMapper).mapSubmissionReceived(eq(match), eq(player));
//            doReturn(new MatchNotificationDto()).when(matchNotificationMapper).mapSubmissionResult(eq(match), eq(submission));
//            doReturn(new MatchNotificationDto()).when(matchNotificationMapper).mapMatchEnded(eq(match));
//
//            spyService.submitCode(dto, player);
//
//            verify(spyService).completeMatch(match, player);
//            verify(notificationService, atLeast(2)).send(any(), any(), any(), any());
//        }

        @Test
        @DisplayName("Should get match submission log")
        void test_getMatchSubmissionLog_returnsLogs() {
            User user = User.builder().id(1L).build();
            MatchParticipant participant = MatchParticipant.builder().user(user).build();
            Match match = Match.builder().id(100L).participants(List.of(participant)).build();
            Submission submission = new Submission();
            List<Submission> submissions = List.of(submission);

            when(matchRepository.findById(100L)).thenReturn(Optional.of(match));
            when(submissionRepository.findByUserIdAndMatchId(user.getId(), 100L)).thenReturn(submissions);
            when(matchMapper.toMatchSubmissionLogDto(participant, submissions)).thenReturn(new MatchSubmissionLogDto());

            List<MatchSubmissionLogDto> logs = matchService.getMatchSubmissionLog(100L);

            assertEquals(1, logs.size());
        }

        @Test
        @DisplayName("Should throw exception when match not found for submission log")
        void test_getMatchSubmissionLog_matchNotFound() {
            when(matchRepository.findById(100L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () ->
                    matchService.getMatchSubmissionLog(100L)
            );
        }
    }
}