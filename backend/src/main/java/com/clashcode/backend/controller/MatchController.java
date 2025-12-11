package com.clashcode.backend.controller;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.exception.UnauthorizedException;
import com.clashcode.backend.model.Notification;
import com.clashcode.backend.model.User;
import com.clashcode.backend.service.MatchService;
import com.clashcode.backend.service.SubmissionService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Void> invitePlayer(
            @PathVariable String recipientUsername,
            @AuthenticationPrincipal User sender
    ) {
        matchService.sendMatchInvite(sender, recipientUsername);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/invite/{notificationId}/accept")
    public ResponseEntity<MatchResponseDto> acceptInvite(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal User player1) {
        MatchResponseDto matchResponseDto = matchService.acceptMatchInvite(player1, notificationId);
        return ResponseEntity.ok(matchResponseDto);
    }

    @GetMapping("/{matchId}/problem")
    public ResponseEntity<ProblemResponseDto> getMatchProblem(@PathVariable Long matchId) {
        return ResponseEntity.ok(matchService.getMatchProblem(matchId));
    }
}
