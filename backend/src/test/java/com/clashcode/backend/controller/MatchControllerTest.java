package com.clashcode.backend.controller;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.MatchState;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
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
    void test_submitCode_unauthorized() throws Exception {
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
    void test_resignMatch_unauthorized() throws Exception {
        doThrow(new UnauthorizedException("User not authenticated"))
                .when(matchService).resignMatch(any(Long.class), any(User.class));

        mockMvc.perform(post("/matches/123/resign"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("User not authenticated"));
    }

    @Test
    @DisplayName("GET /matches/{matchId}/submission-log - Success")
    void test_getMatchSubmissionLog_success() throws Exception {
        MatchSubmissionLogDto logDto = MatchSubmissionLogDto.builder()
                .submissions(List.of()) // empty submissions for simplicity
                .build();

        when(matchService.getMatchSubmissionLog(123L)).thenReturn(List.of(logDto));

        mockMvc.perform(get("/matches/123/submission-log"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST /matches/invite/{recipientUsername} - Success")
    void test_invitePlayer_success() throws Exception {
        User sender = new User();
        sender.setId(1L);
        sender.setUsername("senderUser");

        // just do nothing when service is called

        mockMvc.perform(post("/matches/invite/recipientUser")
                        .principal(() -> "senderUser")) // AuthenticationPrincipal simulation
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /matches/{matchId}/problem - Success")
    void test_getMatchProblem_success() throws Exception {
        PartialProblemResponseDto problemDto = PartialProblemResponseDto.builder()
                .id(10L)
                .submissionsCount(0L)
                .title("Problem Title")
                .inputFormat("")
                .outputFormat("")
                .statement("")
                .notes("")
                .timeLimit(1)
                .memoryLimit(1)
                .rate(0)
                .author("author")
                .tags(new ArrayList<>())
                .visibleTestCases(new ArrayList<>())
                .build();
        when(matchService.getMatchProblem(10L)).thenReturn(problemDto);

        mockMvc.perform(get("/matches/10/problem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Problem Title"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags").isEmpty())
                .andExpect(jsonPath("$.visibleTestCases").isArray())
                .andExpect(jsonPath("$.visibleTestCases").isEmpty());
    }

    @Test
    @DisplayName("GET /matches/{matchId} - Success")
    void test_getMatchDetails_success() throws Exception {
        MatchResponseDto matchDto = MatchResponseDto.builder()
                .id(10L)
                .matchState(MatchState.ONGOING)
                .participants(List.of())
                .build();

        when(matchService.getMatchDetails(10L)).thenReturn(matchDto);

        mockMvc.perform(get("/matches/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @DisplayName("GET /matches/{matchId}/results - Success")
    void test_getMatchResults_success() throws Exception {
        Long matchId = 123L;
        MatchResultDto resultDto = MatchResultDto.builder()
                .isRated(true)
                .username("ClashMaster")
                .avatarUrl("https://example.com/avatar.png")
                .rank(1)
                .rateChange(25)
                .newRating(1525)
                .build();

        when(matchService.getMatchResults(eq(matchId), any())).thenReturn(resultDto);

        mockMvc.perform(get("/matches/{matchId}/results", matchId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("ClashMaster"))
                .andExpect(jsonPath("$.avatarUrl").value("https://example.com/avatar.png"))
                .andExpect(jsonPath("$.rank").value(1))
                .andExpect(jsonPath("$.rateChange").value(25))
                .andExpect(jsonPath("$.newRating").value(1525));
    }

    @Test
    @DisplayName("POST /matches/start-rated-match - Success")
    void test_startRatedMatch_success() throws Exception {
        MatchCreationDto dto = new MatchCreationDto();
        dto.setPlayerIdA(1L);
        dto.setPlayerIdB(2L);

        // do nothing when service is called
        mockMvc.perform(post("/matches/start-rated-match")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // optionally verify service was called
        verify(matchService).startRatedMatch(any(MatchCreationDto.class));
    }

    @Test
    @DisplayName("POST /matches/search-opponent - Success")
    void test_searchOpponent_success() throws Exception {
        User user = User.builder().id(1L).username("player1").build();

        // Mock SecurityContext to provide the authenticated user
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        mockMvc.perform(post("/matches/search-opponent"))
                .andExpect(status().isOk());

        verify(matchService).searchForOpponent(eq(user));
    }

    @Test
    @DisplayName("DELETE /matches/search-opponent/cancel - Success")
    void test_cancelSearchOpponent_success() throws Exception {
        User user = User.builder().id(1L).username("player1").build();

        // Mock SecurityContext to provide the authenticated user
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        mockMvc.perform(delete("/matches/search-opponent/cancel"))
                .andExpect(status().isOk());

        verify(matchService).cancelSearchForOpponent(eq(user));
    }
}
