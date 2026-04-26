package com.clashcode.backend.service;

import com.clashcode.backend.enums.Roles;
import com.clashcode.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private long jwtExpiration = 3600000;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", jwtExpiration);
    }

    @Test
    void generateToken_Success() {
        User user = User.builder()
                .email("test@example.com")
                .username("testuser")
                .role(Roles.USER)
                .build();

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_Success() {
        User user = User.builder()
                .email("test@example.com")
                .username("testuser")
                .role(Roles.USER)
                .build();

        String token = jwtService.generateToken(user);
        String extractedEmail = jwtService.extractUsername(token);

        assertEquals("test@example.com", extractedEmail);
    }

    @Test
    void isTokenValid_ValidToken_ReturnsTrue() {
        User user = User.builder()
                .email("test@example.com")
                .username("testuser")
                .role(Roles.USER)
                .build();

        String token = jwtService.generateToken(user);
        boolean isValid = jwtService.isTokenValid(token, user);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_WrongUser_ReturnsFalse() {
        User user1 = User.builder()
                .email("user1@example.com")
                .username("user1")
                .role(Roles.USER)
                .build();

        User user2 = User.builder()
                .email("user2@example.com")
                .username("user2")
                .role(Roles.USER)
                .build();

        String token = jwtService.generateToken(user1);
        boolean isValid = jwtService.isTokenValid(token, user2);

        assertFalse(isValid);
    }

    @Test
    void getExpirationTime_ReturnsCorrectValue() {
        long expiration = jwtService.getExpirationTime();

        assertEquals(jwtExpiration, expiration);
    }

    @Test
    void extractClaim_Success() {
        User user = User.builder()
                .email("test@example.com")
                .username("testuser")
                .role(Roles.USER)
                .build();

        String token = jwtService.generateToken(user);
        String subject = jwtService.extractClaim(token, io.jsonwebtoken.Claims::getSubject);

        assertEquals("test@example.com", subject);
    }

    @Test
    void generateToken_WithExtraClaims_Success() {
        User user = User.builder()
                .email("test@example.com")
                .username("testuser")
                .role(Roles.ADMIN)
                .build();

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("customClaim", "customValue");

        String token = jwtService.generateToken(extraClaims, user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }
}
