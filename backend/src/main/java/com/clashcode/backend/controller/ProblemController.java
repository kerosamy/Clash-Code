package com.clashcode.backend.controller;
import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.ProblemResponsDto;
import com.clashcode.backend.service.ProblemService;
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
    public ProblemResponsDto addProblem(@RequestBody ProblemRequestDto problemRequestDto) {
        return problemService.addProblem(problemRequestDto);
    }

}
