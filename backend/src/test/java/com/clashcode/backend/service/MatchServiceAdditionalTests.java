package com.clashcode.backend.service;

import com.clashcode.backend.Notification.Dtos.MatchNotificationDto;
import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.GameMode;
import com.clashcode.backend.enums.MatchState;
import com.clashcode.backend.exception.UserNotFoundException;
import com.clashcode.backend.mapper.MatchMapper;
import com.clashcode.backend.mapper.MatchNotificationMapper;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceAdditionalTests {

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

    // ==================== RESIGN MATCH TESTS ====================

    @Nested
    @DisplayName("resignMatch Tests")
    class ResignMatchTests {

        @Test
        @DisplayName("Should throw exception when match not found")
        void test_resignMatch_matchNotFound() {
            Long matchId = 999L;
            User resigningUser = User.builder().id(1L).build();

            when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () ->
                    matchService.resignMatch(matchId, resigningUser)
            );

            verify(matchRepository).findById(matchId);
        }

        @Test
        @DisplayName("Should throw exception when match is already completed")
        void test_resignMatch_alreadyCompleted() {
            Long matchId = 100L;
            User resigningUser = User.builder().id(1L).build();

            Match match = Match.builder()
                    .id(matchId)
                    .matchState(MatchState.COMPLETED)
                    .build();

            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                    matchService.resignMatch(matchId, resigningUser)
            );

            assertTrue(exception.getMessage().contains("Cannot resign a completed match"));
        }

        @Test
        @DisplayName("Should throw exception when opponent participant not found")
        void test_resignMatch_opponentNotFound() {
            Long matchId = 100L;
            User resigningUser = User.builder().id(1L).build();

            MatchParticipant resigningParticipant = MatchParticipant.builder()
                    .user(resigningUser)
                    .build();

            Match match = Match.builder()
                    .id(matchId)
                    .matchState(MatchState.ONGOING)
                    .participants(List.of(resigningParticipant))
                    .build();

            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

            assertThrows(IllegalArgumentException.class, () ->
                    matchService.resignMatch(matchId, resigningUser)
            );
        }
    }

    // ==================== GET MATCH PROBLEM TESTS ====================

    @Nested
    @DisplayName("getMatchProblem Tests")
    class GetMatchProblemTests {

        @Test
        @DisplayName("Should get match problem successfully")
        void test_getMatchProblem_success() {
            Long matchId = 100L;
            Long problemId = 50L;

            Problem problem = new Problem();
            problem.setId(problemId);

            Match match = Match.builder()
                    .id(matchId)
                    .problem(problem)
                    .build();

            PartialProblemResponseDto expectedDto = PartialProblemResponseDto.builder()
                    .id(problemId)
                    .build();

            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
            when(problemService.getPartialProblemById(problemId)).thenReturn(expectedDto);

            PartialProblemResponseDto result = matchService.getMatchProblem(matchId);

            assertNotNull(result);
            assertEquals(problemId, result.getId());
            verify(matchRepository).findById(matchId);
            verify(problemService).getPartialProblemById(problemId);
        }

        @Test
        @DisplayName("Should throw exception when match not found")
        void test_getMatchProblem_matchNotFound() {
            Long matchId = 999L;

            when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                    matchService.getMatchProblem(matchId)
            );

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertTrue(exception.getReason().contains("Match not found"));
        }
    }

    // ==================== GET MATCH DETAILS TESTS ====================

    @Nested
    @DisplayName("getMatchDetails Tests")
    class GetMatchDetailsTests {

        @Test
        @DisplayName("Should get match details successfully")
        void test_getMatchDetails_success() {
            Long matchId = 100L;

            Match match = Match.builder()
                    .id(matchId)
                    .matchState(MatchState.ONGOING)
                    .duration(30)
                    .build();

            MatchResponseDto expectedDto = MatchResponseDto.builder()
                    .id(matchId)
                    .build();

            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
            when(matchMapper.toResponseDto(match)).thenReturn(expectedDto);

            MatchResponseDto result = matchService.getMatchDetails(matchId);

            assertNotNull(result);
            assertEquals(matchId, result.getId());
            verify(matchRepository).findById(matchId);
            verify(matchMapper).toResponseDto(match);
        }

        @Test
        @DisplayName("Should throw exception when match not found")
        void test_getMatchDetails_matchNotFound() {
            Long matchId = 999L;

            when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    matchService.getMatchDetails(matchId)
            );

            assertTrue(exception.getMessage().contains("Match not found with ID"));
        }
    }

    // ==================== GET MATCH RESULTS TESTS ====================

    @Nested
    @DisplayName("getMatchResults Tests")
    class GetMatchResultsTests {

        @Test
        @DisplayName("Should get match results for rated match")
        void test_getMatchResults_ratedMatch() {
            Long matchId = 100L;
            User user = User.builder().id(1L).username("player1").build();

            MatchParticipant participant = MatchParticipant.builder()
                    .user(user)
                    .rank(1)
                    .rateChange(25)
                    .newRating(1225)
                    .build();

            Match match = Match.builder()
                    .id(matchId)
                    .gameMode(GameMode.RATED)
                    .participants(List.of(participant))
                    .build();

            MatchResultDto expectedDto = MatchResultDto.builder()
                    .rank(1)
                    .rateChange(25)
                    .build();

            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
            when(matchMapper.toMatchResultDto(true, participant)).thenReturn(expectedDto);

            MatchResultDto result = matchService.getMatchResults(matchId, user);

            assertNotNull(result);
            assertEquals(1, result.getRank());
            assertEquals(25, result.getRateChange());
            verify(matchMapper).toMatchResultDto(true, participant);
        }

        @Test
        @DisplayName("Should get match results for unrated match")
        void test_getMatchResults_unratedMatch() {
            Long matchId = 100L;
            User user = User.builder().id(1L).username("player1").build();

            MatchParticipant participant = MatchParticipant.builder()
                    .user(user)
                    .rank(2)
                    .build();

            Match match = Match.builder()
                    .id(matchId)
                    .gameMode(GameMode.UNRATED)
                    .participants(List.of(participant))
                    .build();

            MatchResultDto expectedDto = MatchResultDto.builder()
                    .rank(2)
                    .build();

            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
            when(matchMapper.toMatchResultDto(false, participant)).thenReturn(expectedDto);

            MatchResultDto result = matchService.getMatchResults(matchId, user);

            assertNotNull(result);
            assertEquals(2, result.getRank());
            verify(matchMapper).toMatchResultDto(false, participant);
        }

        @Test
        @DisplayName("Should throw exception when match not found")
        void test_getMatchResults_matchNotFound() {
            Long matchId = 999L;
            User user = User.builder().id(1L).build();

            when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    matchService.getMatchResults(matchId, user)
            );

            assertTrue(exception.getMessage().contains("Match not found with ID"));
        }

        @Test
        @DisplayName("Should throw exception when user is not a participant")
        void test_getMatchResults_userNotParticipant() {
            Long matchId = 100L;
            User user = User.builder().id(1L).build();
            User otherUser = User.builder().id(2L).build();

            MatchParticipant participant = MatchParticipant.builder()
                    .user(otherUser)
                    .build();

            Match match = Match.builder()
                    .id(matchId)
                    .participants(List.of(participant))
                    .build();

            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    matchService.getMatchResults(matchId, user)
            );

            assertTrue(exception.getMessage().contains("User is not a participant"));
        }
    }

    // ==================== START RATED MATCH TESTS ====================

    @Nested
    @DisplayName("startRatedMatch Tests")
    class StartRatedMatchTests {

//        @Test
//        @DisplayName("Should start rated match successfully")
//        void test_startRatedMatch_success() {
//            Long playerIdA = 1L;
//            Long playerIdB = 2L;
//
//            MatchCreationDto dto = new MatchCreationDto();
//            dto.setPlayerIdA(playerIdA);
//            dto.setPlayerIdB(playerIdB);
//
//            User userA = User.builder()
//                    .id(playerIdA)
//                    .currentRate(1200)
//                    .build();
//
//            User userB = User.builder()
//                    .id(playerIdB)
//                    .currentRate(1300)
//                    .build();
//
//            Problem problem = new Problem();
//            problem.setId(10L);
//            problem.setRate(1250);
//
//            when(userRepository.findById(playerIdA)).thenReturn(Optional.of(userA));
//            when(userRepository.findById(playerIdB)).thenReturn(Optional.of(userB));
//
//            MatchService spyService = Mockito.spy(matchService);
//            doReturn(problem).when(spyService).selectProblem(userA, userB);
//            doReturn(new MatchResponseDto()).when(spyService)
//                    .createMatch(userA, userB, problem, 15, GameMode.RATED);
//
//            spyService.startRatedMatch(dto);
//
//            verify(userRepository).findById(playerIdA);
//            verify(userRepository).findById(playerIdB);
//            verify(spyService).selectProblem(userA, userB);
//            verify(spyService).createMatch(userA, userB, problem, 15, GameMode.RATED);
//        }

        @Test
        @DisplayName("Should throw exception when player A not found")
        void test_startRatedMatch_playerANotFound() {
            MatchCreationDto dto = new MatchCreationDto();
            dto.setPlayerIdA(1L);
            dto.setPlayerIdB(2L);

            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    matchService.startRatedMatch(dto)
            );

            assertTrue(exception.getMessage().contains("Player not found"));
        }

        @Test
        @DisplayName("Should throw exception when player B not found")
        void test_startRatedMatch_playerBNotFound() {
            MatchCreationDto dto = new MatchCreationDto();
            dto.setPlayerIdA(1L);
            dto.setPlayerIdB(2L);

            User userA = User.builder().id(1L).build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(userA));
            when(userRepository.findById(2L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    matchService.startRatedMatch(dto)
            );

            assertTrue(exception.getMessage().contains("Player not found"));
        }
    }

    // ==================== SEARCH FOR OPPONENT TESTS ====================

    @Nested
    @DisplayName("searchForOpponent Tests")
    class SearchForOpponentTests {

        @Test
        @DisplayName("Should request matching successfully")
        void test_searchForOpponent_success() {
            User user = User.builder()
                    .id(1L)
                    .username("player1")
                    .currentRate(1200)
                    .build();

            ArgumentCaptor<MatchingRequestDto> captor = ArgumentCaptor.forClass(MatchingRequestDto.class);

            matchService.searchForOpponent(user);

            verify(matchingServiceClient).requestMatching(captor.capture());

            MatchingRequestDto capturedDto = captor.getValue();
            assertEquals(user.getId(), capturedDto.getUserId());
            assertEquals(user.getCurrentRate(), capturedDto.getUserRating());
        }

        @Test
        @DisplayName("Should handle user with different ratings")
        void test_searchForOpponent_differentRatings() {
            User user = User.builder()
                    .id(5L)
                    .username("player5")
                    .currentRate(1800)
                    .build();

            ArgumentCaptor<MatchingRequestDto> captor = ArgumentCaptor.forClass(MatchingRequestDto.class);

            matchService.searchForOpponent(user);

            verify(matchingServiceClient).requestMatching(captor.capture());

            MatchingRequestDto capturedDto = captor.getValue();
            assertEquals(5L, capturedDto.getUserId());
            assertEquals(1800, capturedDto.getUserRating());
        }
    }

    // ==================== CANCEL SEARCH FOR OPPONENT TESTS ====================

    @Nested
    @DisplayName("cancelSearchForOpponent Tests")
    class CancelSearchForOpponentTests {

        @Test
        @DisplayName("Should cancel matching request successfully")
        void test_cancelSearchForOpponent_success() {
            User user = User.builder()
                    .id(1L)
                    .username("player1")
                    .build();

            matchService.cancelSearchForOpponent(user);

            verify(matchingServiceClient).deleteMatching(user.getId());
        }

        @Test
        @DisplayName("Should cancel matching for different user")
        void test_cancelSearchForOpponent_differentUser() {
            User user = User.builder()
                    .id(999L)
                    .username("player999")
                    .build();

            matchService.cancelSearchForOpponent(user);

            verify(matchingServiceClient).deleteMatching(999L);
        }
    }

    // ==================== GET USER MATCH HISTORY TESTS ====================

    @Nested
    @DisplayName("getUserMatchHistory Tests")
    class GetUserMatchHistoryTests {

        @Test
        @DisplayName("Should get user match history for rated matches")
        void test_getUserMatchHistory_ratedMatches() {
            Long userId = 1L;
            Boolean rated = true;
            Pageable pageable = PageRequest.of(0, 10);

            User user = User.builder().id(userId).username("player1").build();
            MatchParticipant participant = MatchParticipant.builder()
                    .user(user)
                    .rank(1)
                    .build();

            Page<MatchParticipant> participantPage = new PageImpl<>(List.of(participant));
            MatchHistoryDto historyDto = new MatchHistoryDto();

            when(matchParticipantRepository.findHistoryByUserId(userId, rated, pageable))
                    .thenReturn(participantPage);
            when(matchMapper.toMatchHistoryDto(participant)).thenReturn(historyDto);

            Page<MatchHistoryDto> result = matchService.getUserMatchHistory(userId, rated, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(matchParticipantRepository).findHistoryByUserId(userId, rated, pageable);
        }

        @Test
        @DisplayName("Should get user match history for all matches (null rated filter)")
        void test_getUserMatchHistory_allMatches() {
            Long userId = 1L;
            Boolean rated = null;
            Pageable pageable = PageRequest.of(0, 10);

            User user = User.builder().id(userId).build();
            MatchParticipant participant1 = MatchParticipant.builder().user(user).rank(1).build();
            MatchParticipant participant2 = MatchParticipant.builder().user(user).rank(2).build();

            Page<MatchParticipant> participantPage = new PageImpl<>(List.of(participant1, participant2));

            when(matchParticipantRepository.findHistoryByUserId(userId, rated, pageable))
                    .thenReturn(participantPage);
            when(matchMapper.toMatchHistoryDto(any(MatchParticipant.class)))
                    .thenReturn(new MatchHistoryDto());

            Page<MatchHistoryDto> result = matchService.getUserMatchHistory(userId, rated, pageable);

            assertNotNull(result);
            assertEquals(2, result.getTotalElements());
            verify(matchMapper, times(2)).toMatchHistoryDto(any(MatchParticipant.class));
        }

        @Test
        @DisplayName("Should get user match history for unrated matches")
        void test_getUserMatchHistory_unratedMatches() {
            Long userId = 2L;
            Boolean rated = false;
            Pageable pageable = PageRequest.of(0, 5);

            Page<MatchParticipant> emptyPage = new PageImpl<>(new ArrayList<>());

            when(matchParticipantRepository.findHistoryByUserId(userId, rated, pageable))
                    .thenReturn(emptyPage);

            Page<MatchHistoryDto> result = matchService.getUserMatchHistory(userId, rated, pageable);

            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
            verify(matchParticipantRepository).findHistoryByUserId(userId, rated, pageable);
        }

        @Test
        @DisplayName("Should handle pagination correctly")
        void test_getUserMatchHistory_pagination() {
            Long userId = 1L;
            Boolean rated = true;
            Pageable pageable = PageRequest.of(1, 5); // Second page

            User user = User.builder().id(userId).build();
            List<MatchParticipant> participants = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                participants.add(MatchParticipant.builder().user(user).rank(i).build());
            }

            Page<MatchParticipant> participantPage = new PageImpl<>(participants, pageable, 10);

            when(matchParticipantRepository.findHistoryByUserId(userId, rated, pageable))
                    .thenReturn(participantPage);
            when(matchMapper.toMatchHistoryDto(any(MatchParticipant.class)))
                    .thenReturn(new MatchHistoryDto());

            Page<MatchHistoryDto> result = matchService.getUserMatchHistory(userId, rated, pageable);

            assertNotNull(result);
            assertEquals(3, result.getNumberOfElements());
            assertEquals(10, result.getTotalElements());
            assertEquals(1, result.getNumber());
            verify(matchMapper, times(3)).toMatchHistoryDto(any(MatchParticipant.class));
        }
    }

    // ==================== GET ONGOING MATCH TESTS ====================

    @Nested
    @DisplayName("getOnGoingMatch Tests")
    class GetOnGoingMatchTests {

        @Test
        @DisplayName("Should return ongoing match ID when user has one")
        void test_getOnGoingMatch_matchExists() {
            User user = User.builder().id(1L).username("player1").build();
            Long expectedMatchId = 100L;

            when(matchParticipantRepository.getOnGoingMatchByUserId(user.getId(), MatchState.ONGOING))
                    .thenReturn(Optional.of(expectedMatchId));

            Optional<Long> result = matchService.getOnGoingMatch(user);

            assertTrue(result.isPresent());
            assertEquals(expectedMatchId, result.get());
            verify(matchParticipantRepository).getOnGoingMatchByUserId(user.getId(), MatchState.ONGOING);
        }

        @Test
        @DisplayName("Should return empty optional when user has no ongoing match")
        void test_getOnGoingMatch_noMatch() {
            User user = User.builder().id(2L).username("player2").build();

            when(matchParticipantRepository.getOnGoingMatchByUserId(user.getId(), MatchState.ONGOING))
                    .thenReturn(Optional.empty());

            Optional<Long> result = matchService.getOnGoingMatch(user);

            assertFalse(result.isPresent());
            verify(matchParticipantRepository).getOnGoingMatchByUserId(user.getId(), MatchState.ONGOING);
        }

        @Test
        @DisplayName("Should handle different users correctly")
        void test_getOnGoingMatch_differentUsers() {
            User user1 = User.builder().id(1L).build();
            User user2 = User.builder().id(2L).build();

            when(matchParticipantRepository.getOnGoingMatchByUserId(1L, MatchState.ONGOING))
                    .thenReturn(Optional.of(100L));
            when(matchParticipantRepository.getOnGoingMatchByUserId(2L, MatchState.ONGOING))
                    .thenReturn(Optional.empty());

            Optional<Long> result1 = matchService.getOnGoingMatch(user1);
            Optional<Long> result2 = matchService.getOnGoingMatch(user2);

            assertTrue(result1.isPresent());
            assertEquals(100L, result1.get());
            assertFalse(result2.isPresent());
        }
    }
}