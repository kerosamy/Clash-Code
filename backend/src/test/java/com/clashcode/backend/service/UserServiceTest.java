package com.clashcode.backend.service;

import com.clashcode.backend.dto.ProfileDto;
import com.clashcode.backend.dto.UserSearchResponseDto;
import com.clashcode.backend.exception.UserNotFoundException;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetProfile_AssemblesCorrectProfile() {
        User user = new User();
        user.setId(1L);
        user.setUsername("mina");
        user.setCurrentRate(1140);
        user.setMaxRate(1200);
        user.setImgUrl("https://example.com/avatar.png");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ProfileDto profile = userService.getProfile(user);

        assertEquals("mina", profile.getUsername());
        assertEquals("DIAMOND", profile.getRank());
        assertEquals(1140, profile.getCurrentRate());
        assertEquals(1200, profile.getMaxRate());
        assertEquals(20, profile.getFriendCount());

        assertNotNull(profile.getStats());
        assertNotNull(profile.getCategories());
    }

    @Test
    void testGetProfile_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getProfile(99L));
    }

    @Test
    void testSearchByUsername_Found() {
        // Arrange
        User user1 = new User();
        user1.setUsername("caro");
        user1.setCurrentRate(1200);

        User user2 = new User();
        user2.setUsername("caroline");
        user2.setCurrentRate(1500);

        when(userRepository.findByUsernameContainingIgnoreCase("car"))
                .thenReturn(List.of(user1, user2));

        // Act
        List<UserSearchResponseDto> results = userService.searchByUsername("car");

        // Assert
        assertEquals(2, results.size());
        assertEquals("caro", results.get(0).getUsername());
        assertEquals("MASTER", results.get(0).getRank());
        assertEquals("caroline", results.get(1).getUsername());
        assertEquals("CHAMPION", results.get(1).getRank());
    }


    @Test
    void testSearchByUsername_NoMatch() {
        when(userRepository.findByUsernameContainingIgnoreCase("mo"))
                .thenReturn(List.of());

        List<UserSearchResponseDto> results = userService.searchByUsername("mo");

        assertTrue(results.isEmpty());
    }
}
