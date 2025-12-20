package com.clashcode.backend.service;

import com.clashcode.backend.Notification.Dtos.MatchNotificationDto;
import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.*;
import com.clashcode.backend.dto.MatchResponseDto;
import com.clashcode.backend.dto.MatchSubmissionLogDto;
import com.clashcode.backend.dto.SubmissionRequestDto;
import com.clashcode.backend.enums.GameMode;
import com.clashcode.backend.dto.PartialProblemResponseDto;
import com.clashcode.backend.enums.MatchState;
import com.clashcode.backend.enums.NotificationType;
import com.clashcode.backend.enums.SubmissionStatus;
import com.clashcode.backend.exception.UnauthorizedException;
import com.clashcode.backend.exception.UserNotFoundException;
import com.clashcode.backend.mapper.MatchMapper;
import com.clashcode.backend.mapper.MatchNotificationMapper;
import com.clashcode.backend.mapper.RankMapper;
import com.clashcode.backend.matching.MatchingServiceClient;
import com.clashcode.backend.matching.dto.MatchingRequestDto;
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
    private final MatchNotificationMapper matchNotificationMapper;
    private final ProblemService problemService;
    private final MatchingServiceClient matchingServiceClient;

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
            MatchNotificationMapper matchNotificationMapper,
            ProblemService problemService,
            MatchingServiceClient matchingServiceClient
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
        this.matchNotificationMapper = matchNotificationMapper;
        this.problemService = problemService;
        this.matchingServiceClient = matchingServiceClient;
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

        MatchNotificationDto dto = matchNotificationMapper.mapMatchInvite(sender);

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
        Match match = validateMatch(submissionRequestDto.getMatchId(), player);

        match.getParticipants().forEach(mp -> {
            MatchNotificationDto dto = matchNotificationMapper.mapSubmissionReceived(match, player);
            notificationService.send(
                    player.getId(),
                    mp.getUser().getId(),
                    mp.getUser().getUsername(),
                    dto
            );
        });

        Submission submission = submissionService.submitCode(submissionRequestDto, player);

        match.getParticipants().forEach(mp -> {
            MatchNotificationDto resultDto = matchNotificationMapper.mapSubmissionResult(match, submission);
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
        if (match == null) throw new IllegalArgumentException("Match cannot be null");
        if (match.getMatchState() == MatchState.COMPLETED) return;

        MatchParticipant winnerParticipant = null;
        MatchParticipant loserParticipant = null;

        if (winner != null) {
            winnerParticipant = match.getParticipants().stream()
                    .filter(mp -> mp.getUser().getId().equals(winner.getId()))
                    .findFirst()
                    .orElse(null);

            loserParticipant = match.getParticipants().stream()
                    .filter(mp -> !mp.getUser().getId().equals(winner.getId()))
                    .findFirst()
                    .orElse(null);
        }

        List<MatchParticipant> participants = match.getParticipants();
        if (participants == null || participants.size() != 2) {
            throw new IllegalStateException("Match must have exactly 2 participants");
        }

        // Handle draw or invalid winner
        if (winnerParticipant != null && loserParticipant != null) {
            winnerParticipant.setRank(rankMapper.toRank("winner"));
            loserParticipant.setRank(rankMapper.toRank("loser"));
        } else {
            // Either winner is null, invalid, or participants missing → draw
            participants.forEach(mp -> mp.setRank(rankMapper.toRank("draw")));
            winnerParticipant = null;
            loserParticipant = null;
            System.out.println("Match ended in a draw or invalid winner");
        }

        match.setMatchState(MatchState.COMPLETED);
        matchRepository.save(match);
        matchParticipantRepository.saveAll(participants);

        // Send match ended notifications
        for (MatchParticipant participant : participants) {
            MatchNotificationDto dto = matchNotificationMapper.mapMatchEnded(match);
            notificationService.send(
                    match.getId(),
                    participant.getUser().getId(),
                    participant.getUser().getUsername(),
                    dto
            );
        }

        // ELO calculation for Rated matches
        if (match.getGameMode() == GameMode.RATED && participants.size() == 2) {
            updateParticipantRatings(match, winnerParticipant != null ? winnerParticipant.getUser() : null);
        }

        // Save users safely, ensuring no negative ratings
        for (MatchParticipant mp : participants) {
            User user = mp.getUser();
            if (user.getCurrentRate() < 0) user.setCurrentRate(0);
            user.setMaxRate(Math.max(user.getMaxRate(), user.getCurrentRate()));
        }

        matchRepository.save(match);
        userRepository.saveAll(participants.stream()
                .map(MatchParticipant::getUser)
                .toList());
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

        MatchNotificationDto dto = matchNotificationMapper.mapOpponentResigned(match, resigningUser);
        notificationService.send(
                resigningUser.getId(),
                winnerParticipant.getUser().getId(),
                winnerParticipant.getUser().getUsername(),
                dto
        );

        completeMatch(match, winnerParticipant.getUser());
    }


    public PartialProblemResponseDto getMatchProblem(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Match not found"));
        Problem problem = match.getProblem();
        return problemService.getPartialProblemById(problem.getId());
    }

    public MatchResponseDto getMatchDetails(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with ID: " + matchId));
        return matchMapper.toResponseDto(match);
    }

    public MatchResultDto getMatchResults(Long matchId, User user) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with ID: " + matchId));

        MatchParticipant participant = match.getParticipants().stream()
                .filter(mp -> mp.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User is not a participant in this match"));

        boolean isRated = match.getGameMode() == GameMode.RATED;
        return matchMapper.toMatchResultDto(isRated, participant);
    }
    
    private void updateParticipantRatings(Match match, User winner) {
        List<MatchParticipant> participants = match.getParticipants();
        if (participants.size() != 2) return;

        MatchParticipant mpA = participants.get(0);
        MatchParticipant mpB = participants.get(1);

        User userA = mpA.getUser();
        User userB = mpB.getUser();

        int oldRatingA = userA.getCurrentRate();
        int oldRatingB = userB.getCurrentRate();
        int difficulty = match.getProblem().getRate();

        double expectedA = EloCalculatorService.calculateExpectedScore(oldRatingA, oldRatingB);
        double expectedB = 1.0 - expectedA;

        double scoreA, scoreB;
        if (winner == null) {
            scoreA = 0.5;
            scoreB = 0.5;
        } else if (winner.getId().equals(userA.getId())) {
            scoreA = 1.0;
            scoreB = 0.0;
        } else {
            scoreA = 0.0;
            scoreB = 1.0;
        }

        int newRatingA = EloCalculatorService.calculateNewRating(oldRatingA, expectedA, scoreA, difficulty);
        int newRatingB = EloCalculatorService.calculateNewRating(oldRatingB, expectedB, scoreB, difficulty);

        mpA.setRateChange(newRatingA - oldRatingA);
        mpA.setNewRating(newRatingA);

        mpB.setRateChange(newRatingB - oldRatingB);
        mpB.setNewRating(newRatingB);

        userA.setCurrentRate(newRatingA);
        userA.setMaxRate(Math.max(userA.getMaxRate(), newRatingA));

        userB.setCurrentRate(newRatingB);
        userB.setMaxRate(Math.max(userB.getMaxRate(), newRatingB));
    }
    
    @Transactional
    public void startRatedMatch(MatchCreationDto dto) {

        User userA = userRepository.findById(dto.getPlayerIdA())
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        User userB = userRepository.findById(dto.getPlayerIdB())
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        Problem problem = selectProblem(userA , userB);

        createMatch(userA,userB,problem,15,GameMode.RATED);// change the duration
    }

    public void searchForOpponent (User user){
        matchingServiceClient.requestMatching(
                new MatchingRequestDto(
                        user.getId(),
                        user.getCurrentRate()
                )
        );
    }

    public void cancelSearchForOpponent (User user){
        matchingServiceClient.deleteMatching(
                user.getId()
        );
    }
}

