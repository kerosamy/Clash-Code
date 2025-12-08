package com.clashcode.backend.controller;

import com.clashcode.backend.dto.SubmissionListDto;
import com.clashcode.backend.dto.SubmissionRequestDto;
import com.clashcode.backend.exception.UnauthorizedException;
import com.clashcode.backend.model.User;
import com.clashcode.backend.service.JwtService;
import com.clashcode.backend.service.SubmissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubmissionController.class)
@AutoConfigureMockMvc(addFilters = false)
class SubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubmissionService submissionService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setupSecurityContext(Long userId, String username) {
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername(username);
        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // ---------------- Submit Code Test ----------------
    @Test
    @DisplayName("POST /submissions/submit - Success")
    void submitCode_ShouldReturnOk() throws Exception {
        SubmissionRequestDto requestDto = SubmissionRequestDto.builder()
                .userId(1L)
                .problemId(2L)
                .code("print('hello')")
                .codeLanguage("python")
                .build();

        doNothing().when(submissionService).submitCode(any());

        mockMvc.perform(post("/submissions/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    // ---------------- Get My Submissions Test ----------------
    @Test
    @DisplayName("GET /submissions/me - Returns List")
    void getMySubmissions_ShouldReturnList() throws Exception {
        setupSecurityContext(1L, "mina");

        SubmissionListDto submission = SubmissionListDto.builder()
                .submissionStatus("ACCEPTED")
                .timeTaken(100)
                .memoryTaken(256)
                .submittedAt("2025-11-27")
                .build();

        when(submissionService.getSubmissionsByUser(eq(1L))).thenReturn(List.of(submission));

        mockMvc.perform(get("/submissions/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].submissionStatus").value("ACCEPTED"))
                .andExpect(jsonPath("$[0].timeTaken").value(100))
                .andExpect(jsonPath("$[0].memoryTaken").value(256))
                .andExpect(jsonPath("$[0].submittedAt").value("2025-11-27"));
    }

    // ---------------- Edge Case: No Submissions ----------------
    @Test
    @DisplayName("GET /submissions/me - Empty List")
    void getMySubmissions_ShouldReturnEmptyList() throws Exception {
        setupSecurityContext(1L, "mina");

        when(submissionService.getSubmissionsByUser(1L)).thenReturn(List.of());

        mockMvc.perform(get("/submissions/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ---------------- Edge Case: Unauthenticated ----------------
    @Test
    @DisplayName("GET /submissions/me - Unauthenticated throws Exception")
    void getMySubmissions_Unauthenticated() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(get("/submissions/me"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UnauthorizedException));
    }
}