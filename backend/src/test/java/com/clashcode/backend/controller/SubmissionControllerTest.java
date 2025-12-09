package com.clashcode.backend.controller;

import com.clashcode.backend.dto.SubmissionListDto;
import com.clashcode.backend.dto.SubmissionRequestDto;
import com.clashcode.backend.exception.GlobalExceptionHandler;
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
    void submitCode_ShouldCallService_AndReturn200() throws Exception {
        // Arrange
        SubmissionRequestDto dto = new SubmissionRequestDto();
        dto.setCode("print('hello')");
        dto.setCodeLanguage("PYTHON_3_8");
        dto.setProblemId(1L);

        User mockUser = new User();
        mockUser.setId(5L);
        mockUser.setUsername("kero");

        // Setup authentication
        setupAuthentication(mockUser);

        doNothing().when(submissionService).submitCode(any(SubmissionRequestDto.class), any(User.class));

        // Act & Assert
        mockMvc.perform(post("/submissions/submit")
                        .principal(SecurityContextHolder.getContext().getAuthentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

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
}