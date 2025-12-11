package com.clashcode.backend.controller;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import com.clashcode.backend.dto.CreateMatchRequestDto;
import com.clashcode.backend.dto.MatchResponseDto;
import com.clashcode.backend.dto.MatchSubmissionLogDto;
import com.clashcode.backend.dto.SubmissionRequestDto;
import com.clashcode.backend.exception.UnauthorizedException;
import com.clashcode.backend.model.User;
import com.clashcode.backend.service.JwtService;
import com.clashcode.backend.service.MatchService;
import com.clashcode.backend.service.SubmissionService;
import com.clashcode.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MatchController.class)
@AutoConfigureMockMvc(addFilters = false)
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean private MatchService matchService;
    @MockitoBean private SubmissionService submissionService;
    @MockitoBean private UserService userService;
    @MockitoBean private UserDetailsService userDetailsService;
    @MockitoBean private JwtService jwtService;

    @Test
    @DisplayName("POST /matches/{matchId}/submit - Unauthorized")
    void submitCode_unauthorized() throws Exception {
        SubmissionRequestDto submissionRequestDto = new SubmissionRequestDto();
        submissionRequestDto.setCode("print('Hello')");
        submissionRequestDto.setCodeLanguage("python");

        doThrow(new UnauthorizedException("User not authenticated"))
                .when(submissionService).submitCode(any(SubmissionRequestDto.class), any(User.class));

        mockMvc.perform(post("/matches/123/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submissionRequestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("User not authenticated"));
    }

    @Test
    @DisplayName("POST /matches/{matchId}/resign - Unauthorized")
    void resignMatch_unauthorized() throws Exception {
        doThrow(new UnauthorizedException("User not authenticated"))
                .when(matchService).resignMatch(any(Long.class), any(User.class));

        mockMvc.perform(post("/matches/123/resign"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("User not authenticated"));
    }
}
