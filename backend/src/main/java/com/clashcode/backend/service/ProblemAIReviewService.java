package com.clashcode.backend.service;

import com.clashcode.backend.ai.GeminiInterface;
import com.clashcode.backend.ai.ProblemAIReviewPrompt;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.ProblemAIReview;
import com.clashcode.backend.repository.ProblemAIReviewRepository;
import com.clashcode.backend.repository.ProblemRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProblemAIReviewService {
    private final GeminiInterface gemini;
    private final ProblemRepository problemRepository;
    private final ProblemAIReviewRepository problemAIReviewRepository;
    private final ProblemAIReviewPrompt promptBuilder;
    private final TestCaseService testCaseService;

    public ProblemAIReviewService(
            GeminiInterface geminiInterface,
            ProblemRepository problemRepository,
            ProblemAIReviewRepository problemAIReviewRepository, ProblemAIReviewPrompt promptBuilder, TestCaseService testCaseService
    ) {
        this.gemini = geminiInterface;
        this.problemRepository = problemRepository;
        this.problemAIReviewRepository = problemAIReviewRepository;
        this.promptBuilder = promptBuilder;
        this.testCaseService = testCaseService;
    }

    public String getProblemAIReview(Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));
        String currentHash = computeProblemHash(problem);

        ProblemAIReview existingReview = problemAIReviewRepository.findById(problemId)
                .orElse(null);

        if (existingReview != null && existingReview.getProblemHash().equals(currentHash)) {
            return existingReview.getReviewJSON();
        }

        String prompt = buildPrompt(problem);
        String aiResponse = gemini.getResponse(prompt);

        ProblemAIReview review = ProblemAIReview.builder()
                .problem(problem)
                .problemHash(currentHash)
                .reviewJSON(aiResponse)
                .build();

        problemAIReviewRepository.save(review);
        return review.getReviewJSON();
    }

    private String buildPrompt(Problem problem) {
        Map<String, String> values = new HashMap<>();

        values.put("title", problem.getTitle());
        values.put("statement", problem.getStatement());
        values.put("inputFormat", problem.getInputFormat());
        values.put("outputFormat", problem.getOutputFormat());
        values.put("timeLimit", String.valueOf(problem.getTimeLimit()));
        values.put("memoryLimit", String.valueOf(problem.getMemoryLimit()));
        values.put("tags", problem.getTags() != null
                ? problem.getTags().toString()
                : "[]");

        values.put("solution", problem.getSolution() != null
                ? problem.getSolution().toString()
                : "No solution provided");

        values.put("testCases", problem.getTestCases() != null
                ? testCaseService.getInputTestCasesForProblem(problem).toString()
                : "No test cases provided");

        return promptBuilder.build(values);
    }

    private String computeProblemHash(Problem problem) {
        String content = String.join("|",
                problem.getStatement(),
                problem.getInputFormat(),
                problem.getOutputFormat(),
                String.valueOf(problem.getTimeLimit()),
                String.valueOf(problem.getMemoryLimit()),
                problem.getSolution() != null ? problem.getSolution().toString() : "",
                problem.getTags().toString(),
                testCaseService.getInputTestCasesForProblem(problem).toString()
        );

        return DigestUtils.sha256Hex(content);
    }
}
