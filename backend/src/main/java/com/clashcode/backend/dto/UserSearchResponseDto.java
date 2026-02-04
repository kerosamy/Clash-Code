package com.clashcode.backend.dto;

import com.clashcode.backend.enums.UserStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchResponseDto {
    private String username;
    private String rank;
    private UserStatus status;
}
