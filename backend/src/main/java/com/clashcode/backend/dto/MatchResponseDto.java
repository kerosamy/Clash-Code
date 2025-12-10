package com.clashcode.backend.dto;

import com.clashcode.backend.enums.MatchState;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchResponseDto {
    private Long id;
    private LocalDateTime startAt;
    private Integer duration;
    private MatchState matchState;
    private Long problemId;
    private List<MatchParticipantDto> participants;
}
