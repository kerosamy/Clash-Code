package com.clashcode.backend.service;

import com.clashcode.backend.dto.CreateMatchRequestDto;
import com.clashcode.backend.dto.MatchResponseDto;
import com.clashcode.backend.mapper.MatchMapper;
import com.clashcode.backend.model.*;
import com.clashcode.backend.repository.MatchParticipantRepository;
import com.clashcode.backend.repository.MatchRepository;
import com.clashcode.backend.repository.ProblemRepository;
import com.clashcode.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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

    @InjectMocks
    private MatchService matchService;


    @Test
    void createMatch_success() {
        // Arrange
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

        // Act
        MatchResponseDto result = matchService.createMatch(reqDto);

        // Assert
        assertNotNull(result);
        assertEquals(matchId, result.getId());

        verify(matchRepository).save(matchBeforeSave);
        verify(matchParticipantRepository).saveAll(anyList());
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
}