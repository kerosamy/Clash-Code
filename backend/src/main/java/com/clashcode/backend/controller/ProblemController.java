package com.clashcode.backend.controller;
import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.ProblemResponseDto;
import com.clashcode.backend.service.ProblemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/problem")
public class ProblemController {
    private final ProblemService problemService;

    ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @GetMapping("/{id}")
    public ProblemResponseDto getProblem(@PathVariable Long id) {
        return problemService.getProblemById(id);
    }

    @PostMapping
    public ResponseEntity<Void> addProblem(@RequestBody ProblemRequestDto problemRequestDto) {
        problemService.addProblem(problemRequestDto);
        return ResponseEntity.ok().build();
    }

}
