package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.UserDto;
import com.clashcode.backend.model.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        if (user == null) return null;
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

}
