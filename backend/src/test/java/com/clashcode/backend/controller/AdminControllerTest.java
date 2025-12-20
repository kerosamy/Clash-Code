package com.clashcode.backend.controller;

import com.clashcode.backend.dto.FullProblemResponseDto;
import com.clashcode.backend.dto.ProblemListDto;
import com.clashcode.backend.dto.PracticeProblemResponseDto;
import com.clashcode.backend.service.JwtService;
import com.clashcode.backend.service.ProblemService;
import com.clashcode.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProblemService problemService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("GET /admin/problems/pending - success")
    void getPendingProblems_success() throws Exception {
        ProblemListDto dto = ProblemListDto.builder()
                .id(1L)
                .title("Sample Problem")
                .rate(800)
                .build();

        Page<ProblemListDto> page = new PageImpl<>(List.of(dto));

        when(problemService.getPendingProblems(0, 10)).thenReturn(page);

        mockMvc.perform(get("/admin/problems/pending"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Sample Problem"));

        verify(problemService, times(1)).getPendingProblems(0, 10);
    }

    @Test
    @DisplayName("GET /admin/problems/pending - custom pagination")
    void getPendingProblems_customPagination() throws Exception {
        Page<ProblemListDto> page = new PageImpl<>(List.of());

        when(problemService.getPendingProblems(2, 20)).thenReturn(page);

        mockMvc.perform(get("/admin/problems/pending")
                        .param("page", "2")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(problemService, times(1)).getPendingProblems(2, 20);
    }

    @Test
    @DisplayName("GET /admin/problems/{id} - success")
    void getProblemDetails_success() throws Exception {
        FullProblemResponseDto responseDto = FullProblemResponseDto.builder()
                .id(1L)
                .title("Problem 1")
                .rate(1200)
                .build();

        when(problemService.getProblemById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/admin/problems/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Problem 1"))
                .andExpect(jsonPath("$.rate").value(1200));

        verify(problemService, times(1)).getProblemById(1L);
    }

    @Test
    @DisplayName("POST /admin/problems/{id}/accept - success")
    void acceptProblem_success() throws Exception {
        doNothing().when(problemService).acceptProblem(1L);

        mockMvc.perform(post("/admin/problems/1/accept"))
                .andExpect(status().isOk());

        verify(problemService, times(1)).acceptProblem(1L);
    }


    @Test
    @DisplayName("GET /admin/problems/pending - empty result")
    void getPendingProblems_emptyResult() throws Exception {
        Page<ProblemListDto> emptyPage = new PageImpl<>(List.of());

        when(problemService.getPendingProblems(0, 10)).thenReturn(emptyPage);

        mockMvc.perform(get("/admin/problems/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
                }
                
	@Test
	@DisplayName("POST /admin/problems/{id}/reject - success")
	void rejectProblem_success() throws Exception {
	    Long problemId = 1L;
	    String rejectionNote = "The constraints are too vague.";

	    // No return value (void), so we use doNothing
	    doNothing().when(problemService).rejectProblem(eq(problemId), anyString());

	    mockMvc.perform(post("/admin/problems/1/reject")
		            .contentType(MediaType.TEXT_PLAIN) // Note: Controller expects String body
		            .content(rejectionNote))
		    .andExpect(status().isOk());

	    verify(problemService, times(1)).rejectProblem(eq(problemId), contains("too vague"));
	}

	@Test
	@DisplayName("POST /admin/problems/{id}/accept - problem not found")
	void acceptProblem_notFound() throws Exception {
	    doThrow(new RuntimeException("Problem not found"))
		    .when(problemService).acceptProblem(99L);

	    mockMvc.perform(post("/admin/problems/99/accept"))
		    .andExpect(status().isInternalServerError()); 
		    // Note: Depending on your GlobalExceptionHandler, this might be 404 or 500


		verify(problemService, times(1)).getPendingProblems(0, 10);
	    }
}
