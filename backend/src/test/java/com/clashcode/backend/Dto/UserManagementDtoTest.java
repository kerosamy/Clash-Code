package com.clashcode.backend.Dto;

import com.clashcode.backend.dto.UserManagementDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserManagementDtoTest {

    @Test
    void testBuilderAndGetters() {
        UserManagementDto user = UserManagementDto.builder()
                .id(1L)
                .username("kero")
                .email("kero@example.com")
                .role("ADMIN")
                .rank("Expert")
                .build();

        assertEquals(1L, user.getId());
        assertEquals("kero", user.getUsername());
        assertEquals("kero@example.com", user.getEmail());
        assertEquals("ADMIN", user.getRole());
        assertEquals("Expert", user.getRank());
    }

    @Test
    void testSetters() {
        UserManagementDto user = new UserManagementDto();

        user.setId(2L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setRole("USER");
        user.setRank("Beginner");

        assertEquals(2L, user.getId());
        assertEquals("alice", user.getUsername());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("USER", user.getRole());
        assertEquals("Beginner", user.getRank());
    }
}
