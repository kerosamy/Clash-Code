package com.clashcode.backend.controller;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.exception.UnauthorizedException;
import com.clashcode.backend.model.User;
import com.clashcode.backend.service.AuthService;
import com.clashcode.backend.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /auth/signup - Success")
    void test_Register_Success() throws Exception {
        RegisterUserDto request = new RegisterUserDto();
        request.setEmail("test@example.com");
        request.setUsername("testuser");
        request.setPassword("password");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setUsername("testuser");

        when(authService.signup(any(RegisterUserDto.class))).thenReturn(mockUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock-token");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-token"));
    }

    @Test
    @DisplayName("POST /auth/signup - Failure")
    void test_Register_Failure() throws Exception {
        RegisterUserDto request = new RegisterUserDto();
        request.setEmail("test@example.com");
        request.setUsername("testuser");
        request.setPassword("password");

        when(authService.signup(any(RegisterUserDto.class)))
                .thenThrow(new RuntimeException("Email already exists"));

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already exists"));
    }

    @Test
    @DisplayName("POST /auth/login - Success")
    void test_Authenticate_Success() throws Exception {
        LoginUserDto request = new LoginUserDto();
        request.setEmail("test@example.com");
        request.setPassword("password");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setUsername("testuser");

        when(authService.authenticate(any(LoginUserDto.class))).thenReturn(mockUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-token"));
    }

    @Test
    @DisplayName("POST /auth/recovery-question - Success")
    void test_GetRecoveryQuestion_Success() throws Exception {
        String email = "test@example.com";
        String question = "What is your pet's name?";

        when(authService.getRecoveryQuestion(email)).thenReturn(question);

        mockMvc.perform(post("/auth/recovery-question")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(email))
                .andExpect(status().isOk())
                .andExpect(content().string(question));
    }

    @Test
    @DisplayName("POST /auth/recovery-question - Failure")
    void test_GetRecoveryQuestion_Failure() throws Exception {
        String email = "unknown@example.com";

        when(authService.getRecoveryQuestion(email))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(post("/auth/recovery-question")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(email))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    @DisplayName("POST /auth/verify-recovery - Success")
    void test_VerifyRecoveryAnswer_Success() throws Exception {
        VerifyRecoveryDto request = new VerifyRecoveryDto();
        request.setEmail("test@example.com");
        request.setAnswer("Fluffy");

        mockMvc.perform(post("/auth/verify-recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /auth/verify-recovery - Failure")
    void test_VerifyRecoveryAnswer_Failure() throws Exception {
        VerifyRecoveryDto request = new VerifyRecoveryDto();
        request.setEmail("test@example.com");
        request.setAnswer("WrongAnswer");

        when(authService.verifyRecoveryAnswer(any(), any()))
                .thenThrow(new RuntimeException("Incorrect answer"));

        mockMvc.perform(post("/auth/verify-recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Incorrect answer"));
    }

    @Test
    @DisplayName("POST /auth/reset-password - Success")
    void test_ResetPassword_Success() throws Exception {
        PasswordResetDto request = new PasswordResetDto();
        request.setEmail("test@example.com");
        request.setNewPassword("newPassword123");

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /auth/reset-password - Failure")
    void test_ResetPassword_Failure() throws Exception {
        PasswordResetDto request = new PasswordResetDto();
        request.setEmail("test@example.com");
        request.setNewPassword("newPassword123");

        doThrow(new RuntimeException("User not found"))
                .when(authService).resetPassword(any(), any());


        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

//    @Test
//    @DisplayName("GET /auth/OAuthCallback - Success")
//    void test_HandleGoogleOAuth_Success() throws Exception {
//        OAuth2AuthenticationToken authToken = mock(OAuth2AuthenticationToken.class);
//
//        User mockUser = new User();
//        mockUser.setId(1L);
//        mockUser.setEmail("oauthuser@example.com");
//        mockUser.setUsername("oauthuser");
//
//        when(authService.handleGoogleOAuth(any(OAuth2AuthenticationToken.class))).thenReturn(mockUser);
//        when(jwtService.generateToken(any(User.class))).thenReturn("mock-token");
//
//        mockMvc.perform(get("/auth/OAuthCallback")
//                        .principal(authToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").value("mock-token"));
//    }

    @Test
    @DisplayName("GET /auth/me - Success")
    void testGetMe_Success() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setUsername("testuser");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList())
        );

        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /auth/me - Unauthorized")
    void testGetMe_Unauthorized() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Direct buildAuthResponse - returns token")
    void testBuildAuthResponse() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("direct@example.com");
        mockUser.setUsername("directuser");

        when(jwtService.generateToken(any(User.class))).thenReturn("direct-token");

        AuthController controller = new AuthController(jwtService, authService);
        var response = controller.buildAuthResponse(mockUser);

        assert response.getBody().getToken().equals("direct-token");
    }
}
