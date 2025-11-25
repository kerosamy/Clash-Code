package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.UserResponseDto;
import com.clashcode.backend.model.User;

public class UserMapper {

    public UserResponseDto toUserResponseDto(User user) {
        if (user == null) return null;
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
