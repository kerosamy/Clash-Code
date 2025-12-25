package com.clashcode.backend.dto;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserManagementDto {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String rank;
}