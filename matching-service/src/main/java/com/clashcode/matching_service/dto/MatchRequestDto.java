package com.clashcode.matching_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class MatchRequestDto {
    long userId;
    int userRating;
}
