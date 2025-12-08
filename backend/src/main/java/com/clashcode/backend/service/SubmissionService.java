package com.clashcode.backend.service;

import com.clashcode.backend.dto.ExecutionResultDto;
import com.clashcode.backend.dto.SubmissionListDto;
import com.clashcode.backend.dto.SubmissionRequestDto;
import com.clashcode.backend.enums.SubmissionStatus;
import com.clashcode.backend.judge.Judge0.Judge0Client;
import com.clashcode.backend.mapper.SubmissionMapper;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.Submission;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.ProblemRepository;
import com.clashcode.backend.repository.SubmissionRepository;
import com.clashcode.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final TestCaseService testCaseService;
    private final Judge0Client judge0Client;
    private final SubmissionMapper submissionMapper;

    public SubmissionService(SubmissionRepository submissionRepository,
                             UserRepository userRepository,
                             ProblemRepository problemRepository,
                             Judge0Client judge0Client,
                             SubmissionMapper submissionMapper,
                             TestCaseService testCaseService) {

        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.problemRepository = problemRepository;
        this.judge0Client = judge0Client;
        this.submissionMapper = submissionMapper;
        this.testCaseService = testCaseService;
    }

    public void submitCode(SubmissionRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Problem problem = problemRepository.findById(requestDto.getProblemId())
                .orElseThrow(() -> new IllegalArgumentException("Problem not found"));

        List<String> inputs = testCaseService.getInputTestCasesForProblem(problem) ;
        List<String> outputs = testCaseService.getOutputTestCasesForProblem(problem);

        Submission submission = submissionMapper.toEntity(requestDto, user, problem, inputs.size());

        submissionRepository.save(submission);

        List<ExecutionResultDto> executionResults = new ArrayList<>();
        for(int tcIndex = 0; tcIndex < inputs.size(); tcIndex++) {
            submission.setStatus(SubmissionStatus.RUNNING_ON_TEST);
            submission.setNumberOfCurrentTestCase(tcIndex+1);
            submissionRepository.save(submission);
            ExecutionResultDto executionResult = judge0Client.executeAndCompare(
                    requestDto.getCode(),
                    requestDto.getCodeLanguage(),
                    inputs.get(tcIndex),
                    outputs.get(tcIndex),
                    problem.getTimeLimit(),
                    problem.getMemoryLimit()
            );
            executionResults.add(executionResult);

        }
        submissionRepository.save(submissionMapper.toEntity(executionResults, submission));
    }

    public List<SubmissionListDto> getSubmissionsByUser(Long userId) {
        List<Submission> submissions = submissionRepository.findByUserId(userId);
        return submissionMapper.toListDto(submissions);
    }

    public SubmissionListDto getSubmissionStatusById (Long submissionId){
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        return submissionMapper.toListDto(submission);
    }
}
