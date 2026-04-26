package com.clashcode.backend.controller;

import com.clashcode.backend.service.ProblemAIReviewService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('ADMIN')")
public class ProblemAIReviewController {
    private final ProblemAIReviewService problemAIReviewService;

    public ProblemAIReviewController(ProblemAIReviewService problemAIReviewService) {
        this.problemAIReviewService = problemAIReviewService;
    }

    @PostMapping("/ai-review/{problemId}")
    public ResponseEntity<String> getProblemAIReview(@PathVariable Long problemId) {
        System.out.println("Problem ID: " + problemId);
        String aiReview = problemAIReviewService.getProblemAIReview(problemId);
        System.out.println("AI Review Response: " + aiReview);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(aiReview);
    }
}
