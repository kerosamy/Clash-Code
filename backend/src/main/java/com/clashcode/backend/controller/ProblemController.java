package com.clashcode.backend.controller;
import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.ProblemResponsDto;
import com.clashcode.backend.service.ProblemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProblemController {
    ProblemService problemService;
    ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @GetMapping("/problem/get-problem")
    public ProblemResponsDto getProblem(@RequestParam Long id) {
        return problemService.getProblemById(id);
    }

    @PostMapping("/problem/add-problem")
    public ResponseEntity<Void> addProblem(@RequestBody ProblemRequestDto problemRequestDto) {
        problemService.addProblem(problemRequestDto);
        return ResponseEntity.ok().build();
    }

}
