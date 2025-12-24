package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.GameMode;
import com.clashcode.backend.enums.MatchState;
import com.clashcode.backend.model.*;
import com.clashcode.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchMapperTest {

    private MatchMapper matchMapper;

    @Mock
    private UserService userService;

    @Mock
    private User user1;

    @Mock
    private Problem problem;

    @BeforeEach
    void setUp() {
        matchMapper = new MatchMapper(userService);
    }

    @Test
    void test_toMatchEntity() {
        CreateMatchRequestDto dto = new CreateMatchRequestDto();
        dto.setDuration(30);
        dto.setGameMode(GameMode.UNRATED);

        Match match = matchMapper.toMatchEntity(dto, problem);

        assertNotNull(match);
        assertEquals(30, match.getDuration());
        assertEquals(GameMode.UNRATED, match.getGameMode());
        assertEquals(MatchState.ONGOING, match.getMatchState());
        assertEquals(problem, match.getProblem());
    }

    @Test
    void test_createParticipant() {
        Long userId = 10L;
        Long matchId = 100L;
        Integer currentRate = 1200;

        when(user1.getId()).thenReturn(userId);
        when(user1.getCurrentRate()).thenReturn(currentRate);

        Match match = Match.builder().id(matchId).build();

        MatchParticipant participant = matchMapper.createParticipant(user1, match);

        assertNotNull(participant);
        assertNotNull(participant.getId());
        assertEquals(userId, participant.getId().getUserId());
        assertEquals(matchId, participant.getId().getMatchId());
        assertEquals(user1, participant.getUser());
        assertEquals(match, participant.getMatch());
        assertNull(participant.getRank());
        assertEquals(0, participant.getRateChange());
        assertEquals(currentRate, participant.getNewRating());
    }

    @Test
    void test_toResponseDto() {
        Long matchId = 100L;
        Long problemId = 50L;
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();

        when(problem.getId()).thenReturn(problemId);
        when(user1.getId()).thenReturn(userId);

        Match match = Match.builder()
                .id(matchId)
                .startAt(now)
                .duration(60)
                .matchState(MatchState.COMPLETED)
                .problem(problem)
                .build();

        MatchParticipant participant = MatchParticipant.builder()
                .user(user1)
                .match(match)
                .rank(1)
                .rateChange(10)
                .newRating(1200)
                .build();

        match.setParticipants(List.of(participant));

        MatchResponseDto responseDto = matchMapper.toResponseDto(match);

        assertNotNull(responseDto);
        assertEquals(matchId, responseDto.getId());
        assertEquals(now, responseDto.getStartAt());
        assertEquals(60, responseDto.getDuration());
        assertEquals(MatchState.COMPLETED, responseDto.getMatchState());
        assertEquals(problemId, responseDto.getProblemId());
        assertEquals(1, responseDto.getParticipants().size());

        MatchParticipantDto dto = responseDto.getParticipants().get(0);
        assertEquals(userId, dto.getUserId());
        assertEquals(1, dto.getRank());
        assertEquals(1200, dto.getNewRating());
        assertEquals(10, dto.getRateChange());
    }

    @Test
    void test_toResponseDto_withEmptyParticipants() {
        when(problem.getId()).thenReturn(50L);

        Match match = Match.builder()
                .id(1L)
                .startAt(LocalDateTime.now())
                .duration(60)
                .matchState(MatchState.ONGOING)
                .problem(problem)
                .participants(List.of())
                .build();

        MatchResponseDto responseDto = matchMapper.toResponseDto(match);

        assertNotNull(responseDto);
        assertNotNull(responseDto.getParticipants());
        assertTrue(responseDto.getParticipants().isEmpty());
    }

    @Test
    void test_toSubmissionLogDto() {
        Submission submission = Submission.builder()
                .id(1L)
                .submittedAt(LocalDateTime.now())
                .numberOfPassedTestCases(3)
                .numberOfTestCases(5)
                .numberOfCurrentTestCase(2)
                .build();

        SubmissionLogEntryDto dto = matchMapper.toSubmissionLogDto(submission);

        assertEquals(1L, dto.getSubmissionId());
        assertEquals(3, dto.getNumberOfPassedTestCases());
        assertEquals(5, dto.getNumberOfTotalTestCases());
        assertEquals(2, dto.getNumberOfCurrentTestCase());
        assertNotNull(dto.getSubmittedAt());
    }

    @Test
    void test_toMatchSubmissionLogDto() {
        String username = "kero";
        String avatarUrl = "avatar.png";
        int rank = 1200;

        when(user1.getUsername()).thenReturn(username);
        when(user1.getCurrentRate()).thenReturn(rank);
        when(user1.getImgUrl()).thenReturn(avatarUrl);

        when(userService.buildImageUrl(avatarUrl)).thenReturn("http://localhost/avatars/" + avatarUrl);
        when(userService.getRank(rank)).thenReturn("BRONZE");

        MatchParticipant participant = MatchParticipant.builder()
                .user(user1)
                .build();

        Submission submission1 = Submission.builder().id(1L).build();
        Submission submission2 = Submission.builder().id(2L).build();

        MatchSubmissionLogDto dto = matchMapper.toMatchSubmissionLogDto(participant, List.of(submission1, submission2));

        assertEquals(username, dto.getUsername());
        assertEquals("http://localhost/avatars/" + avatarUrl, dto.getAvatarUrl());
        assertEquals("BRONZE", dto.getRank());

        assertEquals(2, dto.getSubmissions().size());
        assertEquals(1L, dto.getSubmissions().get(0).getSubmissionId());
        assertEquals(2L, dto.getSubmissions().get(1).getSubmissionId());
    }


    @Test
    void test_toSubmissionLogDto_handlesNullValues() {
        Submission submission = Submission.builder()
                .id(10L)
                .status(null)
                .submittedAt(null)
                .numberOfPassedTestCases(null)
                .numberOfTestCases(null)
                .numberOfCurrentTestCase(null)
                .build();

        SubmissionLogEntryDto dto = matchMapper.toSubmissionLogDto(submission);

        assertEquals("UNKNOWN", dto.getStatus());
        assertEquals("", dto.getSubmittedAt());
        assertEquals(0, dto.getNumberOfPassedTestCases());
        assertEquals(0, dto.getNumberOfTotalTestCases());
        assertEquals(0, dto.getNumberOfCurrentTestCase());
    }

    @Test
    void test_toMatchResultDto() {
        boolean isRated = true;
        String username = "TestUser";
        String rawImgUrl = "raw.png";
        String fullImgUrl = "http://api.com/raw.png";

        when(user1.getUsername()).thenReturn(username);
        when(user1.getImgUrl()).thenReturn(rawImgUrl);
        when(userService.buildImageUrl(rawImgUrl)).thenReturn(fullImgUrl);

        MatchParticipant participant = MatchParticipant.builder()
                .user(user1)
                .rank(1)
                .rateChange(25)
                .newRating(1500)
                .build();

        MatchResultDto resultDto = matchMapper.toMatchResultDto(isRated, participant);

        assertNotNull(resultDto);
        assertTrue(resultDto.isRated());
        assertEquals(username, resultDto.getUsername());
        assertEquals(fullImgUrl, resultDto.getAvatarUrl());
        assertEquals(1, resultDto.getRank());
        assertEquals(25, resultDto.getRateChange());
        assertEquals(1500, resultDto.getNewRating());
    }

    @Test
    void test_toMatchHistoryDto() {
        User currentUser = mock(User.class);
        User opponentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(1L);
        when(opponentUser.getId()).thenReturn(2L);
        when(opponentUser.getUsername()).thenReturn("OpponentUser");

        Problem problem = mock(Problem.class);
        when(problem.getTitle()).thenReturn("ProblemTitle");

        Match match = Match.builder()
                .id(100L)
                .startAt(LocalDateTime.of(2025, 12, 24, 12, 0))
                .gameMode(GameMode.RATED)
                .problem(problem)
                .build();

        MatchParticipant currentParticipant = MatchParticipant.builder()
                .user(currentUser)
                .match(match)
                .rank(1)
                .rateChange(20)
                .newRating(1500)
                .build();

        MatchParticipant opponentParticipant = MatchParticipant.builder()
                .user(opponentUser)
                .match(match)
                .rank(2)
                .rateChange(-20)
                .newRating(1480)
                .build();

        match.setParticipants(List.of(currentParticipant, opponentParticipant));

        MatchHistoryDto dto = matchMapper.toMatchHistoryDto(currentParticipant);

        assertNotNull(dto);
        assertEquals(100L, dto.getMatchId());
        assertEquals(LocalDateTime.of(2025, 12, 24, 12, 0), dto.getTime());
        assertEquals("OpponentUser", dto.getOpponent());
        assertEquals("ProblemTitle", dto.getProblem());
        assertEquals(1, dto.getRank());
        assertEquals(20, dto.getRateChange());
        assertEquals(1500, dto.getNewRating());
        assertTrue(dto.isRated());
    }
}
