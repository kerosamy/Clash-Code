package com.clashcode.backend.controller;

import com.clashcode.backend.Notification.Dtos.MatchNotificationDto;
import com.clashcode.backend.dto.*;
import com.clashcode.backend.exception.UnauthorizedException;
import com.clashcode.backend.model.User;
import com.clashcode.backend.service.MatchService;
import com.clashcode.backend.service.SubmissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matches")
public class MatchController {
    private final MatchService matchService;

    public MatchController(MatchService matchService, SubmissionService submissionService) {
        this.matchService = matchService;
    }

    @PostMapping("/{matchId}/submit")
    public ResponseEntity<Void> submitCode (
            @RequestBody SubmissionRequestDto submissionRequestDto,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        matchService.submitCode(submissionRequestDto, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{matchId}/submission-log")
    public List<MatchSubmissionLogDto> getMatchSubmissionLog(@PathVariable Long matchId) {
        return matchService.getMatchSubmissionLog(matchId);
    }

    @PostMapping("/{matchId}/resign")
    public ResponseEntity<Void> resignMatch(
            @PathVariable Long matchId,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        matchService.resignMatch(matchId, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/invite/{recipientUsername}")
    public ResponseEntity<Long> invitePlayer(
            @PathVariable String recipientUsername,
            @AuthenticationPrincipal User sender
    ) {
        if (sender == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        Long notificationId = matchService.sendMatchInvite(sender, recipientUsername);
        return ResponseEntity.ok(notificationId);
    }


    @PostMapping("/invite/{notificationId}/accept")
    public ResponseEntity<MatchResponseDto> acceptInvite(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal User player1) {
        MatchResponseDto matchResponseDto = matchService.acceptMatchInvite(player1, notificationId);
        return ResponseEntity.ok(matchResponseDto);
    }

    @PatchMapping("/invite/{notificationId}/cancel")
    public ResponseEntity<Void> cancelInvite(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal User sender
    ) {
        if (sender == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        matchService.cancelMatchInvite(sender, notificationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{matchId}/problem")
    public ResponseEntity<PartialProblemResponseDto> getMatchProblem(@PathVariable Long matchId) {
        return ResponseEntity.ok(matchService.getMatchProblem(matchId));
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<MatchResponseDto> getMatchDetails(@PathVariable Long matchId) {
        return ResponseEntity.ok(matchService.getMatchDetails(matchId));
    }

    @GetMapping("/{matchId}/results")
    public ResponseEntity<MatchResultDto> getMatchResults(
            @PathVariable Long matchId,
            @AuthenticationPrincipal User user
    ) {
        MatchResultDto matchResultDto = matchService.getMatchResults(matchId, user);
        return ResponseEntity.ok(matchResultDto);
    }

    @PostMapping("/start-rated-match")
    public ResponseEntity<Void> createMatch(@RequestBody MatchCreationDto matchCreationDto){
        matchService.startRatedMatch(matchCreationDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/search-opponent")
    public ResponseEntity<Void> searchOpponent(@AuthenticationPrincipal User user) {
        matchService.searchForOpponent(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/search-opponent/cancel")
    public ResponseEntity<Void> cancelOpponent(@AuthenticationPrincipal User user) {
        matchService.cancelSearchForOpponent(user);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/my-history")
    public ResponseEntity<Page<MatchHistoryDto>> getMatchHistory(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean rated
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
                matchService.getUserMatchHistory(user.getId(), rated, pageable)
        );
    }
}
