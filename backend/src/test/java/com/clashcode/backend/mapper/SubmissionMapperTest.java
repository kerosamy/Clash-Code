package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.ExecutionResultDto;
import com.clashcode.backend.dto.SubmissionListDto;
import com.clashcode.backend.dto.SubmissionRequestDto;
import com.clashcode.backend.enums.LanguageVersion;
import com.clashcode.backend.enums.SubmissionStatus;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.Submission;
import com.clashcode.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SubmissionMapperTest {

    private SubmissionMapper submissionMapper;

    @BeforeEach
    void setUp() {
        submissionMapper = new SubmissionMapper();
    }

    @Test
    void toEntity_FromRequestDto_ShouldMapCorrectly() {
        // Arrange
        User user = new User();
        Problem problem = new Problem();
        SubmissionRequestDto dto = new SubmissionRequestDto();
        dto.setCode("System.out.println(\"Hello\");");
        dto.setCodeLanguage("JAVA_OPENJDK_13");

        // Act
        Submission submission = submissionMapper.toEntity(dto, user, problem, 5);

        // Assert
        assertThat(submission).isNotNull();
        assertThat(submission.getCode()).isEqualTo(dto.getCode());
        assertThat(submission.getLanguageVersion()).isEqualTo(LanguageVersion.JAVA_OPENJDK_13);
        assertThat(submission.getUser()).isEqualTo(user);
        assertThat(submission.getProblem()).isEqualTo(problem);
        assertThat(submission.getNumberOfTestCases()).isEqualTo(5);
        assertThat(submission.getStatus()).isEqualTo(SubmissionStatus.WAITING);
        assertThat(submission.getSubmittedAt()).isNotNull();
    }

    @Test
    void toEntity_FromExecutionResultDtoList_ShouldUpdateSubmission() {
        // Arrange
        Submission submission = new Submission();
        ExecutionResultDto result1 = new ExecutionResultDto();
        result1.setStatus("ACCEPTED");
        result1.setTimeTaken(100);
        result1.setMemoryTaken(200);

        ExecutionResultDto result2 = new ExecutionResultDto();
        result2.setStatus("WRONG ANSWER");
        result2.setTimeTaken(150);
        result2.setMemoryTaken(250);

        List<ExecutionResultDto> results = List.of(result1, result2);

        // Act
        Submission updated = submissionMapper.toEntity(results, submission);

        // Assert
        assertThat(updated.getTimeTaken()).isEqualTo(150);
        assertThat(updated.getMemoryTaken()).isEqualTo(250);
        assertThat(updated.getStatus()).isEqualTo(SubmissionStatus.WRONG_ANSWER);
        assertThat(updated.getNumberOfPassedTestCases()).isEqualTo(1); // only result1 passed
    }

    @Test
    void toListDto_FromSingleSubmission_ShouldMapCorrectly() {
        // Arrange
        Problem problem = new Problem();
        problem.setId(10L);
        problem.setTitle("Sample Problem");

        Submission submission = Submission.builder()
                .id(1L)
                .code("code")
                .status(SubmissionStatus.ACCEPTED)
                .memoryTaken(100)
                .timeTaken(50)
                .numberOfTestCases(5)
                .numberOfPassedTestCases(5)
                .numberOfCurrentTestCase(3)
                .submittedAt(java.time.LocalDateTime.now())
                .problem(problem)
                .build();

        // Act
        SubmissionListDto dto = submissionMapper.toListDto(submission);

        // Assert
        assertThat(dto.getSubmissionId()).isEqualTo(1L);
        assertThat(dto.getProblemId()).isEqualTo(10L);
        assertThat(dto.getProblemTitle()).isEqualTo("Sample Problem");
        assertThat(dto.getMemoryTaken()).isEqualTo(100);
        assertThat(dto.getTimeTaken()).isEqualTo(50);
        assertThat(dto.getNumberOfTotalTestCases()).isEqualTo(5);
        assertThat(dto.getNumberOfPassedTestCases()).isEqualTo(5);
        assertThat(dto.getNumberOfCurrentTestCase()).isEqualTo(3);
        assertThat(dto.getSubmissionStatus()).isEqualTo("ACCEPTED");
        assertThat(dto.getSubmittedAt()).isNotBlank();
    }

    @Test
    void toListDto_FromListOfSubmissions_ShouldMapCorrectly() {
        // Arrange
        Problem problem1 = new Problem();
        problem1.setId(1L);
        problem1.setTitle("Problem 1");

        Problem problem2 = new Problem();
        problem2.setId(2L);
        problem2.setTitle("Problem 2");

        Submission submission1 = Submission.builder().id(1L).problem(problem1).build();
        Submission submission2 = Submission.builder().id(2L).problem(problem2).build();
        List<Submission> submissions = List.of(submission1, submission2);

        // Act
        List<SubmissionListDto> dtos = submissionMapper.toListDto(submissions);

        // Assert
        assertThat(dtos).hasSize(2);
        assertThat(dtos).extracting("submissionId").containsExactly(1L, 2L);
        assertThat(dtos).extracting("problemTitle").containsExactly("Problem 1", "Problem 2");
    }
}
