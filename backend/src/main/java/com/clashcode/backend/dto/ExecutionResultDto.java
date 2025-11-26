package com.clashcode.backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExecutionResultDto {
    private String result;
    private String status;
    private int timeTaken;
    private int memoryTaken;
}
