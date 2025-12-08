package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.CategoryDto;
import com.clashcode.backend.dto.ProfileDto;
import com.clashcode.backend.dto.RegisterUserDto;
import com.clashcode.backend.dto.StatsDto;
import com.clashcode.backend.enums.RecoveryQuestion;
import com.clashcode.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void testToUser() {
        RegisterUserDto registerDto = new RegisterUserDto();
        registerDto.setUsername("mina");
        registerDto.setEmail("mina@example.com");
        registerDto.setRecoveryQuestion("FIRST_PET");
        registerDto.setRecoveryAnswer("Fluffy");

        String password = "securePassword123";

        User user = userMapper.toUser(registerDto, password);

        assertEquals("mina", user.getUsername());
        assertEquals("mina@example.com", user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(RecoveryQuestion.FIRST_PET, user.getRecoveryQuestion());
        assertEquals("Fluffy", user.getRecoveryAnswer());
        assertEquals(0, user.getMaxRate());
        assertEquals(0, user.getCurrentRate());
    }

    @Test
    void testToUser_withNullRecoveryAnswer() {
        RegisterUserDto registerDto = new RegisterUserDto();
        registerDto.setUsername("john");
        registerDto.setEmail("john@example.com");
        registerDto.setRecoveryQuestion("FAVORITE_MOVIE");
        registerDto.setRecoveryAnswer(null);

        String password = "abc123";

        User user = userMapper.toUser(registerDto, password);

        assertEquals("john", user.getUsername());
        assertEquals("john@example.com", user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(RecoveryQuestion.FAVORITE_MOVIE, user.getRecoveryQuestion());
        assertNull(user.getRecoveryAnswer());
    }

    @Test
    void testToUserProfile() {
        User user = User.builder()
                .username("caroline")
                .currentRate(1200)
                .maxRate(1300)
                .imgUrl("https://example.com/avatar.png")
                .build();

        StatsDto stats = new StatsDto();
        stats.setSolvedProblems(100);
        stats.setAttemptedProblems(50);
        stats.setMatchesPlayed(20);
        stats.setMatchesWon(10);

        CategoryDto category1 = new CategoryDto("DP", 20);
        CategoryDto category2 = new CategoryDto("GRAPH", 30);

        ProfileDto profile = userMapper.toUserProfile(user, "DIAMOND", 5, stats, new CategoryDto[]{category1, category2});

        assertEquals("caroline", profile.getUsername());
        assertEquals("DIAMOND", profile.getRank());
        assertEquals(1200, profile.getCurrentRate());
        assertEquals(1300, profile.getMaxRate());
        assertEquals(5, profile.getFriendCount());
        assertEquals("https://example.com/avatar.png", profile.getAvatarUrl());
        assertEquals(stats, profile.getStats());
        assertEquals(2, profile.getCategories().length);
        assertEquals("DP", profile.getCategories()[0].getName());
        assertEquals("GRAPH", profile.getCategories()[1].getName());
    }

    @Test
    void testToUserProfile_withNullCategories() {
        User user = User.builder()
                .username("alex")
                .currentRate(500)
                .maxRate(600)
                .imgUrl(null)
                .build();

        StatsDto stats = new StatsDto();

        ProfileDto profile = userMapper.toUserProfile(user, "BRONZE", 0, stats, null);

        assertEquals("alex", profile.getUsername());
        assertEquals("BRONZE", profile.getRank());
        assertEquals(500, profile.getCurrentRate());
        assertEquals(600, profile.getMaxRate());
        assertEquals(0, profile.getFriendCount());
        assertNull(profile.getAvatarUrl());
        assertEquals(stats, profile.getStats());
        assertNull(profile.getCategories());
    }
}
