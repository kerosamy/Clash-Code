package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.TestCaseRequestDto;
import com.clashcode.backend.dto.TestCaseResponseDto;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.TestCase;
import org.springframework.stereotype.Component;

@Component
public class TestCaseMapper {
    public TestCaseResponseDto toResponseDto(TestCase testCase) {
        TestCaseResponseDto responseDto = new TestCaseResponseDto();
        responseDto.setInput(testCase.getInput());
        return responseDto ;
    }

    public TestCase toEntity(TestCaseRequestDto dto, Problem problem) {
        return TestCase.builder()
                .input(dto.getInput())
                .visible(dto.isVisible())
                .problem(problem)
                .build();
    }
}
