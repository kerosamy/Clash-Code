package com.clashcode.backend.service;

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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final Judge0Client judge0Client;
    private final SubmissionMapper submissionMapper;

    public SubmissionService(SubmissionRepository submissionRepository,
                             UserRepository userRepository,
                             ProblemRepository problemRepository,
                             Judge0Client judge0Client,
                             SubmissionMapper submissionMapper) {

        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.problemRepository = problemRepository;
        this.judge0Client = judge0Client;
        this.submissionMapper = submissionMapper;
    }

    public void submitCode(SubmissionRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Problem problem = problemRepository.findById(requestDto.getProblemId())
                .orElseThrow(() -> new IllegalArgumentException("Problem not found"));

        Submission submission = submissionMapper.toEntity(requestDto);
        submission.setUser(user);
        submission.setProblem(problem);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStatus(SubmissionStatus.WAITING);
        submissionRepository.save(submission);


    }
}
