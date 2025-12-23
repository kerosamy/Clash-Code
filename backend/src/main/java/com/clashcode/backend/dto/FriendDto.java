package com.clashcode.backend.dto;

import com.clashcode.backend.enums.FriendStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendDto {
    private String username;
    private Integer currentRate;
    private String imgUrl;
    private FriendStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime updatedAt;
}