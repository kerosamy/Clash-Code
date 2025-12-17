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

    @Mock
    private ProfileDto profileDto;

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
        when(userService.getUserProfile(user1.getUsername())).thenReturn(profileDto);

        MatchParticipant participant = MatchParticipant.builder()
                .user(user1)
                .build();

        Submission submission1 = Submission.builder().id(1L).build();
        Submission submission2 = Submission.builder().id(2L).build();

        MatchSubmissionLogDto dto = matchMapper.toMatchSubmissionLogDto(participant, List.of(submission1, submission2));

        assertEquals(profileDto, dto.getProfile());
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
}
