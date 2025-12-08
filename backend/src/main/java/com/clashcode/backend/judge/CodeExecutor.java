package com.clashcode.backend.judge;

import com.clashcode.backend.dto.ExecutionResultDto;

import java.util.List;

public interface CodeExecutor {
    List<ExecutionResultDto> executeBatch(
        String sourceCode,
        String language,
        List<String> testCases,
        List<String> expectedResults,
        Integer timeLimit,
        Integer memoryLimit
    );

    ExecutionResultDto executeAndCompare(
        String sourceCode,
        String language,
        String testCase,
        String expectedResult,
        Integer timeLimit,
        Integer memoryLimit
    );

    String executeAndReturnOutput(
        String stdin,
        String sourceCode,
        String language,
        Integer timeLimit,
        Integer memoryLimit
    );

}
