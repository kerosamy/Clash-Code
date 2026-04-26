package com.clashcode.backend.dto;

import com.clashcode.backend.enums.FriendStatus;
import com.clashcode.backend.enums.UserStatus;
import lombok.*;

import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendDto {
    private String username;
    private Integer currentRate;
    private String imgUrl;
    private UserStatus userStatus;
    private FriendStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime updatedAt;
}