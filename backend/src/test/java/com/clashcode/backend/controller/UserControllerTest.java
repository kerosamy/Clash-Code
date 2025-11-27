package com.clashcode.backend.controller;

import com.clashcode.backend.dto.CategoryDto;
import com.clashcode.backend.dto.ProfileDto;
import com.clashcode.backend.dto.SignUpCompletionDto;
import com.clashcode.backend.dto.StatsDto;
import com.clashcode.backend.dto.UserResponseDto;
import com.clashcode.backend.exception.UserNotFoundException;
import com.clashcode.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private UserService userService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void testHandleOAuth2_NewUser() throws Exception {
                OAuth2User oAuth2User = new DefaultOAuth2User(
                                Collections.emptyList(),
                                Map.of("email", "newuser@example.com"),
                                "email");
                OAuth2AuthenticationToken token = org.mockito.Mockito.mock(OAuth2AuthenticationToken.class);
                when(token.getPrincipal()).thenReturn(oAuth2User);

                UserResponseDto mockResponse = UserResponseDto.builder()
                                .email("newuser@example.com")
                                .build();
                when(userService.handleOAuth2(any(OAuth2AuthenticationToken.class))).thenReturn(mockResponse);

                mockMvc.perform(get("/users/OAuthCallback").with(oauth2Login()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                                .andExpect(jsonPath("$.username").doesNotExist());
        }

        @Test
        void testCompleteSignUp() throws Exception {
                SignUpCompletionDto request = new SignUpCompletionDto();
                request.setUsername("newuser");

                UserResponseDto mockResponse = new UserResponseDto();
                mockResponse.setId(1L);
                mockResponse.setUsername("newuser");
                mockResponse.setEmail("newuser@example.com");

                when(userService.completeSignUp(any(SignUpCompletionDto.class), any(OAuth2AuthenticationToken.class)))
                                .thenReturn(mockResponse);

                mockMvc.perform(post("/users/GoogleSignUp/completeRegistration")
                                .with(oauth2Login())
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.email").value("newuser@example.com"));
        }

    @Test
    @WithMockUser
    void testGetProfile_ReturnsValidProfile() throws Exception {
        ProfileDto mockProfile = ProfileDto.builder()
                .username("mina")
                .rank("DIAMOND")
                .currentRate(1140)
                .maxRate(1200)
                .friendCount(20)
                .avatarUrl("https://example.com/avatar.png")
                .stats(StatsDto.builder()
                        .solvedProblems(750)
                        .attemptedProblems(525)
                        .matchesPlayed(330)
                        .matchesWon(230)
                        .build())
                .categories(new CategoryDto[] {
                        CategoryDto.builder().name("DP").value(20).build(),
                        CategoryDto.builder().name("TWO_POINTERS").value(40).build()
                })
                .build();

        when(userService.getProfile(1L)).thenReturn(mockProfile);

        mockMvc.perform(get("/users/profile/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("mina"))
                .andExpect(jsonPath("$.rank").value("DIAMOND"))
                .andExpect(jsonPath("$.currentRate").value(1140))
                .andExpect(jsonPath("$.maxRate").value(1200))
                .andExpect(jsonPath("$.friendCount").value(20))
                .andExpect(jsonPath("$.avatarUrl").value("https://example.com/avatar.png"))
                .andExpect(jsonPath("$.stats.solvedProblems").value(750))
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories.length()").value(2));
    }

    @Test
    @WithMockUser
    void testGetProfile_UserNotFound() throws Exception {
        when(userService.getProfile(99L)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users/profile/99"))
                .andExpect(status().isNotFound());
    }
}
