package com.clashcode.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDto {
    private String email;
    private String password;
    private String username;
    private String recoveryQuestion;
    private String recoveryAnswer;
}
