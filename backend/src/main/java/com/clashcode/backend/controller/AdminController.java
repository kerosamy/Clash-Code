package com.clashcode.backend.controller;

import com.clashcode.backend.dto.FullProblemResponseDto;
import com.clashcode.backend.dto.ProblemListDto;
import com.clashcode.backend.dto.PartialProblemResponseDto;
import com.clashcode.backend.service.ProblemService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/problems")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ProblemService problemService;

    public AdminController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @GetMapping("/pending")
    public ResponseEntity<Page<ProblemListDto>> getPendingProblems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(problemService.getPendingProblems(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartialProblemResponseDto> getProblemDetails(@PathVariable Long id) {
        return ResponseEntity.ok(problemService.getPartialProblemById(id));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptProblem(@PathVariable Long id) {
        problemService.acceptProblem(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectProblem(
            @PathVariable Long id,
            @RequestBody String rejectionNote
    ) {
        problemService.rejectProblem(id, rejectionNote);
        return ResponseEntity.ok().build();
    }
}
