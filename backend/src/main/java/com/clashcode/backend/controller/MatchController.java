package com.clashcode.backend.controller;

import com.clashcode.backend.dto.CreateMatchRequestDto;
import com.clashcode.backend.dto.MatchResponseDto;
import com.clashcode.backend.dto.MatchSubmissionLogDto;
import com.clashcode.backend.dto.SubmissionRequestDto;
import com.clashcode.backend.exception.UnauthorizedException;
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
    private final SubmissionService submissionService;

    public MatchController(MatchService matchService, SubmissionService submissionService) {
        this.matchService = matchService;
        this.submissionService = submissionService;
    }

    @PostMapping("/create")
    public ResponseEntity<MatchResponseDto> createMatch(@RequestBody CreateMatchRequestDto createMatchRequestDto) {
        MatchResponseDto matchResponseDto = matchService.createMatch(createMatchRequestDto);
        return ResponseEntity.ok(matchResponseDto);
    }

    @PostMapping("/{matchId}/submit")
    public ResponseEntity<Void> submitCode (
            @RequestBody SubmissionRequestDto submissionRequestDto,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        submissionService.submitCode(submissionRequestDto, user);
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
}
