package com.clashcode.backend.dto;

import com.clashcode.backend.enums.UserStatus;
import lombok.*;

@Getter
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
    UserStatus userStatus;
    StatsDto stats;
    CategoryDto[] categories;
}
