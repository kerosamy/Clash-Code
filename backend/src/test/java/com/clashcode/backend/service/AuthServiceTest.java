package com.clashcode.backend.service;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.Roles;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.UserRepository;
import com.clashcode.backend.enums.RecoveryQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OAuth2AuthenticationToken oAuth2Token;

    @Mock
    private OAuth2User oAuth2User;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;


    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signup_success() {
        RegisterUserDto input = new RegisterUserDto();
        input.setUsername("newuser");
        input.setEmail("newuser@example.com");
        input.setPassword("pass123");
        input.setRecoveryQuestion(RecoveryQuestion.FIRST_PET.name());
        input.setRecoveryAnswer("Fluffy");

        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setEmail("newuser@example.com");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = authService.signup(input);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("newuser@example.com", result.getEmail());
    }

    @Test
    void signup_emailTaken_throws() {
        RegisterUserDto input = new RegisterUserDto();
        input.setEmail("taken@example.com");
        input.setUsername("user");

        when(userRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(new User()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.signup(input));
        assertEquals("Email Already Taken", ex.getMessage());
    }

    @Test
    void signup_usernameTaken_throws() {
        RegisterUserDto input = new RegisterUserDto();
        input.setEmail("user@example.com");
        input.setUsername("takenuser");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("takenuser")).thenReturn(Optional.of(new User()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.signup(input));
        assertEquals("Username Already Taken", ex.getMessage());
    }

    @Test
    void authenticate_success() {
        LoginUserDto input = new LoginUserDto();
        input.setEmail("user@example.com");
        input.setPassword("pass");

        User user = new User();
        user.setEmail("user@example.com");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        User result = authService.authenticate(input);

        assertNotNull(result);
        assertEquals("user@example.com", result.getEmail());
    }

    @Test
    void authenticate_userNotFound_throws() {
        LoginUserDto input = new LoginUserDto();
        input.setEmail("nouser@example.com");
        input.setPassword("pass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail("nouser@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.authenticate(input));
        assertEquals("User not found", ex.getMessage());
    }

    // GOOGLE
    @Test
    void testHandleOAuth2_NewUser() {
        // Arrange
        when(oAuth2Token.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("newuser@example.com");
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());

        // Act
        authService.handleGoogleOAuth(oAuth2Token);

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
        User result = authService.handleGoogleOAuth(oAuth2Token);

        assertNotNull(result);
        assertEquals("existing@example.com", result.getEmail());
        assertEquals("existinguser", result.getUsername());
        assertEquals(1L, result.getId());
    }

    @Test
    void testCompleteSignUp_Success() {
        SignUpCompletionDto request = new SignUpCompletionDto();
        request.setUsername("newuser");
        request.setEmail("newuser");

        when(oAuth2Token.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("newuser@example.com");
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("newuser@example.com");
        savedUser.setUsername("newuser");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = authService.completeGoogleSignUp(request);

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
                () -> authService.completeGoogleSignUp(request));

        assertEquals("Username already taken", exception.getMessage());
    }

    @Test
    void getRecoveryQuestion_Success() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setRecoveryQuestion(RecoveryQuestion.FIRST_PET);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        String result = authService.getRecoveryQuestion("user@example.com");

        assertEquals("FIRST_PET", result);
        verify(userRepository).findByEmail("user@example.com");
    }

    @Test
    void getRecoveryQuestion_EmailNotFound_Throws() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.getRecoveryQuestion("notfound@example.com"));

        assertEquals("Email not found", ex.getMessage());
    }

    @Test
    void getRecoveryQuestion_GoogleUser_Throws() {
        User user = new User();
        user.setEmail("google@example.com");
        user.setRecoveryQuestion(null);

        when(userRepository.findByEmail("google@example.com")).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.getRecoveryQuestion("google@example.com"));

        assertEquals("Google account user", ex.getMessage());
    }

    @Test
    void verifyRecoveryAnswer_Success() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setRecoveryAnswer("Fluffy");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        boolean result = authService.verifyRecoveryAnswer("user@example.com", "Fluffy");

        assertTrue(result);
    }

    @Test
    void verifyRecoveryAnswer_IncorrectAnswer_Throws() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setRecoveryAnswer("Fluffy");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.verifyRecoveryAnswer("user@example.com", "Wrong"));

        assertEquals("Incorrect recovery answer.", ex.getMessage());
    }

    @Test
    void verifyRecoveryAnswer_CaseInsensitive_Success() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setRecoveryAnswer("Fluffy");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        boolean result = authService.verifyRecoveryAnswer("user@example.com", "fluffy");

        assertTrue(result);
    }

    @Test
    void verifyRecoveryAnswer_WithWhitespace_Success() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setRecoveryAnswer("Fluffy");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        boolean result = authService.verifyRecoveryAnswer("user@example.com", "  Fluffy  ");

        assertTrue(result);
    }

    @Test
    void verifyRecoveryAnswer_NullAnswer_Throws() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setRecoveryAnswer(null);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.verifyRecoveryAnswer("user@example.com", "Any"));

        assertEquals("Incorrect recovery answer.", ex.getMessage());
    }

    @Test
    void verifyRecoveryAnswer_UserNotFound_Throws() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.verifyRecoveryAnswer("notfound@example.com", "Any"));

        assertEquals("Email not found.", ex.getMessage());
    }

    @Test
    void resetPassword_Success() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("oldPassword");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        authService.resetPassword("user@example.com", "newPassword");

        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository).save(user);
        verify(passwordEncoder).encode("newPassword");
    }

    @Test
    void resetPassword_UserNotFound_Throws() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.resetPassword("notfound@example.com", "newPassword"));

        assertEquals("Email not found.", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void handleGoogleOAuth_NewUser_CreatesUser() {
        when(oAuth2Token.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("newuser@example.com");
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());

        User result = authService.handleGoogleOAuth(oAuth2Token);

        assertNotNull(result);
        assertEquals("newuser@example.com", result.getEmail());
        assertEquals(Roles.USER, result.getRole());
        verify(userRepository).findByEmail("newuser@example.com");
    }
}