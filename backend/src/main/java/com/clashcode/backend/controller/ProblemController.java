package com.clashcode.backend.controller;

import com.clashcode.backend.dto.ProblemFilterDto;
import com.clashcode.backend.dto.ProblemListDto;
import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.ProblemResponseDto;
import com.clashcode.backend.service.ProblemService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/problem")
public class ProblemController {

    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemResponseDto> getProblem(@PathVariable Long id) {
        ProblemResponseDto problem = problemService.getProblemById(id);
        return ResponseEntity.ok(problem);
    }

    @PostMapping
    public ResponseEntity<Void> addProblem(
            @RequestPart("problem") ProblemRequestDto problemRequestDto,
            @RequestPart("testcases") List<MultipartFile> files) {

        problemService.addProblem(problemRequestDto, files);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/browse")
    public ResponseEntity<Page<ProblemListDto>> browse(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ProblemListDto> problems = problemService.getAllProblems(page, size);
        return ResponseEntity.ok(problems);
    }

    @PostMapping("/browse/filter")
    public ResponseEntity<Page<ProblemListDto>> browseFiltered(
            @RequestBody ProblemFilterDto filterDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ProblemListDto> filteredProblems = problemService.getFilteredProblems(
                filterDto.getTags(), filterDto.getMinRate(), filterDto.getMaxRate(), page, size
        );
        return ResponseEntity.ok(filteredProblems);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProblemListDto>> searchByName(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ProblemListDto> results = problemService.searchProblemsByName(keyword, page, size);
        return ResponseEntity.ok(results);
    }
}
