package com.clashcode.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyRecoveryDto {
    private String email;
    private String answer;
}