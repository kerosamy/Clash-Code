package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.OAuth2Dto;
import com.clashcode.backend.model.User;

public class UserMapper {

    public OAuth2Dto toUserResponseDto(User user) {
        if (user == null) return null;
        return OAuth2Dto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
