package com.clashcode.backend.controller;

import com.clashcode.backend.dto.SignUpCompletionDto;
import com.clashcode.backend.dto.UserDto;
import com.clashcode.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testHandleOAuth2Signup_NewUser() throws Exception {
        OAuth2User oAuth2User = new DefaultOAuth2User(
                Collections.emptyList(),
                Map.of("email", "newuser@example.com"),
                "email"
        );
        OAuth2AuthenticationToken token = org.mockito.Mockito.mock(OAuth2AuthenticationToken.class);
        when(token.getPrincipal()).thenReturn(oAuth2User);

        UserDto mockResponse = UserDto.builder()
                .email("newuser@example.com")
                .build();
        when(userService.handleOAuth2Signup(any(OAuth2AuthenticationToken.class))).thenReturn(mockResponse);


        mockMvc.perform(get("/GoogleSignUp").with(oauth2Login()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.username").doesNotExist());
    }

    @Test
    void testCompleteSignUp() throws Exception {
        SignUpCompletionDto request = new SignUpCompletionDto();
        request.setUsername("newuser");

        UserDto mockResponse = UserDto.builder()
                .id(1L)
                .username("newuser")
                .email("newuser@example.com")
                .build();

        when(userService.completeSignUp(any(SignUpCompletionDto.class), any(OAuth2AuthenticationToken.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/GoogleSignUp/completeRegistration")
                        .with(oauth2Login())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"));
    }
}
