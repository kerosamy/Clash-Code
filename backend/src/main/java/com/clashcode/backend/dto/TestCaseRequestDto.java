package com.clashcode.backend.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseRequestDto {
    private String input;

    private boolean visible;
}
