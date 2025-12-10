package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.CreateMatchRequestDto;
import com.clashcode.backend.dto.MatchParticipantDto;
import com.clashcode.backend.dto.MatchResponseDto;
import com.clashcode.backend.enums.GameMode;
import com.clashcode.backend.enums.MatchState;
import com.clashcode.backend.model.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MatchMapper {

    public MatchResponseDto toResponseDto(Match match) {
        return MatchResponseDto.builder()
                .id(match.getId())
                .startAt(match.getStartAt())
                .duration(match.getDuration())
                .matchState(match.getMatchState())
                .problemId(match.getProblem().getId())
                .participants(
                        match.getParticipants().stream()
                                .map(this::mapParticipant)
                                .collect(Collectors.toList())
                )
                .build();
    }

    private MatchParticipantDto mapParticipant(MatchParticipant matchParticipant) {
        return MatchParticipantDto.builder()
                .userId(matchParticipant.getUser().getId())
                .rank(matchParticipant.getRank())
                .rateChange(matchParticipant.getRateChange())
                .newRating(matchParticipant.getNewRating())
                .build();
    }

    public Match toMatchEntity(CreateMatchRequestDto dto, Problem problem) {
        return Match.builder()
                .duration(dto.getDuration())
                .gameMode(GameMode.valueOf(dto.getGameMode().name()))
                .matchState(MatchState.ONGOING)
                .problem(problem)
                .build();
    }

    public MatchParticipant createParticipant(User user, Match match) {
        MatchParticipantId id = new MatchParticipantId(user.getId(), match.getId());

        return MatchParticipant.builder()
                .id(id)
                .user(user)
                .match(match)
                .rank(null)
                .rateChange(0)
                .newRating(user.getCurrentRate())
                .build();
    }
}
