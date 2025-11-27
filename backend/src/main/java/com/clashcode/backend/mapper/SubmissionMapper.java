package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.SubmissionRequestDto;
import com.clashcode.backend.enums.LanguageVersion;
import com.clashcode.backend.model.Submission;
import org.springframework.stereotype.Component;

@Component
public class SubmissionMapper {
    public Submission toEntity (SubmissionRequestDto requestDto) {
        return Submission.builder()
                .code(requestDto.getCode())
                .languageVersion(LanguageVersion.valueOf(requestDto.getCodeLanguage()))
                .build();
    }
}
