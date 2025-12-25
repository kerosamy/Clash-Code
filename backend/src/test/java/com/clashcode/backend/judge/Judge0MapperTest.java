package com.clashcode.backend.judge;

import com.clashcode.backend.dto.ExecutionResultDto;
import com.clashcode.backend.enums.LanguageVersion;
import com.clashcode.backend.judge.Judge0.Judge0Mapper;
import com.clashcode.backend.judge.Judge0.Judge0RequestDto;
import com.clashcode.backend.judge.Judge0.Judge0ResponseDto;
import com.clashcode.backend.judge.Judge0.Judge0StatusDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Judge0MapperTest {

    private Judge0Mapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new Judge0Mapper();
    }

    @Test
    void testMapToJudge0Id() {
        // Python 3.8
        assertEquals(71, mapper.mapToJudge0Id(LanguageVersion.PYTHON_3_8));
        // Java 11
        assertEquals(63, mapper.mapToJudge0Id(LanguageVersion.JAVA_OPENJDK_11));
        // C++ GCC 9.2
        assertEquals(54, mapper.mapToJudge0Id(LanguageVersion.CPP_GCC_9_2));
    }

    @Test
    void testToExecutionResultDto() {
        Judge0ResponseDto response = new Judge0ResponseDto();
        response.setTime(1.5); // seconds
        response.setMemory(2048.0); // KB
        response.setStdout("Hello World");
        response.setStatus(new Judge0StatusDto( 1,"Accepted"));

        ExecutionResultDto result = mapper.toExecutionResultDto(response);

        assertEquals(1500, result.getTimeTaken()); // 1.5s → 1500ms
        assertEquals(2, result.getMemoryTaken()); // 2048KB → 2MB
        assertEquals("Accepted", result.getStatus());
        assertEquals("Hello World", result.getResult());
    }

    @Test
    void testToRequestDto() {
        String code = "print('Hello')";
        String input = "input";
        String language = "PYTHON_3_8";
        String expected = "Hello";
        int timeLimitMs = 2000;
        int memoryLimitMb = 128;

        Judge0RequestDto request = mapper.toRequestDto(code, input, language, expected, timeLimitMs, memoryLimitMb);

        assertEquals(code, request.getSourceCode());
        assertEquals(input, request.getStdin());
        assertEquals(71, request.getLanguageId()); // Python 3.8
        assertEquals(expected, request.getExpectedOutput());
        assertEquals(131072.0, request.getMemoryLimit()); // 128 MB → 128*1024 KB
        assertEquals(2.0, request.getTimeLimit()); // 2000ms → 2s
        assertEquals(4.0, request.getWallTimeLimit()); // 2 * CPU time
    }

    @Test
    void testDecode_validBase64() {
        String encoded = "SGVsbG8gV29ybGQ="; // "Hello World"
        String decoded = mapper.decode(encoded);
        assertEquals("Hello World", decoded);
    }

    @Test
    void testDecode_invalidBase64_returnsOriginal() {
        String invalid = "invalid@@base64";
        String decoded = mapper.decode(invalid);
        assertEquals(invalid, decoded);
    }

    @Test
    void testDecode_withWhitespaceAndQuotes() {
        String encoded = "\"SGVsbG8gV29ybGQ=\"\n ";
        String decoded = mapper.decode(encoded);
        assertEquals("Hello World", decoded);
    }
}
