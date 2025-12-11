package com.clashcode.backend.dto;


import com.clashcode.backend.enums.FriendRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendDto {
    private String username;
    private String rank;
    private String imgUrl;
    private FriendRequestStatus status;
    private Long requestedAt;
    private Long updatedAt;
}
