package com.clashcode.backend.controller;

import com.clashcode.backend.dto.SubmissionDetailsDto;
import com.clashcode.backend.dto.SubmissionListDto;
import com.clashcode.backend.dto.SubmissionRequestDto;
import com.clashcode.backend.exception.GlobalExceptionHandler;
import com.clashcode.backend.model.Submission;
import com.clashcode.backend.model.User;
import com.clashcode.backend.service.SubmissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SubmissionControllerTest {

    @Mock
    private SubmissionService submissionService;

    @InjectMocks
    private SubmissionController submissionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(submissionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }

    // Helper method to set up authentication
    private void setupAuthentication(User user) {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // ----------------------------------------------------------------
    // 1) POST /submissions/submit - SUCCESS
    // ----------------------------------------------------------------
    @Test
    @WithMockUser(username = "kero", roles = {"USER"})
    void submitCode_ShouldCallService_AndReturn200() throws Exception {
        // Arrange
        SubmissionRequestDto dto = new SubmissionRequestDto();
        dto.setCode("print('hello')");
        dto.setCodeLanguage("PYTHON_3_8");
        dto.setProblemId(1L);

        // Mock user submission return
        Submission dummySubmission = new Submission(); // you can fill fields if needed
        when(submissionService.submitCode(any(SubmissionRequestDto.class), any(User.class)))
                .thenReturn(dummySubmission);

        // Act & Assert
        mockMvc.perform(post("/submissions/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // Verify service was called once
        verify(submissionService, times(1)).submitCode(any(SubmissionRequestDto.class), any(User.class));
    }

    // ----------------------------------------------------------------
    // 2) GET /submissions/status/{submissionId} - SUCCESS
    // ----------------------------------------------------------------
    @Test
    void getSubmissionStatusById_ShouldReturnDto() throws Exception {
        // Arrange
        SubmissionListDto dto = new SubmissionListDto();
        dto.setSubmissionId(99L);
        dto.setProblemId(5L);

        when(submissionService.getSubmissionStatusById(99L)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/submissions/status/99"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.submissionId").value(99));

        verify(submissionService, times(1)).getSubmissionStatusById(99L);
    }

    // ----------------------------------------------------------------
    // 3) GET /submissions/status/{submissionId} - DIFFERENT ID
    // ----------------------------------------------------------------
    @Test
    void getSubmissionStatusById_ShouldReturnCorrectDto_ForDifferentId() throws Exception {
        // Arrange
        SubmissionListDto dto = new SubmissionListDto();
        dto.setSubmissionId(123L);
        dto.setProblemId(10L);

        when(submissionService.getSubmissionStatusById(123L)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/submissions/status/123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.submissionId").value(123));

        verify(submissionService, times(1)).getSubmissionStatusById(123L);
    }
    // ----------------------------------------------------------------
    // 4) GET /submissions/details/{submissionId} - SUCCESS
    // ----------------------------------------------------------------
    @Test
    void getSubmissionDetailsById_ShouldReturnDetailsDto() throws Exception {
        // Arrange
        SubmissionDetailsDto dto = new SubmissionDetailsDto();
        dto.setSubmissionLang("CPP_GCC_9_2");
        dto.setSubmissionCode("#include <iostream>...");
        dto.setProblemTitle("Two Sum");
        dto.setUsername("kero");
        dto.setSubmissionStatus("ACCEPTED");

        when(submissionService.getSubmissionDetailsById(50L)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/submissions/details/50"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.problemTitle").value("Two Sum"))
                .andExpect(jsonPath("$.submissionLang").value("CPP_GCC_9_2"))
                .andExpect(jsonPath("$.username").value("kero"));

        verify(submissionService, times(1)).getSubmissionDetailsById(50L);
    }


    // ----------------------------------------------------------------
    // 5) GET /submissions/problem-title/{problemId} - SUCCESS
    // ----------------------------------------------------------------
    @Test
    void getProblemTitleById_ShouldReturnTitle() throws Exception {
        // Arrange
        when(submissionService.getProblemTitleById(7L)).thenReturn("Longest Increasing Subsequence");

        // Act & Assert
        mockMvc.perform(get("/submissions/problem-title/7"))
                .andExpect(status().isOk())
                .andExpect(content().string("Longest Increasing Subsequence"));

        verify(submissionService, times(1)).getProblemTitleById(7L);
    }


    // ----------------------------------------------------------------
    // 6) GET /submissions/problem-title/{problemId} - DIFFERENT ID
    // ----------------------------------------------------------------
    @Test
    void getProblemTitleById_ShouldReturnCorrectTitle_ForDifferentId() throws Exception {
        // Arrange
        when(submissionService.getProblemTitleById(42L)).thenReturn("Binary Search");

        // Act & Assert
        mockMvc.perform(get("/submissions/problem-title/42"))
                .andExpect(status().isOk())
                .andExpect(content().string("Binary Search"));

        verify(submissionService, times(1)).getProblemTitleById(42L);
    }

}