package com.clashcode.backend.controller;

import com.clashcode.backend.dto.SubmissionListDto;
import com.clashcode.backend.dto.SubmissionRequestDto;
import com.clashcode.backend.service.SubmissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubmissionController.class)
class SubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubmissionService submissionService;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------------- Submit Code Test ----------------
    @Test
    @WithMockUser
    void submitCode_ShouldReturnOk() throws Exception {
        SubmissionRequestDto requestDto = SubmissionRequestDto.builder()
                .userId(1L)
                .problemId(2L)
                .code("print('hello')")
                .codeLanguage("python")
                .build();

        doNothing().when(submissionService).submitCode(any());

        mockMvc.perform(post("/submissions/submit")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    // ---------------- Get Submissions by User Test ----------------
    @Test
    @WithMockUser
    void getSubmissionsByUser_ShouldReturnList() throws Exception {
        SubmissionListDto submission = SubmissionListDto.builder()
                .submissionStatus("ACCEPTED")
                .timeTaken(100)
                .memoryTaken(256)
                .submittedAt("2025-11-27")
                .build();

        when(submissionService.getSubmissionsByUser(1L)).thenReturn(List.of(submission));

        mockMvc.perform(get("/submissions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].submissionStatus").value("ACCEPTED"))
                .andExpect(jsonPath("$[0].timeTaken").value(100))
                .andExpect(jsonPath("$[0].memoryTaken").value(256))
                .andExpect(jsonPath("$[0].submittedAt").value("2025-11-27"));
    }

    // ---------------- Edge Case: No Submissions ----------------
    @Test
    @WithMockUser
    void getSubmissionsByUser_ShouldReturnEmptyList() throws Exception {
        when(submissionService.getSubmissionsByUser(1L)).thenReturn(List.of());

        mockMvc.perform(get("/submissions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
