package com.clashcode.backend.dto;

import com.clashcode.backend.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchResponseDto {
    private String username;
    private String rank;
    private UserStatus status;
}
