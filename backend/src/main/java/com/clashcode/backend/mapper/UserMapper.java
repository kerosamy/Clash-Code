package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.CategoryDto;
import com.clashcode.backend.dto.ProfileDto;
import com.clashcode.backend.dto.RegisterUserDto;
import com.clashcode.backend.dto.StatsDto;
import com.clashcode.backend.enums.RecoveryQuestion;
import com.clashcode.backend.model.User;

public class UserMapper {
    public User toUser(RegisterUserDto input, String password) {
        return User.builder()
                .username(input.getUsername())
                .email(input.getEmail())
                .password(password)
                .recoveryQuestion(RecoveryQuestion.valueOf(input.getRecoveryQuestion()))
                .recoveryAnswer(input.getRecoveryAnswer())
                .isAdmin(false)
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
}
