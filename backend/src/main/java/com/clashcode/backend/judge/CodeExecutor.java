package com.clashcode.backend.judge;

import com.clashcode.backend.dto.ExecutionResultDto;

import java.util.List;

public interface CodeExecutor {

    List<ExecutionResultDto> executeBatch(String sourceCode,
                                          String language,
                                          List<String> testCases,
                                          List<String> expectedResults );

}
