package com.clashcode.backend.controller;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.model.User;
import com.clashcode.backend.service.ProblemService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/problem")
@PreAuthorize("hasRole('USER')")
public class ProblemController {

    private final ProblemService problemService;

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartialProblemResponseDto> getPartialProblem(@PathVariable Long id) {
        PartialProblemResponseDto problem = problemService.getPartialProblemById(id);
        return ResponseEntity.ok(problem);
    }

    @GetMapping("/draft/{id}")
    public ResponseEntity<FullProblemResponseDto> getFullProblem(@PathVariable Long id) {
        FullProblemResponseDto problem = problemService.getFullProblemById(id);
        return ResponseEntity.ok(problem);
    }

    @PostMapping("/suggest")
    public ResponseEntity<Void> addProblem(
            @RequestPart("problem") ProblemRequestDto problemRequestDto,
            @RequestPart("testcases") List<MultipartFile> files,
            @AuthenticationPrincipal User user
    ) {
        if(user == null) {
            return ResponseEntity.badRequest().build();
        }

        String username = user.getUsername();
        problemService.addProblem(problemRequestDto, files, username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/browse")
    public ResponseEntity<Page<ProblemListDto>> browse(
            @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = "" + DEFAULT_SIZE) int size
    ) {
        Page<ProblemListDto> problems = problemService.getApprovedProblems(page, size);
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/browse/rejected")
    public ResponseEntity<Page<ProblemListDto>> browseRejected(
            @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = "" + DEFAULT_SIZE) int size,
            @AuthenticationPrincipal User user
    ) {
        Page<ProblemListDto> problems = problemService.getRejectedProblems(page, size, user.getUsername());
        return ResponseEntity.ok(problems);
    }

    @PostMapping("/browse/filter")
    public ResponseEntity<Page<ProblemListDto>> browseFiltered(
            @RequestBody ProblemFilterDto filterDto,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = "" + DEFAULT_SIZE) int size
    ) {
        Page<ProblemListDto> filteredProblems = problemService.getFilteredProblems(
                filterDto.getTags(), filterDto.getMinRate(), filterDto.getMaxRate(), page, size);
        return ResponseEntity.ok(filteredProblems);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProblemListDto>> searchByName(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = "" + DEFAULT_SIZE) int size
    ) {
        Page<ProblemListDto> results = problemService.searchProblemsByName(keyword, page, size);
        return ResponseEntity.ok(results);
    }
}
