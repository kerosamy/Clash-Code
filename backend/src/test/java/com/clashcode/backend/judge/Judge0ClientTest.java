package com.clashcode.backend.judge;

import com.clashcode.backend.dto.ExecutionResultDto;
import com.clashcode.backend.judge.Judge0.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class Judge0ClientTest {

    private RestTemplate mockRestTemplate;
    private Judge0Client judge0Client;

    @BeforeEach
    void setup() {
        // Create mock RestTemplate
        mockRestTemplate = mock(RestTemplate.class);

        // Create Judge0Client instance
        judge0Client = new Judge0Client();

        // Inject mocked RestTemplate and URL using reflection
        ReflectionTestUtils.setField(judge0Client, "restTemplate", mockRestTemplate);
        ReflectionTestUtils.setField(judge0Client, "JUDGE0_URL", "http://judge0");
    }

    @Test
    void test_executeAndCompare_success() {
        // Given
        String sourceCode = "print('Hello')";
        String language = "PYTHON_3_8";
        String testCase = "";
        String expectedResult = "Hello";
        Integer timeLimit = 1000;
        Integer memoryLimit = 128;

        Judge0TokenDto token = new Judge0TokenDto("test-token-123");
        Judge0StatusDto status = new Judge0StatusDto(3, "Accepted");
        Judge0ResponseDto response = new Judge0ResponseDto();
        response.setStdout("SGVsbG8="); // "Hello" in base64
        response.setTime(0.5);
        response.setMemory(1024.0);
        response.setStatus(status);

        when(mockRestTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Judge0TokenDto.class)
        )).thenReturn(ResponseEntity.ok(token));

        when(mockRestTemplate.getForEntity(
                anyString(),
                eq(Judge0ResponseDto.class)
        )).thenReturn(ResponseEntity.ok(response));

        // When
        ExecutionResultDto result = judge0Client.executeAndCompare(
                sourceCode, language, testCase, expectedResult, timeLimit, memoryLimit
        );

        // Then
        assertNotNull(result);
        assertEquals("Accepted", result.getStatus());
        assertEquals(500, result.getTimeTaken());
        assertEquals(1, result.getMemoryTaken());

        // Verify mocks were called
        verify(mockRestTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Judge0TokenDto.class));
        verify(mockRestTemplate, times(1)).getForEntity(anyString(), eq(Judge0ResponseDto.class));
    }

    @Test
    void test_executeAndReturnOutput_withStdout() {
        // Given
        String stdin = "test input";
        String sourceCode = "print(input())";
        String language = "PYTHON_3_8";
        Integer timeLimit = 1000;
        Integer memoryLimit = 128;

        Judge0TokenDto token = new Judge0TokenDto("token-456");
        Judge0StatusDto status = new Judge0StatusDto(3, "Accepted");
        Judge0ResponseDto response = new Judge0ResponseDto();
        response.setStdout("dGVzdCBpbnB1dA=="); // "test input" in base64
        response.setTime(0.1);
        response.setMemory(512.0);
        response.setStatus(status);

        when(mockRestTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Judge0TokenDto.class)
        )).thenReturn(ResponseEntity.ok(token));

        when(mockRestTemplate.getForEntity(
                anyString(),
                eq(Judge0ResponseDto.class)
        )).thenReturn(ResponseEntity.ok(response));

        // When
        String output = judge0Client.executeAndReturnOutput(
                stdin, sourceCode, language, timeLimit, memoryLimit
        );

        // Then
        assertEquals("test input", output);
        verify(mockRestTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Judge0TokenDto.class));
        verify(mockRestTemplate, times(1)).getForEntity(anyString(), eq(Judge0ResponseDto.class));
    }

    @Test
    void test_executeAndReturnOutput_withCompileError() {
        // Given
        Judge0TokenDto token = new Judge0TokenDto("token-789");
        Judge0StatusDto status = new Judge0StatusDto(6, "Compilation Error");
        Judge0ResponseDto response = new Judge0ResponseDto();
        response.setCompileOutput("c29tZSBlcnJvcg=="); // "some error" in base64
        response.setStatus(status);

        when(mockRestTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Judge0TokenDto.class)
        )).thenReturn(ResponseEntity.ok(token));

        when(mockRestTemplate.getForEntity(
                anyString(),
                eq(Judge0ResponseDto.class)
        )).thenReturn(ResponseEntity.ok(response));

        // When
        String output = judge0Client.executeAndReturnOutput(
                "", "invalid code", "PYTHON_3_8", 1000, 128
        );

        // Then
        assertTrue(output.startsWith("Compilation Error:"));
        assertTrue(output.contains("some error"));
        verify(mockRestTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Judge0TokenDto.class));
        verify(mockRestTemplate, times(1)).getForEntity(anyString(), eq(Judge0ResponseDto.class));
    }

    @Test
    void test_executeAndReturnOutput_withRuntimeError() {
        // Given
        Judge0TokenDto token = new Judge0TokenDto("token-error");
        Judge0StatusDto status = new Judge0StatusDto(5, "Runtime Error");
        Judge0ResponseDto response = new Judge0ResponseDto();
        response.setStderr("cnVudGltZSBlcnJvcg=="); // "runtime error" in base64
        response.setStatus(status);

        when(mockRestTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Judge0TokenDto.class)
        )).thenReturn(ResponseEntity.ok(token));

        when(mockRestTemplate.getForEntity(
                anyString(),
                eq(Judge0ResponseDto.class)
        )).thenReturn(ResponseEntity.ok(response));

        // When
        String output = judge0Client.executeAndReturnOutput(
                "", "1/0", "PYTHON_3_8", 1000, 128
        );

        // Then
        assertTrue(output.startsWith("Runtime Error:"));
        assertTrue(output.contains("runtime error"));
        verify(mockRestTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Judge0TokenDto.class));
        verify(mockRestTemplate, times(1)).getForEntity(anyString(), eq(Judge0ResponseDto.class));
    }

    @Test
    void test_executeAndReturnOutput_withUnknownError() {
        // Given
        Judge0TokenDto token = new Judge0TokenDto("token-unknown");
        Judge0StatusDto status = new Judge0StatusDto(11, "Internal Error");
        Judge0ResponseDto response = new Judge0ResponseDto();
        response.setStatus(status);

        when(mockRestTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Judge0TokenDto.class)
        )).thenReturn(ResponseEntity.ok(token));

        when(mockRestTemplate.getForEntity(
                anyString(),
                eq(Judge0ResponseDto.class)
        )).thenReturn(ResponseEntity.ok(response));

        // When
        String output = judge0Client.executeAndReturnOutput(
                "", "code", "PYTHON_3_8", 1000, 128
        );

        // Then
        assertEquals("Unknown error occurred", output);
        verify(mockRestTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Judge0TokenDto.class));
        verify(mockRestTemplate, times(1)).getForEntity(anyString(), eq(Judge0ResponseDto.class));
    }

    @Test
    void test_submitCode_success() {
        // Given
        String sourceCode = "print('test')";
        String testCase = "input";
        String language = "PYTHON_3_8";
        String expectedResult = "test";
        Integer timeLimit = 1000;
        Integer memoryLimit = 128;

        Judge0TokenDto expectedToken = new Judge0TokenDto("token-submit");

        when(mockRestTemplate.exchange(
                eq("http://judge0/submissions/?base64_encoded=false&wait=false"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Judge0TokenDto.class)
        )).thenReturn(ResponseEntity.ok(expectedToken));

        // When
        Judge0TokenDto result = judge0Client.submitCode(
                sourceCode, testCase, language, expectedResult, timeLimit, memoryLimit
        );

        // Then
        assertNotNull(result);
        assertEquals("token-submit", result.getToken());

        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(mockRestTemplate).exchange(
                anyString(),
                eq(HttpMethod.POST),
                entityCaptor.capture(),
                eq(Judge0TokenDto.class)
        );

        HttpEntity<String> capturedEntity = entityCaptor.getValue();
        assertEquals(MediaType.APPLICATION_JSON, capturedEntity.getHeaders().getContentType());
    }

    @Test
    void test_submitCode_withInvalidLanguage() {
        // Given
        String sourceCode = "print('test')";
        String testCase = "input";
        String language = "INVALID_LANGUAGE";

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            judge0Client.submitCode(sourceCode, testCase, language, null, 1000, 128);
        });

        // Verify no HTTP calls were made
        verify(mockRestTemplate, never()).exchange(anyString(), any(), any(), any(Class.class));
    }

    @Test
    void test_waitForResult_immediateSuccess() {
        // Given
        Judge0TokenDto token = new Judge0TokenDto("token-immediate");
        Judge0StatusDto status = new Judge0StatusDto(3, "Accepted");
        Judge0ResponseDto response = new Judge0ResponseDto();
        response.setStdout("b3V0cHV0"); // "output" in base64
        response.setStatus(status);

        when(mockRestTemplate.getForEntity(
                eq("http://judge0/submissions/token-immediate?base64_encoded=true"),
                eq(Judge0ResponseDto.class)
        )).thenReturn(ResponseEntity.ok(response));

        // When
        Judge0ResponseDto result = judge0Client.waitForResult(token);

        // Then
        assertNotNull(result);
        assertEquals("output", result.getStdout());
        assertEquals(3, result.getStatus().getId());
        verify(mockRestTemplate, times(1)).getForEntity(anyString(), eq(Judge0ResponseDto.class));
    }

    @Test
    void test_waitForResult_pollsMultipleTimes() {
        // Given
        Judge0TokenDto token = new Judge0TokenDto("token-polling");

        Judge0StatusDto processingStatus = new Judge0StatusDto(1, "In Queue");
        Judge0ResponseDto processingResponse = new Judge0ResponseDto();
        processingResponse.setStatus(processingStatus);

        Judge0StatusDto runningStatus = new Judge0StatusDto(2, "Processing");
        Judge0ResponseDto runningResponse = new Judge0ResponseDto();
        runningResponse.setStatus(runningStatus);

        Judge0StatusDto finalStatus = new Judge0StatusDto(3, "Accepted");
        Judge0ResponseDto finalResponse = new Judge0ResponseDto();
        finalResponse.setStdout("ZmluYWw="); // "final" in base64
        finalResponse.setStatus(finalStatus);

        when(mockRestTemplate.getForEntity(
                eq("http://judge0/submissions/token-polling?base64_encoded=true"),
                eq(Judge0ResponseDto.class)
        ))
                .thenReturn(ResponseEntity.ok(processingResponse))
                .thenReturn(ResponseEntity.ok(runningResponse))
                .thenReturn(ResponseEntity.ok(finalResponse));

        // When
        Judge0ResponseDto result = judge0Client.waitForResult(token);

        // Then
        assertNotNull(result);
        assertEquals("final", result.getStdout());
        assertEquals(3, result.getStatus().getId());
        verify(mockRestTemplate, times(3)).getForEntity(anyString(), eq(Judge0ResponseDto.class));
    }

    @Test
    void test_waitForResult_timeout() {
        // Given
        Judge0TokenDto token = new Judge0TokenDto("token-timeout");
        Judge0StatusDto processingStatus = new Judge0StatusDto(1, "In Queue");
        Judge0ResponseDto processingResponse = new Judge0ResponseDto();
        processingResponse.setStatus(processingStatus);

        when(mockRestTemplate.getForEntity(
                eq("http://judge0/submissions/token-timeout?base64_encoded=true"),
                eq(Judge0ResponseDto.class)
        )).thenReturn(ResponseEntity.ok(processingResponse));

        // When/Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            judge0Client.waitForResult(token);
        });

        assertTrue(exception.getMessage().contains("Timeout"));
        verify(mockRestTemplate, times(50)).getForEntity(anyString(), eq(Judge0ResponseDto.class));
    }

    @Test
    void test_waitForResult_nullResponse() {
        // Given
        Judge0TokenDto token = new Judge0TokenDto("token-null");

        when(mockRestTemplate.getForEntity(
                eq("http://judge0/submissions/token-null?base64_encoded=true"),
                eq(Judge0ResponseDto.class)
        )).thenReturn(ResponseEntity.ok(null));

        // When/Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            judge0Client.waitForResult(token);
        });

        assertTrue(exception.getMessage().contains("Timeout"));
        verify(mockRestTemplate, times(50)).getForEntity(anyString(), eq(Judge0ResponseDto.class));
    }

    @Test
    void test_waitForResult_nullStatus() {
        // Given
        Judge0TokenDto token = new Judge0TokenDto("token-null-status");
        Judge0ResponseDto response = new Judge0ResponseDto();
        response.setStatus(null);

        when(mockRestTemplate.getForEntity(
                eq("http://judge0/submissions/token-null-status?base64_encoded=true"),
                eq(Judge0ResponseDto.class)
        )).thenReturn(ResponseEntity.ok(response));

        // When/Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            judge0Client.waitForResult(token);
        });

        assertTrue(exception.getMessage().contains("Timeout"));
        verify(mockRestTemplate, times(50)).getForEntity(anyString(), eq(Judge0ResponseDto.class));
    }

    @Test
    void test_waitForResult_decodesAllFields() {
        // Given
        Judge0TokenDto token = new Judge0TokenDto("token-decode");
        Judge0StatusDto status = new Judge0StatusDto(5, "Runtime Error");
        Judge0ResponseDto response = new Judge0ResponseDto();
        response.setStdout("c3Rkb3V0"); // "stdout" in base64
        response.setStderr("c3RkZXJy"); // "stderr" in base64
        response.setCompileOutput("Y29tcGlsZQ=="); // "compile" in base64
        response.setStatus(status);

        when(mockRestTemplate.getForEntity(
                eq("http://judge0/submissions/token-decode?base64_encoded=true"),
                eq(Judge0ResponseDto.class)
        )).thenReturn(ResponseEntity.ok(response));

        // When
        Judge0ResponseDto result = judge0Client.waitForResult(token);

        // Then
        assertEquals("stdout", result.getStdout());
        assertEquals("stderr", result.getStderr());
        assertEquals("compile", result.getCompileOutput());
        verify(mockRestTemplate, times(1)).getForEntity(anyString(), eq(Judge0ResponseDto.class));
    }

    @Test
    void test_waitForResult_interruptedException() {
        // Given
        Judge0TokenDto token = new Judge0TokenDto("token-interrupt");
        Judge0StatusDto processingStatus = new Judge0StatusDto(1, "In Queue");
        Judge0ResponseDto processingResponse = new Judge0ResponseDto();
        processingResponse.setStatus(processingStatus);

        when(mockRestTemplate.getForEntity(
                eq("http://judge0/submissions/token-interrupt?base64_encoded=true"),
                eq(Judge0ResponseDto.class)
        )).thenReturn(ResponseEntity.ok(processingResponse));

        // Interrupt the current thread before calling waitForResult
        Thread.currentThread().interrupt();

        // When/Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            judge0Client.waitForResult(token);
        });

        assertTrue(exception.getMessage().contains("Polling interrupted"));
        assertTrue(Thread.interrupted()); // Clear the interrupt flag
    }

    @Test
    void test_waitForResult_onlyStdoutDecoded() {
        // Given
        Judge0TokenDto token = new Judge0TokenDto("token-stdout-only");
        Judge0StatusDto status = new Judge0StatusDto(3, "Accepted");
        Judge0ResponseDto response = new Judge0ResponseDto();
        response.setStdout("SGVsbG8="); // "Hello" in base64
        response.setStatus(status);

        when(mockRestTemplate.getForEntity(
                eq("http://judge0/submissions/token-stdout-only?base64_encoded=true"),
                eq(Judge0ResponseDto.class)
        )).thenReturn(ResponseEntity.ok(response));

        // When
        Judge0ResponseDto result = judge0Client.waitForResult(token);

        // Then
        assertEquals("Hello", result.getStdout());
        assertNull(result.getStderr());
        assertNull(result.getCompileOutput());
    }
}