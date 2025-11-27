package com.clashcode.backend.controller;

import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.SubmissionRequestDto;
import com.clashcode.backend.service.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {
    private final SubmissionService submissionService;
    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("submit-code")
    public ResponseEntity<Void> submitCode(@RequestBody SubmissionRequestDto submissionRequestDto) {
        submissionService.submitCode(submissionRequestDto);
    }

}
