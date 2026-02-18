package com.clashcode.backend.controller;

import com.clashcode.backend.dto.SubmissionDetailsDto;
import com.clashcode.backend.dto.SubmissionListDto;
import com.clashcode.backend.dto.SubmissionRequestDto;
import com.clashcode.backend.exception.UnauthorizedException;
import com.clashcode.backend.model.User;
import com.clashcode.backend.service.SubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/submissions")
@PreAuthorize("hasRole('USER')")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/submit")
    public ResponseEntity<Void> submitCode(
            @RequestBody SubmissionRequestDto submissionRequestDto,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        submissionService.submitCode(submissionRequestDto, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-submissions")
    public ResponseEntity<List<SubmissionListDto>> getMySubmissions(@AuthenticationPrincipal User user) {
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        return ResponseEntity.ok(submissionService.getSubmissionsByUser(user.getId()));
    }


    @GetMapping("/status/{submissionId}")
    public ResponseEntity<SubmissionListDto> getSubmissionStatusById(@PathVariable Long submissionId) {
        return ResponseEntity.ok(submissionService.getSubmissionStatusById(submissionId));
    }

    @GetMapping("/details/{submissionId}")
    public ResponseEntity<SubmissionDetailsDto> getSubmissionDetailsById(@PathVariable Long submissionId) {
        return ResponseEntity.ok(submissionService.getSubmissionDetailsById(submissionId));
    }

    @GetMapping("/problem-title/{problemId}")
    public ResponseEntity<String> getProblemTitleById(@PathVariable Long problemId) {
        return ResponseEntity.ok(submissionService.getProblemTitleById(problemId));
    }

    @GetMapping("/judge/health")
    public ResponseEntity<?> checkJudgeHealth() {
        boolean available = submissionService.isJudgeAvailable();

        if (available) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Judge service unavailable"));
        }
    }

}
