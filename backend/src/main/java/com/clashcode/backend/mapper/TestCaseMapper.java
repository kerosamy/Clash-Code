package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.TestCaseRequestDto;
import com.clashcode.backend.dto.TestCaseResponseDto;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.TestCase;
import org.springframework.stereotype.Component;

@Component
public class TestCaseMapper {
    public TestCaseResponseDto toResponseDto(TestCase testCase) {
        return TestCaseResponseDto.builder()
                .input(testCase.getInput())
                .build();
    }

    public TestCase toEntity(TestCaseRequestDto dto, Problem problem) {
        return TestCase.builder()
                .input(dto.getInput())
                .visible(dto.isVisible())
                .problem(problem)
                .build();
    }
}
