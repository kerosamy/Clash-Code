package com.clashcode.backend.controller;
import com.clashcode.backend.dto.ProblemListDto;
import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.ProblemResponseDto;
import com.clashcode.backend.service.ProblemService;
import org.springframework.data.domain.Page;
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

    @GetMapping("/browse")
    public ResponseEntity<Page<ProblemListDto>> browse(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(problemService.getAllProblems(page, size));
    }

//    @GetMapping("/submit/{id}")
//    public ResponseEntity<ProblemResponseDto> submit(@PathVariable Long id) {
//
//
//    }

}
