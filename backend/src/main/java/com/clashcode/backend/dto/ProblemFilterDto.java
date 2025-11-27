package com.clashcode.backend.dto;

import com.clashcode.backend.enums.ProblemTags;

import lombok.*;

import java.util.List;

@Getter
@Data
public class ProblemFilterDto {
    private List<ProblemTags> tags;
    private Integer minRate;
    private Integer maxRate;
}