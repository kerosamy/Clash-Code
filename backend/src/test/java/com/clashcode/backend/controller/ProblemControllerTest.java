package com.clashcode.backend.controller;

import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.ProblemResponseDto;
import com.clashcode.backend.service.ProblemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProblemController.class)
class ProblemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProblemService problemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddProblem() throws Exception {
        ProblemRequestDto request = new ProblemRequestDto();
        request.setTitle("Add Two Integers");

        // Mock service to do nothing
        doNothing().when(problemService).addProblem(any());

        // Use the correct URL path that matches your controller mapping
        mockMvc.perform(post("/problem") // <-- match @PostMapping("/problem") in controller
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetProblem() throws Exception {
        ProblemResponseDto response = new ProblemResponseDto();
        response.setId(1L);
        response.setTitle("Add Two Integers");

        when(problemService.getProblemById(1L)).thenReturn(response);

        // Use correct URL and path variable if controller uses @GetMapping("/problem/{id}")
        mockMvc.perform(get("/problem/1")) // <-- match @GetMapping("/problem/{id}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Add Two Integers"));
    }
}
