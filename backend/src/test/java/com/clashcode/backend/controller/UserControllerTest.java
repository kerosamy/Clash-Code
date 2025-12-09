package com.clashcode.backend.controller;

import com.clashcode.backend.dto.CategoryDto;
import com.clashcode.backend.dto.ProfileDto;
import com.clashcode.backend.dto.StatsDto;
import com.clashcode.backend.dto.UserSearchResponseDto;
import com.clashcode.backend.exception.UserNotFoundException;
import com.clashcode.backend.model.User;
import com.clashcode.backend.service.JwtService;
import com.clashcode.backend.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @AfterEach
    void tearDown() {
        // Essential: Clear the context after each test to prevent pollution
        SecurityContextHolder.clearContext();
    }

    private void setupSecurityContext(Long userId, String username) {
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername(username);
        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("GET /users/profile - Success (authenticated user)")
    void getMyProfile_success() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("mina");
        setupSecurityContext(1L, "mina");

        StatsDto stats = new StatsDto(750, 525, 330, 230);
        CategoryDto category = new CategoryDto("DP", 20);

        ProfileDto mockProfile = ProfileDto.builder()
                .username("mina")
                .rank("DIAMOND")
                .currentRate(1200)
                .maxRate(1300)
                .friendCount(10)
                .avatarUrl("https://example.com/avatar.png")
                .stats(stats)
                .categories(new CategoryDto[]{category})
                .build();

        when(userService.getProfile(argThat(u ->
                u.getId().equals(mockUser.getId()) &&
                u.getUsername().equals(mockUser.getUsername())
        ))).thenReturn(mockProfile);

        mockMvc.perform(get("/users/profile").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("mina"))
                .andExpect(jsonPath("$.rank").value("DIAMOND"))
                .andExpect(jsonPath("$.stats.solvedProblems").value(750));
    }


    @Test
    @DisplayName("GET /users/profile/{username} - Success")
    void getUserProfile_success() throws Exception {
        StatsDto stats = new StatsDto(100, 200, 50, 25);
        ProfileDto mockProfile = ProfileDto.builder()
                .username("caro")
                .rank("MASTER")
                .currentRate(1400)
                .maxRate(1500)
                .friendCount(5)
                .avatarUrl("https://example.com/caro.png")
                .stats(stats)
                .categories(new CategoryDto[]{new CategoryDto("GRAPH", 30)})
                .build();

        when(userService.getUserProfile("caro")).thenReturn(mockProfile);

        mockMvc.perform(get("/users/profile/caro").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("caro"))
                .andExpect(jsonPath("$.rank").value("MASTER"))
                .andExpect(jsonPath("$.stats.attemptedProblems").value(200));
    }

    @Test
    @DisplayName("GET /profile/{username} - User Not Found")
    void getUserProfile_notFound() throws Exception {
        when(userService.getUserProfile("unknown"))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/profile/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /users/search - Found")
    void searchUsers_found() throws Exception {
        List<UserSearchResponseDto> mockResults = List.of(
                new UserSearchResponseDto("caro", "MASTER"),
                new UserSearchResponseDto("caroline", "CHAMPION")
        );

        when(userService.searchByUsername("car")).thenReturn(mockResults);

        mockMvc.perform(get("/users/search")
                        .param("username", "car")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("caro"))
                .andExpect(jsonPath("$[0].rank").value("MASTER"))
                .andExpect(jsonPath("$[1].username").value("caroline"))
                .andExpect(jsonPath("$[1].rank").value("CHAMPION"));
    }

    @Test
    @DisplayName("GET /users/search - No Results")
    void searchUsers_noResults() throws Exception {
        when(userService.searchByUsername("caro")).thenReturn(List.of());

        mockMvc.perform(get("/users/search")
                        .param("username", "caro")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}