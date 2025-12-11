package com.clashcode.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDto {
    private String token;

    public AuthResponseDto(String token, long expiresIn) {
        this.token = token;
    }
}