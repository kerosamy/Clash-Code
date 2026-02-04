package com.clashcode.backend.controller;

import com.clashcode.backend.dto.SubmissionDetailsDto;
import com.clashcode.backend.dto.SubmissionListDto;
import com.clashcode.backend.dto.SubmissionRequestDto;
import com.clashcode.backend.model.Submission;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubmissionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SubmissionService submissionService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setupAuthentication() {
        User user = new User();
        user.setId(1L);
        user.setUsername("kero");
        user.setEmail("kero@example.com");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList())
        );
    }

    @Test
    @DisplayName("POST /submissions/submit - SUCCESS")
    void submitCode_ShouldCallService_AndReturn200() throws Exception {
        setupAuthentication();

        SubmissionRequestDto dto = new SubmissionRequestDto();
        dto.setCode("print('hello')");
        dto.setCodeLanguage("PYTHON_3_8");
        dto.setProblemId(1L);

        Submission dummySubmission = new Submission();
        when(submissionService.submitCode(any(SubmissionRequestDto.class), any(User.class)))
                .thenReturn(dummySubmission);

        mockMvc.perform(post("/submissions/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(submissionService, times(1)).submitCode(any(SubmissionRequestDto.class), any(User.class));
    }

    @Test
    @DisplayName("POST /submissions/submit - Unauthorized")
    void submitCode_unauthorized() throws Exception {
        SubmissionRequestDto dto = new SubmissionRequestDto();
        dto.setCode("print('hello')");
        dto.setCodeLanguage("PYTHON_3_8");
        dto.setProblemId(1L);

        mockMvc.perform(post("/submissions/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /submissions/my-submissions - SUCCESS")
    void getMySubmissions_success() throws Exception {
        setupAuthentication();

        List<SubmissionListDto> submissions = new ArrayList<>();
        SubmissionListDto dto = new SubmissionListDto();
        dto.setSubmissionId(1L);
        dto.setProblemId(5L);
        submissions.add(dto);

        when(submissionService.getSubmissionsByUser(1L)).thenReturn(submissions);

        mockMvc.perform(get("/submissions/my-submissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].submissionId").value(1))
                .andExpect(jsonPath("$[0].problemId").value(5));

        verify(submissionService, times(1)).getSubmissionsByUser(1L);
    }

    @Test
    @DisplayName("GET /submissions/my-submissions - Unauthorized")
    void getMySubmissions_unauthorized() throws Exception {
        mockMvc.perform(get("/submissions/my-submissions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /submissions/my-submissions - Empty list")
    void getMySubmissions_emptyList() throws Exception {
        setupAuthentication();

        when(submissionService.getSubmissionsByUser(1L)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/submissions/my-submissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /submissions/status/{submissionId} - SUCCESS")
    void getSubmissionStatusById_ShouldReturnDto() throws Exception {
        SubmissionListDto dto = new SubmissionListDto();
        dto.setSubmissionId(99L);
        dto.setProblemId(5L);

        when(submissionService.getSubmissionStatusById(99L)).thenReturn(dto);

        mockMvc.perform(get("/submissions/status/99"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.submissionId").value(99))
                .andExpect(jsonPath("$.problemId").value(5));

        verify(submissionService, times(1)).getSubmissionStatusById(99L);
    }

    @Test
    @DisplayName("GET /submissions/status/{submissionId} - DIFFERENT ID")
    void getSubmissionStatusById_ShouldReturnCorrectDto_ForDifferentId() throws Exception {
        SubmissionListDto dto = new SubmissionListDto();
        dto.setSubmissionId(123L);
        dto.setProblemId(10L);

        when(submissionService.getSubmissionStatusById(123L)).thenReturn(dto);

        mockMvc.perform(get("/submissions/status/123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.submissionId").value(123))
                .andExpect(jsonPath("$.problemId").value(10));

        verify(submissionService, times(1)).getSubmissionStatusById(123L);
    }

    @Test
    @DisplayName("GET /submissions/details/{submissionId} - SUCCESS")
    void getSubmissionDetailsById_ShouldReturnDetailsDto() throws Exception {
        SubmissionDetailsDto dto = new SubmissionDetailsDto();
        dto.setSubmissionLang("CPP_GCC_9_2");
        dto.setSubmissionCode("#include <iostream>...");
        dto.setProblemTitle("Two Sum");
        dto.setUsername("kero");
        dto.setSubmissionStatus("ACCEPTED");

        when(submissionService.getSubmissionDetailsById(50L)).thenReturn(dto);

        mockMvc.perform(get("/submissions/details/50"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.problemTitle").value("Two Sum"))
                .andExpect(jsonPath("$.submissionLang").value("CPP_GCC_9_2"))
                .andExpect(jsonPath("$.username").value("kero"))
                .andExpect(jsonPath("$.submissionStatus").value("ACCEPTED"));

        verify(submissionService, times(1)).getSubmissionDetailsById(50L);
    }

    @Test
    @DisplayName("GET /submissions/details/{submissionId} - Different submission")
    void getSubmissionDetailsById_differentSubmission() throws Exception {
        SubmissionDetailsDto dto = new SubmissionDetailsDto();
        dto.setSubmissionLang("JAVA_8");
        dto.setSubmissionCode("public class Main {...}");
        dto.setProblemTitle("Binary Search");
        dto.setUsername("alice");
        dto.setSubmissionStatus("WRONG_ANSWER");

        when(submissionService.getSubmissionDetailsById(75L)).thenReturn(dto);

        mockMvc.perform(get("/submissions/details/75"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.problemTitle").value("Binary Search"))
                .andExpect(jsonPath("$.submissionLang").value("JAVA_8"))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.submissionStatus").value("WRONG_ANSWER"));
    }

    @Test
    @DisplayName("GET /submissions/problem-title/{problemId} - SUCCESS")
    void getProblemTitleById_ShouldReturnTitle() throws Exception {
        when(submissionService.getProblemTitleById(7L)).thenReturn("Longest Increasing Subsequence");

        mockMvc.perform(get("/submissions/problem-title/7"))
                .andExpect(status().isOk())
                .andExpect(content().string("Longest Increasing Subsequence"));

        verify(submissionService, times(1)).getProblemTitleById(7L);
    }

    @Test
    @DisplayName("GET /submissions/problem-title/{problemId} - DIFFERENT ID")
    void getProblemTitleById_ShouldReturnCorrectTitle_ForDifferentId() throws Exception {
        when(submissionService.getProblemTitleById(42L)).thenReturn("Binary Search");

        mockMvc.perform(get("/submissions/problem-title/42"))
                .andExpect(status().isOk())
                .andExpect(content().string("Binary Search"));

        verify(submissionService, times(1)).getProblemTitleById(42L);
    }

    @Test
    @DisplayName("GET /submissions/problem-title/{problemId} - Another problem")
    void getProblemTitleById_anotherProblem() throws Exception {
        when(submissionService.getProblemTitleById(100L)).thenReturn("Maximum Subarray Sum");

        mockMvc.perform(get("/submissions/problem-title/100"))
                .andExpect(status().isOk())
                .andExpect(content().string("Maximum Subarray Sum"));

        verify(submissionService, times(1)).getProblemTitleById(100L);
    }

    @Test
    @DisplayName("POST /submissions/submit - With match ID")
    void submitCode_withMatchId() throws Exception {
        setupAuthentication();

        SubmissionRequestDto dto = new SubmissionRequestDto();
        dto.setCode("def solve(): pass");
        dto.setCodeLanguage("PYTHON_3_8");
        dto.setProblemId(1L);
        dto.setMatchId(50L);

        Submission dummySubmission = new Submission();
        when(submissionService.submitCode(any(SubmissionRequestDto.class), any(User.class)))
                .thenReturn(dummySubmission);

        mockMvc.perform(post("/submissions/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(submissionService, times(1)).submitCode(any(SubmissionRequestDto.class), any(User.class));
    }

    @Test
    @DisplayName("GET /submissions/my-submissions - Multiple submissions")
    void getMySubmissions_multipleSubmissions() throws Exception {
        setupAuthentication();

        List<SubmissionListDto> submissions = new ArrayList<>();

        SubmissionListDto dto1 = new SubmissionListDto();
        dto1.setSubmissionId(1L);
        dto1.setProblemId(5L);
        submissions.add(dto1);

        SubmissionListDto dto2 = new SubmissionListDto();
        dto2.setSubmissionId(2L);
        dto2.setProblemId(6L);
        submissions.add(dto2);

        SubmissionListDto dto3 = new SubmissionListDto();
        dto3.setSubmissionId(3L);
        dto3.setProblemId(7L);
        submissions.add(dto3);

        when(submissionService.getSubmissionsByUser(1L)).thenReturn(submissions);

        mockMvc.perform(get("/submissions/my-submissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].submissionId").value(1))
                .andExpect(jsonPath("$[1].submissionId").value(2))
                .andExpect(jsonPath("$[2].submissionId").value(3));
    }
}