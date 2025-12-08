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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SubmissionServiceTest {

    private SubmissionRepository submissionRepository;
    private UserRepository userRepository;
    private ProblemRepository problemRepository;
    private TestCaseService testCaseService;
    private Judge0Client judge0Client;
    private SubmissionMapper submissionMapper;

    private SubmissionService submissionService;

    @BeforeEach
    void setUp() {
        submissionRepository = mock(SubmissionRepository.class);
        userRepository = mock(UserRepository.class);
        problemRepository = mock(ProblemRepository.class);
        testCaseService = mock(TestCaseService.class);
        judge0Client = mock(Judge0Client.class);
        submissionMapper = mock(SubmissionMapper.class);

        submissionService = new SubmissionService(
                submissionRepository,
                userRepository,
                problemRepository,
                judge0Client,
                submissionMapper,
                testCaseService
        );
    }

    @Test
    void submitCode_ShouldSaveSubmissionAndCallJudge() {
        // Arrange
        SubmissionRequestDto requestDto = SubmissionRequestDto.builder()
                .userId(1L)
                .problemId(2L)
                .code("print('hello')")
                .codeLanguage("python")
                .build();

        User user = new User();
        user.setId(1L);
        Problem problem = new Problem();
        problem.setId(2L);

        Submission submissionEntity = new Submission();
        Submission updatedSubmission = new Submission();
        ExecutionResultDto result = new ExecutionResultDto();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(problemRepository.findById(2L)).thenReturn(Optional.of(problem));
        when(testCaseService.getInputTestCasesForProblem(problem)).thenReturn(List.of("input1"));
        when(testCaseService.getOutputTestCasesForProblem(problem)).thenReturn(List.of("output1"));
        when(submissionMapper.toEntity(List.of(result), submissionEntity)).thenReturn(updatedSubmission);

        // Act
        submissionService.submitCode(requestDto);

        // Assert
        ArgumentCaptor<Submission> captor = ArgumentCaptor.forClass(Submission.class);
        verify(submissionRepository, times(2)).save(captor.capture());
        List<Submission> savedSubmissions = captor.getAllValues();

        assertThat(savedSubmissions.get(0).getStatus()).isEqualTo(SubmissionStatus.WAITING);
        assertThat(savedSubmissions.get(0).getUser()).isEqualTo(user);
        assertThat(savedSubmissions.get(0).getProblem()).isEqualTo(problem);
        assertThat(savedSubmissions.get(1)).isEqualTo(updatedSubmission);
    }

    @Test
    void getSubmissionsByUser_ShouldReturnListDto() {
        // Arrange
        Submission submission = new Submission();
        SubmissionListDto dto = new SubmissionListDto();
        when(submissionRepository.findByUserId(1L)).thenReturn(List.of(submission));
        when(submissionMapper.toListDto(List.of(submission))).thenReturn(List.of(dto));

        // Act
        List<SubmissionListDto> result = submissionService.getSubmissionsByUser(1L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(dto);
    }

    @Test
    void submitCode_ShouldThrowIfUserNotFound() {
        SubmissionRequestDto requestDto = SubmissionRequestDto.builder()
                .userId(99L)
                .problemId(2L)
                .build();
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        try {
            submissionService.submitCode(requestDto);
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).isEqualTo("User not found");
        }
    }

    @Test
    void submitCode_ShouldThrowIfProblemNotFound() {
        SubmissionRequestDto requestDto = SubmissionRequestDto.builder()
                .userId(1L)
                .problemId(99L)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(problemRepository.findById(99L)).thenReturn(Optional.empty());

        try {
            submissionService.submitCode(requestDto);
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).isEqualTo("Problem not found");
        }
    }
}
