package com.clashcode.backend.service;

import com.clashcode.backend.dto.ExecutionResultDto;
import com.clashcode.backend.dto.SubmissionDetailsDto;
import com.clashcode.backend.dto.SubmissionListDto;
import com.clashcode.backend.dto.SubmissionRequestDto;
import com.clashcode.backend.enums.SubmissionStatus;
import com.clashcode.backend.judge.Judge0.Judge0Client;
import com.clashcode.backend.mapper.SubmissionMapper;
import com.clashcode.backend.model.Match;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.Submission;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.MatchRepository;
import com.clashcode.backend.repository.ProblemRepository;
import com.clashcode.backend.repository.SubmissionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final ProblemRepository problemRepository;
    private final TestCaseService testCaseService;
    private final Judge0Client judge0Client;
    private final SubmissionMapper submissionMapper;
    private final MatchRepository matchRepository;

    public SubmissionService(
            SubmissionRepository submissionRepository,
            ProblemRepository problemRepository,
            TestCaseService testCaseService,
            Judge0Client judge0Client,
            SubmissionMapper submissionMapper,
            MatchRepository matchRepository
    ) {
        this.submissionRepository = submissionRepository;
        this.problemRepository = problemRepository;
        this.testCaseService = testCaseService;
        this.judge0Client = judge0Client;
        this.submissionMapper = submissionMapper;
        this.matchRepository = matchRepository;
    }

    public Submission submitCode(SubmissionRequestDto requestDto, User user) {
        Problem problem = problemRepository.findById(requestDto.getProblemId())
                .orElseThrow(() -> new IllegalArgumentException("Problem not found"));

        List<String> inputs = testCaseService.getInputTestCasesForProblem(problem) ;
        List<String> outputs = testCaseService.getOutputTestCasesForProblem(problem);

        Submission submission = submissionMapper.toEntity(requestDto, user, problem, inputs.size());

        if(requestDto.getMatchId()!=null) {
            Match match = matchRepository.findById(requestDto.getMatchId())
                    .orElseThrow(() -> new IllegalArgumentException("Match not found"));
            submission.setMatch(match);
        }

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
        problem.setSubmissionsCount(problem.getSubmissionsCount()+1);
        problemRepository.save(problem);

        return submission;
    }

    public List<SubmissionListDto> getSubmissionsByUser(Long userId) {
        List<Submission> submissions = submissionRepository.findByUserId(userId);
        return submissionMapper.toListDto(submissions).reversed();
    }

    public SubmissionListDto getSubmissionStatusById (Long submissionId){
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        return submissionMapper.toListDto(submission);
    }

    public SubmissionDetailsDto getSubmissionDetailsById(Long submissionId){
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        return submissionMapper.toDetailsDto(submission);
    }

    public String getProblemTitleById(Long problemId) {
        return problemRepository.findById(problemId)
                .map(Problem::getTitle)
                .orElseThrow(() -> new RuntimeException("Problem not found"));
    }
}
