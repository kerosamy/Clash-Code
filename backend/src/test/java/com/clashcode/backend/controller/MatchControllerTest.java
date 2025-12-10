package com.clashcode.backend.controller;

import com.clashcode.backend.dto.CreateMatchRequestDto;
import com.clashcode.backend.dto.MatchResponseDto;
import com.clashcode.backend.service.JwtService;
import com.clashcode.backend.service.MatchService;
import com.clashcode.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MatchController.class)
@AutoConfigureMockMvc(addFilters = false)
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MatchService matchService;

    @MockitoBean private UserService userService;
    @MockitoBean private UserDetailsService userDetailsService;
    @MockitoBean private JwtService jwtService;

    @Test
    @DisplayName("POST /matches/create - Success")
    void createMatch_success() throws Exception {
        // Arrange
        CreateMatchRequestDto requestDto = new CreateMatchRequestDto();
        requestDto.setPlayer1Id(1L);
        requestDto.setPlayer2Id(2L);
        requestDto.setProblemId(10L);

        MatchResponseDto responseDto = MatchResponseDto.builder()
                .id(100L)
                .problemId(10L)
                .build();

        when(matchService.createMatch(any(CreateMatchRequestDto.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/matches/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.problemId").value(10));
    }

    @Test
    @DisplayName("POST /matches/create - Player Not Found")
    void createMatch_PlayerNotExist() throws Exception {
        // Arrange
        CreateMatchRequestDto requestDto = new CreateMatchRequestDto();
        requestDto.setPlayer1Id(99L);

        when(matchService.createMatch(any(CreateMatchRequestDto.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Player 1 does not exist"));

        // Act & Assert
        mockMvc.perform(post("/matches/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Player 1 does not exist"));
    }
}