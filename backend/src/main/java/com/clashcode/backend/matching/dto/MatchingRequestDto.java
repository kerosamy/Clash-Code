package com.clashcode.backend.matching.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MatchingRequestDto {
    long userId;
    int userRating;
}
