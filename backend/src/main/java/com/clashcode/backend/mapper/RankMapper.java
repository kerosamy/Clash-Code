package com.clashcode.backend.mapper;

import org.springframework.stereotype.Component;

@Component
public class RankMapper {
    public int toRank(String outcome) {
        return switch (outcome.toLowerCase()) {
            case "winner" -> 1;
            case "loser" -> 2;
            case "draw" -> 0;
            default -> throw new IllegalArgumentException("Unknown outcome: " + outcome);
        };
    }
}
