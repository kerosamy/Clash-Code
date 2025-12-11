package com.clashcode.backend.service;

import com.clashcode.backend.dto.MatchResponseDto;
import com.clashcode.backend.dto.MatchSubmissionLogDto;
import com.clashcode.backend.dto.SubmissionRequestDto;
import com.clashcode.backend.enums.GameMode;
import com.clashcode.backend.enums.MatchState;
import com.clashcode.backend.enums.NotificationType;
import com.clashcode.backend.enums.SubmissionStatus;
import com.clashcode.backend.exception.UnauthorizedException;
import com.clashcode.backend.exception.UserNotFoundException;
import com.clashcode.backend.mapper.MatchMapper;
import com.clashcode.backend.mapper.RankMapper;
import com.clashcode.backend.model.*;
import com.clashcode.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

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
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final SubmissionService submissionService;

    public MatchService(
            UserRepository userRepository,
            ProblemRepository problemRepository,
            MatchRepository matchRepository,
            MatchParticipantRepository matchParticipantRepository,
            MatchMapper matchMapper,
            SubmissionRepository submissionRepository,
            RankMapper rankMapper,
            MatchScheduler matchScheduler,
            NotificationService notificationService, NotificationRepository notificationRepository,
            SubmissionService submissionService
    ) {
        this.userRepository = userRepository;
        this.problemRepository = problemRepository;
        this.matchRepository = matchRepository;
        this.matchParticipantRepository = matchParticipantRepository;
        this.matchMapper = matchMapper;
        this.submissionRepository = submissionRepository;
        this.rankMapper = rankMapper;
        this.matchScheduler = matchScheduler;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
        this.submissionService = submissionService;
    }

    public Problem selectProblem(User userA, User userB) {
        int avgRate = (userA.getCurrentRate() + userB.getCurrentRate()) / 2;
        int minRate = avgRate - 300;
        int maxRate = avgRate + 300;

        minRate = Math.max(minRate, 0);
        maxRate = Math.min(maxRate, 2000);

        //TODO exclude the problems solved by both of them

        List<Problem> problems = problemRepository.findProblemsInRateRange(minRate, maxRate);

        while (problems.isEmpty() && (minRate > 0 || maxRate < 2000)) {
            minRate = Math.max(minRate - 100, 0);
            maxRate = Math.min(maxRate + 100, 2000);

            problems = problemRepository.findProblemsInRateRange(minRate, maxRate);
        }

        if (problems.isEmpty()) {
            throw new IllegalStateException("No problems available in the full range 0–2000");
        }

        return problems.get(new Random().nextInt(problems.size()));
    }

    public void sendMatchInvite(User sender, String recipientUsername) {
        User recipient = userRepository.findByUsername(recipientUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found with username " + recipientUsername));

        notificationService.send(sender.getId(), recipient.getId(), NotificationType.MATCH_INVITATION,
                "Match Invitation",
                sender.getUsername() + " invites you to a match");
    }

    public MatchResponseDto acceptMatchInvite(User player1, long notificationId) {
        Notification invite = notificationRepository.findById(notificationId).orElseThrow();

        if (!invite.getRecipientId().equals(player1.getId())) {
            throw new UnauthorizedException("Not your invite");
        }

        User player2 = userRepository.findById(invite.getSenderId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + invite.getSenderId()));

        Problem problem = selectProblem(player1, player2);
        return createMatch(player1, player2, problem, 30, GameMode.UNRATED);
    }

    @Transactional
    public MatchResponseDto createMatch(
            User player1,
            User player2,
            Problem problem,
            int duration,
            GameMode gameMode
    ) {
        Match match = Match.builder()
                .duration(duration)
                .gameMode(gameMode)
                .matchState(MatchState.ONGOING)
                .problem(problem)
                .build();

        Match savedMatch = matchRepository.save(match);

        MatchParticipant p1 = matchMapper.createParticipant(player1, savedMatch);
        MatchParticipant p2 = matchMapper.createParticipant(player2, savedMatch);

        List<MatchParticipant> participants = List.of(p1, p2);
        matchParticipantRepository.saveAll(participants);

        savedMatch.setParticipants(participants);
        matchScheduler.scheduleMatchEnd(savedMatch);
        notificationService.sendMatchStart(
                match,
                "Match Begins",
                String.format("Try Your Best, %s", player1.getUsername()),
                player1.getId()
        );
        notificationService.sendMatchStart(
                match,
                "Code Submission",
                String.format("Try Your Best, %s", player2.getUsername()),
                player2.getId()
        );
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
                .map(participant -> {
                    List<Submission> submissions = submissionRepository
                            .findByUserIdAndMatchId(participant.getUser().getId(), matchId);
                    return matchMapper.toMatchSubmissionLogDto(participant, submissions);
                })
                .toList();
    }

    public void submitCode(SubmissionRequestDto submissionRequestDto, User player) {
        Match match = validateMatch(submissionRequestDto.getMatchId(),  player);

        notificationService.sendMatchPop(
                match,
                "Code Submission",
                String.format("%s submitted a solution", player.getUsername()),
                player.getUsername()
        );

        Submission submission = submissionService.submitCode(submissionRequestDto, player);

        notificationService.sendMatchPop(
                match,
                "Submission Status",
                String.format("%s got %s and passed %d test case",
                        player.getUsername(),
                        submission.getStatus(),
                        submission.getNumberOfPassedTestCases()),
                player.getUsername()
        );

        if (submission.getStatus() == SubmissionStatus.ACCEPTED) {
            completeMatch(match, player);
        }
    }

    @Transactional
    public void completeMatch(Match match, User winner) {
        if (match.getMatchState() == MatchState.COMPLETED) return;

        MatchParticipant winnerParticipant = match.getParticipants().stream()
                .filter(mp -> mp.getUser().getId().equals(winner.getId()))
                .findFirst()
                .orElse(null);

        MatchParticipant loserParticipant = match.getParticipants().stream()
                .filter(mp -> !mp.getUser().getId().equals(winner.getId()))
                .findFirst()
                .orElse(null);

        if (winnerParticipant != null && loserParticipant != null) {
            winnerParticipant.setRank(rankMapper.toRank("winner"));
            loserParticipant.setRank(rankMapper.toRank("loser"));
        }

        match.setMatchState(MatchState.COMPLETED);
        matchRepository.save(match);
        matchParticipantRepository.saveAll(match.getParticipants());
    }

    @Transactional
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
