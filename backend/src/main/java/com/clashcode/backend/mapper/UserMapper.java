package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.RecoveryQuestion;
import com.clashcode.backend.enums.Roles;
import com.clashcode.backend.enums.UserStatus;
import com.clashcode.backend.model.User;
import org.springframework.stereotype.Component;

@Component
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
            CategoryDto[] categories,
            String imageUrl,
            UserStatus userStatus
    ) {
        return ProfileDto.builder()
                .username(user.getUsername())
                .rank(rank)
                .currentRate(user.getCurrentRate())
                .maxRate(user.getMaxRate())
                .friendCount(friendCount)
                .avatarUrl(imageUrl)  // Now using the full URL passed as parameter
                .stats(stats)
                .categories(categories)
                .userStatus(userStatus)
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

        public LeaderBoardDto toLeaderboardDto(User user) {
            LeaderBoardDto dto = new LeaderBoardDto();
            dto.setUsername(user.getUsername());
            dto.setCurrentRate(user.getCurrentRate());
            return dto;
        }
}

