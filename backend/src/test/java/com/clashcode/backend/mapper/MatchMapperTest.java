//package com.clashcode.backend.mapper;
//
//import com.clashcode.backend.dto.CreateMatchRequestDto;
//import com.clashcode.backend.dto.MatchResponseDto;
//import com.clashcode.backend.enums.GameMode;
//import com.clashcode.backend.enums.MatchState;
//import com.clashcode.backend.model.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class MatchMapperTest {
//
//    private MatchMapper matchMapper;
//
//    @Mock
//    private User user1;
//
//    @Mock
//    private User user2;
//
//    @Mock
//    private Problem problem;
//
//    @BeforeEach
//    void setUp() {
//        matchMapper = new MatchMapper();
//    }
//
//    @Test
//    void MapToMatchEntity() {
//        // Arrange
//        CreateMatchRequestDto dto = new CreateMatchRequestDto();
//        dto.setDuration(30);
//        dto.setGameMode(GameMode.UNRATED);
//
//        // Act
//        Match match = matchMapper.toMatchEntity(dto, problem);
//
//        // Assert
//        assertNotNull(match);
//        assertEquals(30, match.getDuration());
//        assertEquals(GameMode.UNRATED, match.getGameMode());
//        assertEquals(MatchState.ONGOING, match.getMatchState());
//        assertEquals(problem, match.getProblem());
//        assertNotNull(match.getStartAt());
//        assertNotNull(match.getParticipants());
//        assertTrue(match.getParticipants().isEmpty());
//    }
//
//    @Test
//    void CreateParticipant() {
//        // Arrange
//        Long userId = 10L;
//        Long matchId = 100L;
//        Integer currentRate = 1200;
//
//        when(user1.getId()).thenReturn(userId);
//        when(user1.getCurrentRate()).thenReturn(currentRate);
//
//        Match match = Match.builder().id(matchId).build();
//
//        // Act
//        MatchParticipant participant = matchMapper.createParticipant(user1, match);
//
//        // Assert
//        assertNotNull(participant);
//
//        assertNotNull(participant.getId(), "MatchParticipantId should not be null");
//        assertEquals(userId, participant.getId().getUserId());
//        assertEquals(matchId, participant.getId().getMatchId());
//
//        assertEquals(user1, participant.getUser());
//        assertEquals(match, participant.getMatch());
//        assertNull(participant.getRank());
//        assertEquals(0, participant.getRateChange());
//        assertEquals(currentRate, participant.getNewRating());
//    }
//
//    @Test
//    void MapToDto_Correctly() {
//        // Arrange
//        Long matchId = 100L;
//        Long problemId = 50L;
//        Long userId = 1L;
//        LocalDateTime now = LocalDateTime.now();
//
//        when(problem.getId()).thenReturn(problemId);
//        when(user1.getId()).thenReturn(userId);
//
//        Match match = Match.builder()
//                .id(matchId)
//                .startAt(now)
//                .duration(60)
//                .matchState(MatchState.COMPLETED)
//                .problem(problem)
//                .build();
//
//        MatchParticipant participant = MatchParticipant.builder()
//                .user(user1)
//                .match(match)
//                .rank(1)
//                .rateChange(10)
//                .newRating(1200)
//                .build();
//
//        match.setParticipants(List.of(participant));
//
//        // Act
//        MatchResponseDto responseDto = matchMapper.toResponseDto(match);
//
//        // Assert
//        assertNotNull(responseDto);
//        assertEquals(matchId, responseDto.getId());
//        assertEquals(now, responseDto.getStartAt());
//        assertEquals(60, responseDto.getDuration());
//        assertEquals(MatchState.COMPLETED, responseDto.getMatchState());
//        assertEquals(problemId, responseDto.getProblemId());
//        assertEquals(1, responseDto.getParticipants().size());
//        assertEquals(userId, responseDto.getParticipants().get(0).getUserId());
//        assertEquals(1, responseDto.getParticipants().get(0).getRank());
//        assertEquals(1200, responseDto.getParticipants().get(0).getNewRating());
//    }
//
//    @Test
//    void MapToDto_WithEmptyParticipants() {
//        // Arrange
//        when(problem.getId()).thenReturn(50L);
//
//        Match match = Match.builder()
//                .id(1L)
//                .startAt(LocalDateTime.now())
//                .duration(60)
//                .matchState(MatchState.ONGOING)
//                .problem(problem)
//                .participants(new ArrayList<>())
//                .build();
//
//        // Act
//        MatchResponseDto responseDto = matchMapper.toResponseDto(match);
//
//        // Assert
//        assertNotNull(responseDto);
//        assertNotNull(responseDto.getParticipants());
//        assertTrue(responseDto.getParticipants().isEmpty());
//    }
//}