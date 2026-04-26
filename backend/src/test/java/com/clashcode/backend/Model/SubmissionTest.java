package com.clashcode.backend.Model;

import com.clashcode.backend.enums.*;
import com.clashcode.backend.model.Match;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.Submission;
import com.clashcode.backend.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubmissionTest {

    @Test
    void testSubmissionGettersAndSetters() {
        Submission submission = new Submission();
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        Problem problem = new Problem();
        Match match = new Match();

        submission.setId(1L);
        submission.setSubmittedAt(now);
        submission.setCode("System.out.println()");
        submission.setLanguageVersion(LanguageVersion.JAVA_OPENJDK_13);
        submission.setStatus(SubmissionStatus.ACCEPTED);
        submission.setMemoryTaken(1024);
        submission.setTimeTaken(150);
        submission.setNumberOfTestCases(10);
        submission.setNumberOfPassedTestCases(10);
        submission.setNumberOfCurrentTestCase(10);
        submission.setUser(user);
        submission.setProblem(problem);
        submission.setMatch(match);

        assertEquals(1L, submission.getId());
        assertEquals(now, submission.getSubmittedAt());
        assertEquals("System.out.println()", submission.getCode());
        assertEquals(LanguageVersion.JAVA_OPENJDK_13, submission.getLanguageVersion());
        assertEquals(SubmissionStatus.ACCEPTED, submission.getStatus());
        assertEquals(1024, submission.getMemoryTaken());
        assertEquals(150, submission.getTimeTaken());
        assertEquals(10, submission.getNumberOfTestCases());
        assertEquals(10, submission.getNumberOfPassedTestCases());
        assertEquals(10, submission.getNumberOfCurrentTestCase());
        assertEquals(user, submission.getUser());
        assertEquals(problem, submission.getProblem());
        assertEquals(match, submission.getMatch());
    }

    @Test
    void testSubmissionBuilder() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        Problem problem = new Problem();

        Submission submission = Submission.builder()
                .id(2L)
                .submittedAt(now)
                .code("print('hello')")
                .languageVersion(LanguageVersion.PYTHON_3_8)
                .status(SubmissionStatus.WRONG_ANSWER)
                .memoryTaken(512)
                .timeTaken(200)
                .numberOfTestCases(5)
                .numberOfPassedTestCases(3)
                .numberOfCurrentTestCase(4)
                .user(user)
                .problem(problem)
                .build();

        assertEquals(2L, submission.getId());
        assertEquals(SubmissionStatus.WRONG_ANSWER, submission.getStatus());
        assertEquals(3, submission.getNumberOfPassedTestCases());
    }

    @Test
    void testSubmissionNoArgsConstructor() {
        Submission submission = new Submission();
        assertNotNull(submission);
    }

    @Test
    void testSubmissionAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        Problem problem = new Problem();
        Match match = new Match();

        Submission submission = new Submission(1L, now, "code", LanguageVersion.CPP_CLANG_7_0,
                SubmissionStatus.COMPILATION_ERROR, 256, 100, 10, 5, 6, user, problem, match);

        assertEquals(1L, submission.getId());
        assertEquals(SubmissionStatus.COMPILATION_ERROR, submission.getStatus());
        assertEquals(5, submission.getNumberOfPassedTestCases());
    }
}
