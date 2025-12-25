package com.clashcode.backend.judge;

import com.clashcode.backend.judge.Judge0.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Judge0DtoTest {

    @Test
    void test_Judge0RequestDto_builderAndGetters() {
        Judge0RequestDto dto = Judge0RequestDto.builder()
                .sourceCode("code")
                .stdin("input")
                .languageId(71)
                .expectedOutput("output")
                .timeLimit(2.0)
                .memoryLimit(131072.0)
                .wallTimeLimit(4.0)
                .build();

        assertEquals("code", dto.getSourceCode());
        assertEquals("input", dto.getStdin());
        assertEquals(71, dto.getLanguageId());
        assertEquals("output", dto.getExpectedOutput());
        assertEquals(2.0, dto.getTimeLimit());
        assertEquals(131072.0, dto.getMemoryLimit());
        assertEquals(4.0, dto.getWallTimeLimit());
    }

    @Test
    void test_Judge0RequestDto_noArgsConstructor() {
        Judge0RequestDto dto = new Judge0RequestDto();
        assertNull(dto.getSourceCode());
        assertNull(dto.getStdin());
        assertNull(dto.getLanguageId());
    }

    @Test
    void test_Judge0RequestDto_allArgsConstructor() {
        Judge0RequestDto dto = new Judge0RequestDto(
                "code", "input", 71, "output", 2.0, 131072.0, 4.0
        );

        assertEquals("code", dto.getSourceCode());
        assertEquals(71, dto.getLanguageId());
    }

    @Test
    void test_Judge0ResponseDto_settersAndGetters() {
        Judge0ResponseDto dto = new Judge0ResponseDto();
        Judge0StatusDto status = new Judge0StatusDto(3, "Accepted");

        dto.setStdout("output");
        dto.setStderr("error");
        dto.setCompileOutput("compile");
        dto.setTime(1.5);
        dto.setMemory(2048.0);
        dto.setStatus(status);

        assertEquals("output", dto.getStdout());
        assertEquals("error", dto.getStderr());
        assertEquals("compile", dto.getCompileOutput());
        assertEquals(1.5, dto.getTime());
        assertEquals(2048.0, dto.getMemory());
        assertEquals(status, dto.getStatus());
    }

    @Test
    void test_Judge0ResponseDto_builder() {
        Judge0StatusDto status = new Judge0StatusDto(3, "Accepted");
        Judge0ResponseDto dto = Judge0ResponseDto.builder()
                .stdout("output")
                .stderr("error")
                .compileOutput("compile")
                .time(1.5)
                .memory(2048.0)
                .status(status)
                .build();

        assertEquals("output", dto.getStdout());
        assertEquals(1.5, dto.getTime());
        assertEquals(status, dto.getStatus());
    }

    @Test
    void test_Judge0StatusDto_constructor() {
        Judge0StatusDto dto = new Judge0StatusDto(3, "Accepted");

        assertEquals(3, dto.getId());
        assertEquals("Accepted", dto.getDescription());
    }

    @Test
    void test_Judge0StatusDto_settersAndGetters() {
        Judge0StatusDto dto = new Judge0StatusDto();
        dto.setId(5);
        dto.setDescription("Runtime Error");

        assertEquals(5, dto.getId());
        assertEquals("Runtime Error", dto.getDescription());
    }

    @Test
    void test_Judge0TokenDto_constructor() {
        Judge0TokenDto dto = new Judge0TokenDto("my-token-123");

        assertEquals("my-token-123", dto.getToken());
    }

    @Test
    void test_Judge0TokenDto_settersAndGetters() {
        Judge0TokenDto dto = new Judge0TokenDto();
        dto.setToken("new-token-456");

        assertEquals("new-token-456", dto.getToken());
    }

    @Test
    void test_Judge0TokenDto_noArgsConstructor() {
        Judge0TokenDto dto = new Judge0TokenDto();
        assertNull(dto.getToken());
    }
}