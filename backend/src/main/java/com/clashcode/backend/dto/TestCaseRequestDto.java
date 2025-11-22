package com.clashcode.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseRequestDto {
    private String input;

    private boolean visible;
}
