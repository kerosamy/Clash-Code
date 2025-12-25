package com.clashcode.backend.Model;

import com.clashcode.backend.enums.*;
import com.clashcode.backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserGettersAndSetters() {
        User user = new User();
        LocalDateTime now = LocalDateTime.now();

        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole(Roles.ADMIN);
        user.setCreatedAt(now);
        user.setImgUrl("http://example.com/img.jpg");
        user.setMaxRate(2000);
        user.setCurrentRate(1800);
        user.setRecoveryQuestion(RecoveryQuestion.FIRST_PET);
        user.setRecoveryAnswer("Fluffy");

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(Roles.ADMIN, user.getRole());
        assertEquals(now, user.getCreatedAt());
        assertEquals("http://example.com/img.jpg", user.getImgUrl());
        assertEquals(2000, user.getMaxRate());
        assertEquals(1800, user.getCurrentRate());
        assertEquals(RecoveryQuestion.FIRST_PET, user.getRecoveryQuestion());
        assertEquals("Fluffy", user.getRecoveryAnswer());
    }

    @Test
    void testUserBuilder() {
        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .id(2L)
                .username("builder")
                .email("builder@example.com")
                .password("pass")
                .role(Roles.USER)
                .createdAt(now)
                .imgUrl("http://img.com")
                .maxRate(1500)
                .currentRate(1400)
                .recoveryQuestion(RecoveryQuestion.FIRST_PET)
                .recoveryAnswer("Cairo")
                .build();

        assertEquals(2L, user.getId());
        assertEquals("builder", user.getUsername());
        assertEquals(Roles.USER, user.getRole());
        assertEquals(1500, user.getMaxRate());
    }

    @Test
    void testUserBuilderDefaults() {
        User user = User.builder()
                .username("default")
                .email("default@example.com")
                .password("pass")
                .build();

        assertEquals(Roles.USER, user.getRole());
        assertEquals(0, user.getMaxRate());
        assertEquals(0, user.getCurrentRate());
    }

    @Test
    void testUserNoArgsConstructor() {
        User user = new User();
        assertNotNull(user);
    }

    @Test
    void testUserAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User(1L, "allargs", "allargs@example.com", "pass",
                Roles.SUPER_ADMIN, now, "http://img.com", 2000, 1900,
                RecoveryQuestion.FAVORITE_MOVIE, "1984");

        assertEquals(1L, user.getId());
        assertEquals("allargs", user.getUsername());
        assertEquals(Roles.SUPER_ADMIN, user.getRole());
        assertEquals(2000, user.getMaxRate());
    }

    @Test
    void testUserGetAuthorities_SuperAdmin() {
        User user = User.builder()
                .username("superadmin")
                .email("superadmin@example.com")
                .role(Roles.SUPER_ADMIN)
                .build();

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertEquals(3, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testUserGetAuthorities_Admin() {
        User user = User.builder()
                .username("admin")
                .email("admin@example.com")
                .role(Roles.ADMIN)
                .build();

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testUserGetAuthorities_User() {
        User user = User.builder()
                .username("user")
                .email("user@example.com")
                .role(Roles.USER)
                .build();

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }
}