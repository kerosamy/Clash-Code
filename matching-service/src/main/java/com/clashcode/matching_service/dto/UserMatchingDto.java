package com.clashcode.matching_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class UserMatchingDto {
    long userId;
    long startTime;
    int userRating;
}
