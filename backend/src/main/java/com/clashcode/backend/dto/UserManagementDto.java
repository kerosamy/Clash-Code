package com.clashcode.backend.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserManagementDto {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String rank;
}