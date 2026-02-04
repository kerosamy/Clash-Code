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
import com.clashcode.backend.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubmissionServiceTest {

    @InjectMocks
    private SubmissionService submissionService;

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private TestCaseService testCaseService;

    @Mock
    private Judge0Client judge0Client;

    @Mock
    private SubmissionMapper submissionMapper;

    @Captor
    ArgumentCaptor<Submission> submissionCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------------------------------------------------------------
    // TEST 1: submitCode() success
    // -------------------------------------------------------------------------
    @Test
    void submitCode_ShouldRunAllTestCases_AndSaveSubmission() {
        // GIVEN
        SubmissionRequestDto dto = new SubmissionRequestDto();
        dto.setCode("print(1)");
        dto.setCodeLanguage("PYTHON_3_8");
        dto.setProblemId(1L);

        User user = new User();
        user.setId(10L);

        Problem problem = Problem.builder()
                .id(1L)
                .timeLimit(1000)
                .memoryLimit(128)
                .build();

        List<String> inputs = List.of("1", "2", "3");
        List<String> outputs = List.of("1", "2", "3");

        Submission submission = new Submission();
        submission.setId(50L);
        submission.setStatus(SubmissionStatus.WAITING);

        // MOCKING
        when(problemRepository.findById(1L)).thenReturn(Optional.of(problem));
        when(testCaseService.getInputTestCasesForProblem(problem)).thenReturn(inputs);
        when(testCaseService.getOutputTestCasesForProblem(problem)).thenReturn(outputs);

        when(submissionMapper.toEntity(dto, user, problem, 3)).thenReturn(submission);

        when(submissionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        when(judge0Client.executeAndCompare(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(new ExecutionResultDto());

        when(submissionMapper.toEntity(anyList(), eq(submission)))
                .thenReturn(submission);

        // WHEN
        submissionService.submitCode(dto, user);

        // THEN
        verify(submissionRepository, atLeast(3)).save(any()); // saved for each test case
        verify(judge0Client, times(3)).executeAndCompare(any(), any(), any(), any(), anyInt(), anyInt());
    }

    // -------------------------------------------------------------------------
    // TEST 2: submitCode() when problem does not exist
    // -------------------------------------------------------------------------
    @Test
    void submitCode_ShouldThrow_WhenProblemNotFound() {
        SubmissionRequestDto dto = new SubmissionRequestDto();
        dto.setProblemId(999L);
        User user = new User();

        when(problemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> submissionService.submitCode(dto, user));
    }

    // -------------------------------------------------------------------------
    // TEST 3: getSubmissionsByUser()
    // -------------------------------------------------------------------------
    @Test
    void getSubmissionsByUser_ShouldReturnMappedList() {
        List<Submission> submissions = List.of(new Submission(), new Submission());

        when(submissionRepository.findByUserId(10L)).thenReturn(submissions);
        when(submissionMapper.toListDto(submissions))
                .thenReturn(List.of(new SubmissionListDto()));

        List<SubmissionListDto> result = submissionService.getSubmissionsByUser(10L);

        assertEquals(1, result.size());
        verify(submissionRepository).findByUserId(10L);
    }

    // -------------------------------------------------------------------------
    // TEST 4: getSubmissionStatusById()
    // -------------------------------------------------------------------------
    @Test
    void getSubmissionStatusById_ShouldReturnMappedDto() {
        Submission submission = new Submission();
        submission.setId(100L);

        when(submissionRepository.findById(100L)).thenReturn(Optional.of(submission));
        when(submissionMapper.toListDto(submission))
                .thenReturn(new SubmissionListDto());

        SubmissionListDto dto = submissionService.getSubmissionStatusById(100L);

        assertNotNull(dto);
        verify(submissionRepository).findById(100L);
    }
    // -------------------------------------------------------------------------
    // TEST 5: getSubmissionDetailsById() - SUCCESS
    // -------------------------------------------------------------------------
    @Test
    void getSubmissionDetailsById_ShouldReturnMappedDto() {
        // GIVEN
        Submission submission = new Submission();
        submission.setId(200L);

        SubmissionDetailsDto detailsDto = new SubmissionDetailsDto();
        detailsDto.setProblemTitle("Two Sum");
        detailsDto.setSubmissionStatus("ACCEPTED");

        when(submissionRepository.findById(200L)).thenReturn(Optional.of(submission));
        when(submissionMapper.toDetailsDto(submission)).thenReturn(detailsDto);

        // WHEN
        SubmissionDetailsDto result = submissionService.getSubmissionDetailsById(200L);

        // THEN
        assertNotNull(result);
        assertEquals("Two Sum", result.getProblemTitle());
        verify(submissionRepository).findById(200L);
        verify(submissionMapper).toDetailsDto(submission);
    }

    // -------------------------------------------------------------------------
    // TEST 6: getSubmissionDetailsById() - NOT FOUND
    // -------------------------------------------------------------------------
    @Test
    void getSubmissionDetailsById_ShouldThrow_WhenSubmissionNotFound() {
        when(submissionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> submissionService.getSubmissionDetailsById(999L));
    }
    // -------------------------------------------------------------------------
    // TEST 7: getProblemTitleById() - SUCCESS
    // -------------------------------------------------------------------------
    @Test
    void getProblemTitleById_ShouldReturnTitle() {
        Problem problem = new Problem();
        problem.setId(10L);
        problem.setTitle("Binary Search");

        when(problemRepository.findById(10L)).thenReturn(Optional.of(problem));

        String title = submissionService.getProblemTitleById(10L);

        assertEquals("Binary Search", title);
        verify(problemRepository).findById(10L);
    }

    // -------------------------------------------------------------------------
    // TEST 8: getProblemTitleById() - NOT FOUND
    // -------------------------------------------------------------------------
    @Test
    void getProblemTitleById_ShouldThrow_WhenProblemNotFound() {
        when(problemRepository.findById(777L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> submissionService.getProblemTitleById(777L));
    }
    @Test
    void submitCode_WithMatch_Success() {
        SubmissionRequestDto dto = new SubmissionRequestDto();
        dto.setCode("print(1)");
        dto.setCodeLanguage("PYTHON_3_8");
        dto.setMatchId(10L);

        User user = new User();
        user.setId(1L);

        Problem problem = Problem.builder()
                .id(1L)
                .timeLimit(1000)
                .memoryLimit(128)
                .submissionsCount(5L)
                .build();

        Match match = Match.builder()
                .id(10L)
                .problem(problem)
                .build();

        List<String> inputs = List.of("1");
        List<String> outputs = List.of("1");

        Submission submission = new Submission();
        submission.setId(50L);
        submission.setStatus(SubmissionStatus.WAITING);

        when(matchRepository.findById(10L)).thenReturn(Optional.of(match));
        when(testCaseService.getInputTestCasesForProblem(problem)).thenReturn(inputs);
        when(testCaseService.getOutputTestCasesForProblem(problem)).thenReturn(outputs);
        when(submissionMapper.toEntity(dto, user, problem, 1)).thenReturn(submission);
        when(submissionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(judge0Client.executeAndCompare(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(new ExecutionResultDto());
        when(submissionMapper.toEntity(anyList(), eq(submission))).thenReturn(submission);

        Submission result = submissionService.submitCode(dto, user);

        assertNotNull(result);
        assertEquals(match, submission.getMatch());
        verify(matchRepository).findById(10L);
        verify(problemRepository).save(problem);
        assertEquals(6L, problem.getSubmissionsCount());
    }

    @Test
    void submitCode_CompilationError_StopsExecution() {
        SubmissionRequestDto dto = new SubmissionRequestDto();
        dto.setCode("invalid code");
        dto.setCodeLanguage("JAVA");
        dto.setProblemId(1L);

        User user = new User();
        user.setId(1L);

        Problem problem = Problem.builder()
                .id(1L)
                .timeLimit(1000)
                .memoryLimit(128)
                .submissionsCount(0L)
                .build();

        List<String> inputs = List.of("1", "2", "3");
        List<String> outputs = List.of("1", "2", "3");

        Submission submission = new Submission();
        submission.setId(50L);

        ExecutionResultDto compileError = new ExecutionResultDto();
        compileError.setStatus("Compilation Error");
        compileError.setResult("Compilation Error: syntax error");

        when(problemRepository.findById(1L)).thenReturn(Optional.of(problem));
        when(testCaseService.getInputTestCasesForProblem(problem)).thenReturn(inputs);
        when(testCaseService.getOutputTestCasesForProblem(problem)).thenReturn(outputs);
        when(submissionMapper.toEntity(dto, user, problem, 3)).thenReturn(submission);
        when(submissionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(judge0Client.executeAndCompare(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(compileError);
        when(submissionMapper.toEntity(anyList(), eq(submission))).thenReturn(submission);

        Submission result = submissionService.submitCode(dto, user);

        assertNotNull(result);
        verify(judge0Client, times(1)).executeAndCompare(any(), any(), any(), any(), anyInt(), anyInt());
    }

    @Test
    void submitCode_MatchNotFound_ThrowsException() {
        SubmissionRequestDto dto = new SubmissionRequestDto();
        dto.setMatchId(999L);

        User user = new User();

        when(matchRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> submissionService.submitCode(dto, user));
    }

    @Test
    void getSubmissionsByUser_ReturnsReversedList() {
        List<Submission> submissions = List.of(new Submission(), new Submission());
        List<SubmissionListDto> dtoList = List.of(new SubmissionListDto(), new SubmissionListDto());

        when(submissionRepository.findByUserId(10L)).thenReturn(submissions);
        when(submissionMapper.toListDto(submissions)).thenReturn(dtoList);

        List<SubmissionListDto> result = submissionService.getSubmissionsByUser(10L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(submissionRepository).findByUserId(10L);
    }

    @Test
    void getSubmissionStatusById_NotFound_ThrowsException() {
        when(submissionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> submissionService.getSubmissionStatusById(999L));
    }
}

