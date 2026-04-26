package com.clashcode.backend.dto;

import com.clashcode.backend.enums.FriendStatus;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchDto {
    private String username;
    private int currentRate;
    private FriendStatus friendStatus;
}
