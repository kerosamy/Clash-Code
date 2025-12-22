package com.clashcode.matching_service.main_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MatchCreationDto {
    private Long playerIdA;
    private Long playerIdB;
}

