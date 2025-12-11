package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.RecoveryQuestion;
import com.clashcode.backend.enums.Roles;
import com.clashcode.backend.model.User;

public class UserMapper {
    public User toUser(RegisterUserDto input, String password) {
        return User.builder()
                .username(input.getUsername())
                .email(input.getEmail())
                .password(password)
                .recoveryQuestion(RecoveryQuestion.valueOf(input.getRecoveryQuestion()))
                .recoveryAnswer(input.getRecoveryAnswer())
                .role(Roles.USER)
                .maxRate(0)
                .currentRate(0)
                .build();
    }

    public ProfileDto toUserProfile(
            User user,
            String rank,
            int friendCount,
            StatsDto stats,
            CategoryDto[] categories
    ) {
        return ProfileDto.builder()
                .username(user.getUsername())
                .rank(rank)
                .currentRate(user.getCurrentRate())
                .maxRate(user.getMaxRate())
                .friendCount(friendCount)
                .avatarUrl(user.getImgUrl())
                .stats(stats)
                .categories(categories)
                .build();
    }

    public UserManagementDto toUserManagementDto(User user, String rank) {
        return UserManagementDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .rank(rank)
                .build();
    }

}
