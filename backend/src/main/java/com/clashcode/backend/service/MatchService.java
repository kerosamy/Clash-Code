package com.clashcode.backend.service;

import com.clashcode.backend.dto.CreateMatchRequestDto;
import com.clashcode.backend.dto.MatchResponseDto;
import com.clashcode.backend.mapper.MatchMapper;
import com.clashcode.backend.model.*;
import com.clashcode.backend.repository.MatchParticipantRepository;
import com.clashcode.backend.repository.MatchRepository;
import com.clashcode.backend.repository.ProblemRepository;
import com.clashcode.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MatchService {

    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final MatchRepository matchRepository;
    private final MatchParticipantRepository matchParticipantRepository;
    private final MatchMapper matchMapper;

    public MatchService(UserRepository userRepository, ProblemRepository problemRepository, MatchRepository matchRepository, MatchParticipantRepository matchParticipantRepository, MatchMapper matchMapper) {
        this.userRepository = userRepository;
        this.problemRepository = problemRepository;
        this.matchRepository = matchRepository;
        this.matchParticipantRepository = matchParticipantRepository;
        this.matchMapper = matchMapper;
    }

    public MatchResponseDto createMatch(CreateMatchRequestDto createMatchRequestDto) {
        User player1 = userRepository.findById(createMatchRequestDto.getPlayer1Id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player 1 does not exist"));

        User player2 = userRepository.findById(createMatchRequestDto.getPlayer2Id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player 2 does not exist"));

        Problem problem = problemRepository.findById(createMatchRequestDto.getProblemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem does not exist"));

        Match match = matchMapper.toMatchEntity(createMatchRequestDto, problem);
        Match savedMatch = matchRepository.save(match);

        MatchParticipant p1 = matchMapper.createParticipant(player1, savedMatch);
        MatchParticipant p2 = matchMapper.createParticipant(player2, savedMatch);

        List<MatchParticipant> participants = List.of(p1, p2);
        matchParticipantRepository.saveAll(participants);

        savedMatch.setParticipants(participants);
        return matchMapper.toResponseDto(savedMatch);
    }
}
