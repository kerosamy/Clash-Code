package com.clashcode.backend.service;

import com.clashcode.backend.dto.ProfileDto;
import com.clashcode.backend.dto.SignUpCompletionDto;
import com.clashcode.backend.dto.UserSearchResponse;
import com.clashcode.backend.exception.UserNotFoundException;
import com.clashcode.backend.dto.OAuth2Dto;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OAuth2ServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OAuth2AuthenticationToken oAuth2Token;

    @Mock
    private OAuth2User oAuth2User;

    @InjectMocks
    private OAuth2Service OAuth2Service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleOAuth2_NewUser() {
        // Arrange
        when(oAuth2Token.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("newuser@example.com");
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(null);

        // Act
        OAuth2Service.handleOAuth2(oAuth2Token);

        verify(userRepository).findByEmail("newuser@example.com");
    }

    @Test
    void testHandleOAuth2_ExistingUser() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("existing@example.com");
        existingUser.setUsername("existinguser");

        when(oAuth2Token.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("existing@example.com");
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        // Act
        OAuth2Dto result = OAuth2Service.handleOAuth2(oAuth2Token);

        assertNotNull(result);
        assertEquals("existing@example.com", result.getEmail());
        assertEquals("existinguser", result.getUsername());
        assertEquals(1L, result.getId());
    }

    @Test
    void testCompleteSignUp_Success() {
        SignUpCompletionDto request = new SignUpCompletionDto();
        request.setUsername("newuser");

        when(oAuth2Token.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("newuser@example.com");
        when(userRepository.findByUsername("newuser")).thenReturn(null);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("newuser@example.com");
        savedUser.setUsername("newuser");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        OAuth2Dto result = OAuth2Service.completeSignUp(request, oAuth2Token);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("newuser", result.getUsername());
        assertEquals("newuser@example.com", result.getEmail());

        verify(userRepository).findByUsername("newuser");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCompleteSignUp_UsernameTaken() {
        SignUpCompletionDto request = new SignUpCompletionDto();
        request.setUsername("takenuser");

        when(oAuth2Token.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("newuser@example.com");
        when(userRepository.findByUsername("takenuser"))
                .thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> OAuth2Service.completeSignUp(request, oAuth2Token));

        assertEquals("Username already taken", exception.getMessage());
    }

    @Test
    void testGetProfile_AssemblesCorrectProfile() {
        User user = new User();
        user.setId(1L);
        user.setUsername("mina");
        user.setCurrentRate(1140);
        user.setMaxRate(1200);
        user.setImgUrl("https://example.com/avatar.png");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ProfileDto profile = OAuth2Service.getProfile(1L);

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

        assertThrows(UserNotFoundException.class, () -> OAuth2Service.getProfile(99L));
    }


    @Test
    void testSearchByUsername_Found() {
        // Arrange
        User user1 = new User();
        user1.setUsername("alice");
        user1.setCurrentRate(1200);

        User user2 = new User();
        user2.setUsername("alicia");
        user2.setCurrentRate(1500);

        when(userRepository.findByUsernameContainingIgnoreCase("ali"))
                .thenReturn(List.of(user1, user2));

        // Act
        List<UserSearchResponse> results = OAuth2Service.searchByUsername("ali");

        // Assert
        assertEquals(2, results.size());
        assertEquals("alice", results.get(0).getUsername());
        assertEquals("MASTER", results.get(0).getRank());
        assertEquals("alicia", results.get(1).getUsername());
        assertEquals("CHAMPION", results.get(1).getRank());
    }


    @Test
    void testSearchByUsername_NoMatch() {
        when(userRepository.findByUsernameContainingIgnoreCase("mo"))
                .thenReturn(List.of());

        List<UserSearchResponse> results = OAuth2Service.searchByUsername("mo");

        assertTrue(results.isEmpty());
    }
}