package com.clashcode.backend.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {
    String username;
    String rank;
    int currentRate;
    int maxRate;
    int friendCount;
    String avatarUrl;

    StatsDto stats;
    CategoryDto[] categories;
}
