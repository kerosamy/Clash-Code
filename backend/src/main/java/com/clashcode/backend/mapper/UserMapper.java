package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.UserDto;
import com.clashcode.backend.model.User;

public class UserMapper {

    public UserDto toUserResponseDto(User user) {
        if (user == null) return null;
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
