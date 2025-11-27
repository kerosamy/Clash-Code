package com.clashcode.backend.controller;

import com.clashcode.backend.dto.ProblemFilterDto;
import com.clashcode.backend.dto.ProblemListDto;
import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.ProblemResponseDto;
import com.clashcode.backend.service.ProblemService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ProblemController.class)
class ProblemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProblemService problemService;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------------- Add Problem Test ----------------
    @Test
    @WithMockUser
    void testAddProblem() throws Exception {
        ProblemRequestDto request = new ProblemRequestDto();
        request.setTitle("Add Two Integers");

        doNothing().when(problemService).addProblem(any(), any());

        MockMultipartFile problemPart = new MockMultipartFile(
                "problem",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile testcasesPart = new MockMultipartFile(
                "testcases",
                "testcase.txt",
                "text/plain",
                "dummy content".getBytes()
        );

        mockMvc.perform(multipart("/problem")
                        .file(problemPart)
                        .file(testcasesPart)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    // ---------------- Get Problem Test ----------------
    @Test
    @WithMockUser
    void testGetProblem() throws Exception {
        ProblemResponseDto response = new ProblemResponseDto();
        response.setId(1L);
        response.setTitle("Add Two Integers");

        when(problemService.getProblemById(1L)).thenReturn(response);

        mockMvc.perform(get("/problem/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Add Two Integers"));
    }

    // ---------------- Browse Problems Test ----------------
    @Test
    @WithMockUser
    void testBrowseProblems() throws Exception {
        ProblemListDto problem1 = new ProblemListDto();
        problem1.setId(1L);
        problem1.setTitle("Add Two Integers");

        ProblemListDto problem2 = new ProblemListDto();
        problem2.setId(2L);
        problem2.setTitle("Subtract Two Integers");

        List<ProblemListDto> problemList = List.of(problem1, problem2);

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

    @Test
    void testBrowseFiltered() throws Exception {
        ProblemListDto problem1 = new ProblemListDto();
        problem1.setId(1L);
        problem1.setTitle("Multiply Two Integers");

        List<ProblemListDto> problemList = List.of(problem1);
        Page<ProblemListDto> page = new PageImpl<>(problemList, PageRequest.of(0, 10), problemList.size());


        when(problemService.getFilteredProblems(
                anyList(),
                any(Integer.class),
                any(Integer.class),
                anyInt(),
                anyInt()
        )).thenReturn(page);


        ProblemFilterDto filterDto = new ProblemFilterDto();
        filterDto.setTags(List.of());
        filterDto.setMinRate(100);
        filterDto.setMaxRate(200);

        mockMvc.perform(post("/problem/browse/filter")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Multiply Two Integers"));
    }

    @Test
    void testSearchByName() throws Exception {
        ProblemListDto problem1 = new ProblemListDto();
        problem1.setId(1L);
        problem1.setTitle("Multiply Two Integers");

        List<ProblemListDto> problemList = List.of(problem1);
        Page<ProblemListDto> page = new PageImpl<>(problemList, PageRequest.of(0, 10), problemList.size());

        when(problemService.searchProblemsByName("Multiply", 0, 10)).thenReturn(page);

        mockMvc.perform(get("/problem/search")
                        .param("keyword", "Multiply")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Multiply Two Integers"));
    }
}