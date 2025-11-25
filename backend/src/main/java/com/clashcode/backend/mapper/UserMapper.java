package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.UserResponseDto;
import com.clashcode.backend.model.User;

public class UserMapper {

    public UserResponseDto toUserResponseDto(User user) {
        if (user == null) return null;
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
