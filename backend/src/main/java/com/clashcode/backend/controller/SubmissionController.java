package com.clashcode.backend.controller;

import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.SubmissionListDto;
import com.clashcode.backend.dto.SubmissionRequestDto;
import com.clashcode.backend.service.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public List<SubmissionListDto> getSubmissionsByUser(@PathVariable Long userId) {
        return submissionService.getSubmissionsByUser(userId);
    }




}
