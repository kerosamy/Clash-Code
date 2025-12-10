package com.clashcode.backend.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchParticipantId implements Serializable {
    private Long userId;
    private Long matchId;
}
