package com.clashcode.backend.dto;

import com.clashcode.backend.enums.GameMode;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMatchRequestDto {
    private Long player1Id;
    private Long player2Id;
    private GameMode gameMode;
    private Long problemId;
    private Integer duration;
}
