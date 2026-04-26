package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.ExecutionResultDto;
import com.clashcode.backend.dto.SubmissionDetailsDto;
import com.clashcode.backend.dto.SubmissionListDto;
import com.clashcode.backend.dto.SubmissionRequestDto;
import com.clashcode.backend.enums.LanguageVersion;
import com.clashcode.backend.enums.SubmissionStatus;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.Submission;
import com.clashcode.backend.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class SubmissionMapper {
    public Submission toEntity (
        SubmissionRequestDto requestDto,
        User user,
        Problem problem,
        Integer numberOfTestCases
    ) {

        return Submission.builder()
                .code(requestDto.getCode())
                .languageVersion(LanguageVersion.valueOf(requestDto.getCodeLanguage()))
                .user(user)
                .problem(problem)
                .submittedAt(LocalDateTime.now())
                .status(SubmissionStatus.WAITING)
                .numberOfTestCases(numberOfTestCases)
                .build();
    }

    public Submission toEntity(List<ExecutionResultDto> resultDtoList, Submission submission) {
        int maxTime = 0;
        int maxMemory = 0;
        int passedTestCases = 0;
        SubmissionStatus finalStatus = SubmissionStatus.ACCEPTED;

        for (ExecutionResultDto result : resultDtoList) {
            maxTime = Math.max(maxTime, result.getTimeTaken());
            maxMemory = Math.max(maxMemory, result.getMemoryTaken());

            String judgeStatus = result.getStatus().toUpperCase();
            switch (judgeStatus) {
                case "ACCEPTED":
                    passedTestCases++;
                    break;
                case "WRONG ANSWER":
                    finalStatus = SubmissionStatus.WRONG_ANSWER;
                    break;
                case "TIME LIMIT EXCEEDED":
                    finalStatus = SubmissionStatus.TIME_LIMIT_EXCEEDED;
                    break;
                case "MEMORY LIMIT EXCEEDED":
                    finalStatus = SubmissionStatus.MEMORY_LIMIT_EXCEEDED;
                    break;
                case "COMPILATION ERROR":
                    finalStatus = SubmissionStatus.COMPILATION_ERROR;
                    break;
                default:
                    if (finalStatus == SubmissionStatus.ACCEPTED) {
                        finalStatus = SubmissionStatus.WRONG_ANSWER;
                    }
                    break;
            }
        }

        submission.setTimeTaken(maxTime);
        submission.setMemoryTaken(maxMemory);
        submission.setStatus(finalStatus);
        submission.setNumberOfPassedTestCases(passedTestCases);
        return submission;
    }

    public List<SubmissionListDto> toListDto(List<Submission> submissions) {
        List<SubmissionListDto> submissionListDto = new ArrayList<>();
        for (Submission submission : submissions) {
            submissionListDto.add(toListDto(submission));
        }
        return submissionListDto;
    }
    public SubmissionListDto toListDto (Submission submission) {
        return SubmissionListDto.builder()
                .submissionId(submission.getId())
                        .memoryTaken(submission.getMemoryTaken() != null ? submission.getMemoryTaken() : 0)
                        .submissionStatus(submission.getStatus() != null ? submission.getStatus().toString() : "UNKNOWN")
                        .submittedAt(submission.getSubmittedAt() != null ? submission.getSubmittedAt().toString() : "")
                        .timeTaken(submission.getTimeTaken() != null ? submission.getTimeTaken() : 0)
                        .numberOfPassedTestCases(submission.getNumberOfPassedTestCases() !=null ? submission.getNumberOfPassedTestCases() : 0)
                        .numberOfTotalTestCases(submission.getNumberOfTestCases() !=null ? submission.getNumberOfTestCases() : 0)
                        .numberOfCurrentTestCase(submission.getNumberOfCurrentTestCase() !=null ? submission.getNumberOfCurrentTestCase() : 0)
                .matchId(submission.getMatch() != null ? submission.getMatch().getId() : null)
                .problemTitle(submission.getProblem().getTitle())
                .problemId(submission.getProblem().getId())
                .build();
    }
    public SubmissionDetailsDto toDetailsDto  (Submission submission) {
        return SubmissionDetailsDto.builder()
                .submissionLang(String.valueOf(submission.getLanguageVersion()))
                .submissionCode(submission.getCode())
                .username(submission.getUser().getUsername())
                .problemTitle(submission.getProblem().getTitle())
                .submissionStatus(String.valueOf(submission.getStatus()))
                .build();
    }
}
