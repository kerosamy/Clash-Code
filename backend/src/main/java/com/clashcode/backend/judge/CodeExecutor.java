package com.clashcode.backend.judge;

import com.clashcode.backend.dto.ExecutionResultDto;


public interface CodeExecutor {
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

    boolean isJudgeAvailable();

}
