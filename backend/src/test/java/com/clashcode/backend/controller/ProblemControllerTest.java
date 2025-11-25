package com.clashcode.backend.controller;

import com.clashcode.backend.dto.ProblemListDto;
import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.ProblemResponseDto;
import com.clashcode.backend.service.ProblemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

    @Test
    void testBrowseProblems() throws Exception {
        // Prepare mock data
        ProblemListDto problem1 = new ProblemListDto();
        problem1.setId(1L);
        problem1.setTitle("Add Two Integers");

        ProblemListDto problem2 = new ProblemListDto();
        problem2.setId(2L);
        problem2.setTitle("Subtract Two Integers");

        List<ProblemListDto> problemList = List.of(problem1, problem2);

        // Mock a paginated response
        Page<ProblemListDto> page = new PageImpl<>(problemList, PageRequest.of(0, 10), problemList.size());
        when(problemService.getAllProblems(anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/problem/browse")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Add Two Integers"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].title").value("Subtract Two Integers"));
    }
}
