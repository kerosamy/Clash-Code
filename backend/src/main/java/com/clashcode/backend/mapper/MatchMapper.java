package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.GameMode;
import com.clashcode.backend.enums.MatchState;
import com.clashcode.backend.model.*;
import com.clashcode.backend.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MatchMapper {
    private final UserService userService;
    public MatchMapper(UserService userService) {
        this.userService = userService;
    }

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

    public SubmissionLogEntryDto toSubmissionLogDto(Submission submission) {
        return SubmissionLogEntryDto.builder()
                .submissionId(submission.getId())
                .status(submission.getStatus() != null ? submission.getStatus().toString() : "UNKNOWN")
                .submittedAt(submission.getSubmittedAt() != null ? submission.getSubmittedAt().toString() : "")
                .numberOfPassedTestCases(submission.getNumberOfPassedTestCases() !=null ? submission.getNumberOfPassedTestCases() : 0)
                .numberOfTotalTestCases(submission.getNumberOfTestCases() !=null ? submission.getNumberOfTestCases() : 0)
                .numberOfCurrentTestCase(submission.getNumberOfCurrentTestCase() !=null ? submission.getNumberOfCurrentTestCase() : 0)
                .build();
    }

    public MatchSubmissionLogDto toMatchSubmissionLogDto(MatchParticipant participant, List<Submission> submissions) {
        List<SubmissionLogEntryDto> submissionsLog = submissions.stream()
                .map(this::toSubmissionLogDto)
                .toList();

        return MatchSubmissionLogDto.builder()
                .username(participant.getUser().getUsername())
                .avatarUrl(userService.buildImageUrl(participant.getUser().getImgUrl()))
                .rank(userService.getRank(participant.getUser().getCurrentRate()))
                .submissions(submissionsLog)
                .build();
    }

    public MatchResultDto toMatchResultDto(boolean isRated,  MatchParticipant participant) {
        return MatchResultDto.builder()
                .isRated(isRated)
                .username(participant.getUser().getUsername())
                .avatarUrl(userService.buildImageUrl(participant.getUser().getImgUrl()))
                .rank(participant.getRank())
                .rateChange(participant.getRateChange())
                .newRating(participant.getNewRating())
                .build();
    }

    public MatchHistoryDto toMatchHistoryDto(MatchParticipant mp) {
        Match match = mp.getMatch();
        Long currentUserId = mp.getUser().getId();

        String opponentName = match.getParticipants().stream()
                .filter(p -> !p.getUser().getId().equals(currentUserId))
                .map(p -> p.getUser().getUsername())
                .findFirst()
                .orElse("Unknown");

        boolean isRated = match.getGameMode() == GameMode.RATED;

        return MatchHistoryDto.builder()
                .matchId(match.getId()) 
                .time(match.getStartAt())
                .opponent(opponentName)
                .problem(match.getProblem().getTitle()) 
                .rank(mp.getRank())     
                .rateChange(mp.getRateChange())
                .newRating(mp.getNewRating())
                .isRated(isRated)
                .build();
    }
}
