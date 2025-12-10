package com.clashcode.backend.service;

import com.clashcode.backend.dto.CreateMatchRequestDto;
import com.clashcode.backend.dto.MatchResponseDto;
import com.clashcode.backend.dto.MatchSubmissionLogDto;
import com.clashcode.backend.enums.MatchState;
import com.clashcode.backend.mapper.MatchMapper;
import com.clashcode.backend.mapper.RankMapper;
import com.clashcode.backend.model.*;
import com.clashcode.backend.repository.*;
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
    private final SubmissionRepository submissionRepository;
    private final RankMapper rankMapper;
    private final MatchScheduler matchScheduler;

    public MatchService(
            UserRepository userRepository,
            ProblemRepository problemRepository,
            MatchRepository matchRepository,
            MatchParticipantRepository matchParticipantRepository,
            MatchMapper matchMapper,
            SubmissionRepository submissionRepository,
            RankMapper rankMapper,
            MatchScheduler matchScheduler
    ) {
        this.userRepository = userRepository;
        this.problemRepository = problemRepository;
        this.matchRepository = matchRepository;
        this.matchParticipantRepository = matchParticipantRepository;
        this.matchMapper = matchMapper;
        this.submissionRepository = submissionRepository;
        this.rankMapper = rankMapper;
        this.matchScheduler = matchScheduler;
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
        matchScheduler.scheduleMatchEnd(savedMatch);
        return matchMapper.toResponseDto(savedMatch);
    }

    public Match validateMatch(Long matchId, User user) {
        if (matchId == null) return null;
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));

        if (match.getMatchState() != MatchState.ONGOING) {
            throw new IllegalStateException("Match is not ongoing, submissions are not allowed");
        }

        boolean isParticipant = match.getParticipants().stream()
                .anyMatch(mp -> mp.getUser().getId().equals(user.getId()));

        if (!isParticipant) {
            throw new IllegalArgumentException(
                    "User " + user.getUsername() + " (ID: " + user.getId() + ") is not a participant in match " + matchId
            );
        }
        return match;
    }

    public List<MatchSubmissionLogDto> getMatchSubmissionLog(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));

        return match.getParticipants().stream()
                .map(particicpant -> {
                    List<Submission> submissions = submissionRepository
                            .findByUserIdAndMatchId(particicpant.getUser().getId(), matchId);
                    return matchMapper.toMatchSubmissionLogDto(particicpant, submissions);
                })
                .toList();
    }

    public void completeMatch(Match match, User winner) {
        if (match.getMatchState() == MatchState.COMPLETED) return;

        MatchParticipant winnerParticipant = null;
        MatchParticipant loserParticipant = null;

        for (MatchParticipant mp : match.getParticipants()) {
            if (winner != null && mp.getUser().getId().equals(winner.getId())) {
                winnerParticipant = mp;
            } else {
                loserParticipant = mp;
            }
        }

        if (winnerParticipant != null && loserParticipant != null) {
            winnerParticipant.setRank(rankMapper.toRank("winner"));
            loserParticipant.setRank(rankMapper.toRank("loser"));
        }

        match.setMatchState(MatchState.COMPLETED);
        matchRepository.save(match);
        matchParticipantRepository.saveAll(match.getParticipants());
    }

    public void resignMatch(Long matchId, User resigningUser) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));

        if (match.getMatchState() != MatchState.ONGOING) {
            throw new IllegalStateException("Cannot resign a completed match");
        }

        MatchParticipant winnerParticipant = match.getParticipants().stream()
                .filter(mp -> !mp.getUser().getId().equals(resigningUser.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Other participant not found"));

        completeMatch(match, winnerParticipant.getUser());
    }
}
