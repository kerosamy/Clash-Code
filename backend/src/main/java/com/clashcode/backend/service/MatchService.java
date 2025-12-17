package com.clashcode.backend.service;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.*;
import com.clashcode.backend.exception.UnauthorizedException;
import com.clashcode.backend.exception.UserNotFoundException;
import com.clashcode.backend.mapper.MatchMapper;
import com.clashcode.backend.mapper.MatchNotificationMapper;
import com.clashcode.backend.mapper.ProblemMapper;
import com.clashcode.backend.mapper.RankMapper;
import com.clashcode.backend.model.*;
import com.clashcode.backend.repository.*;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    private final ProblemMapper problemMapper;
    private final TestCaseService testCaseService;
    private final MatchNotificationMapper matchNotificationMapper;

    public MatchService(
            UserRepository userRepository,
            ProblemRepository problemRepository,
            MatchRepository matchRepository,
            MatchParticipantRepository matchParticipantRepository,
            MatchMapper matchMapper,
            SubmissionRepository submissionRepository,
            RankMapper rankMapper,
            MatchScheduler matchScheduler,
            NotificationService notificationService,
            NotificationRepository notificationRepository,
            SubmissionService submissionService,
            ProblemMapper problemMapper,
            TestCaseService testCaseService,
            MatchNotificationMapper matchNotificationMapper
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
        this.problemMapper = problemMapper;
        this.testCaseService = testCaseService;
        this.matchNotificationMapper = matchNotificationMapper;
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

        MatchNotificationDto dto = matchNotificationMapper.mapMatchInvite(sender, recipient);

        notificationService.send(
                sender.getId(),
                recipient.getId(),
                recipient.getUsername(),
                dto
        );
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

        participants.forEach(mp -> {
            MatchNotificationDto dto = matchNotificationMapper.mapMatchStarted(savedMatch, mp.getUser());
            notificationService.send(
                    player1.getId(),
                    mp.getUser().getId(),
                    mp.getUser().getUsername(),
                    dto
            );
        });

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

    @Transactional
    public List<MatchSubmissionLogDto> getMatchSubmissionLog(Long matchId) {
            Match match = matchRepository.findById(matchId)
                    .orElseThrow(() -> new IllegalArgumentException("Match not found with ID: " + matchId));
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

        match.getParticipants().forEach(mp -> {
            MatchNotificationDto dto = matchNotificationMapper.mapSubmissionReceived(match, player, mp.getUser());
            notificationService.send(
                    player.getId(),
                    mp.getUser().getId(),
                    mp.getUser().getUsername(),
                    dto
            );
        });

        Submission submission = submissionService.submitCode(submissionRequestDto, player);

        match.getParticipants().forEach(mp -> {
            MatchNotificationDto resultDto = matchNotificationMapper.mapSubmissionResult(match, submission, mp.getUser());
            notificationService.send(
                    player.getId(),
                    mp.getUser().getId(),
                    mp.getUser().getUsername(),
                    resultDto
            );
        });

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


        for (MatchParticipant participant : match.getParticipants()) {
            MatchNotificationDto dto = matchNotificationMapper.mapMatchEnded(match, participant.getUser());
            notificationService.send(
                    match.getId(),
                    participant.getUser().getId(),
                    participant.getUser().getUsername(),
                    dto
            );
        }

        //TODO calculate new ratings
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


    public ProblemResponseDto getMatchProblem(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Match not found"));
        Problem problem = match.getProblem();
        if (problem == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Match has no problem assigned");
        }
        return problemMapper.toResponseDto(problem, testCaseService.getVisibleTestCasesForProblem(problem));
    }

    public MatchResponseDto getMatchDetails(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with ID: " + matchId));
        return matchMapper.toResponseDto(match);
    }
}


