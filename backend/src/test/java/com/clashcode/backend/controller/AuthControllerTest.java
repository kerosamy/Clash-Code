package com.clashcode.backend.controller;

import com.clashcode.backend.dto.LoginUserDto;
import com.clashcode.backend.dto.RegisterUserDto;
import com.clashcode.backend.dto.SignUpCompletionDto;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void testRegister_Success() throws Exception {
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
        when(jwtService.getExpirationTime()).thenReturn(3600L);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-token"))
                .andExpect(jsonPath("$.expiresIn").value(3600));
    }

    @Test
    @DisplayName("POST /auth/login - Success")
    void testAuthenticate_Success() throws Exception {
        LoginUserDto request = new LoginUserDto();
        request.setEmail("test@example.com");
        request.setPassword("password");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setUsername("testuser");

        when(authService.authenticate(any(LoginUserDto.class))).thenReturn(mockUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock-token");
        when(jwtService.getExpirationTime()).thenReturn(3600L);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-token"))
                .andExpect(jsonPath("$.expiresIn").value(3600));
    }

    @Test
    @DisplayName("GET /auth/OAuthCallback - Success")
    void testHandleGoogleOAuth_Success() throws Exception {
        OAuth2AuthenticationToken authToken = mock(OAuth2AuthenticationToken.class);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("oauthuser@example.com");
        mockUser.setUsername("oauthuser");

        when(authService.handleGoogleOAuth(any(OAuth2AuthenticationToken.class))).thenReturn(mockUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock-token");
        when(jwtService.getExpirationTime()).thenReturn(3600L);

        mockMvc.perform(get("/auth/OAuthCallback")
                        .principal(authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-token"))
                .andExpect(jsonPath("$.expiresIn").value(3600));
    }

    @Test
    @DisplayName("POST /auth/GoogleSignUp/completeRegistration - Success")
    void testCompleteGoogleSignUp_Success() throws Exception {
        SignUpCompletionDto request = new SignUpCompletionDto();
        request.setUsername("newuser");

        OAuth2AuthenticationToken authToken = mock(OAuth2AuthenticationToken.class);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("newuser@example.com");
        mockUser.setUsername("newuser");

        when(authService.completeGoogleSignUp(any(SignUpCompletionDto.class), any(OAuth2AuthenticationToken.class)))
                .thenReturn(mockUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock-token");
        when(jwtService.getExpirationTime()).thenReturn(3600L);

        mockMvc.perform(post("/auth/GoogleSignUp/completeRegistration")
                        .principal(authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-token"))
                .andExpect(jsonPath("$.expiresIn").value(3600));
    }
}