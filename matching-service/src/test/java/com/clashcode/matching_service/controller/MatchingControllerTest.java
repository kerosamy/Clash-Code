package com.clashcode.matching_service.controller;

import com.clashcode.matching_service.dto.MatchingRequestDto;
import com.clashcode.matching_service.service.MatchingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MatchingController.class)
class MatchingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MatchingService matchingService;

    @Test
    void testAddUserToMatching_WithValidRequest_ShouldReturnOk() throws Exception {
        // Arrange
        MatchingRequestDto requestDto = new MatchingRequestDto(1L, 1500);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // Act & Assert
        mockMvc.perform(post("/matching/request-matching")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(matchingService, times(1)).addUserToMatchingService(any(MatchingRequestDto.class));
    }

    @Test
    void testAddUserToMatching_ShouldCallServiceWithCorrectData() throws Exception {
        // Arrange
        MatchingRequestDto requestDto = new MatchingRequestDto(123L, 2000);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // Act
        mockMvc.perform(post("/matching/request-matching")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        // Assert
        verify(matchingService).addUserToMatchingService(argThat(dto ->
                dto.getUserId() == 123L && dto.getUserRating() == 2000
        ));
    }

    @Test
    void testAddUserToMatching_WithDifferentRatings_ShouldWork() throws Exception {
        // Arrange
        MatchingRequestDto requestDto = new MatchingRequestDto(5L, 500);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // Act & Assert
        mockMvc.perform(post("/matching/request-matching")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(matchingService).addUserToMatchingService(any(MatchingRequestDto.class));
    }

    @Test
    void testAddUserToMatching_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String invalidJson = "{invalid json}";

        // Act & Assert
        mockMvc.perform(post("/matching/request-matching")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(matchingService, never()).addUserToMatchingService(any());
    }

    @Test
    void testDeleteUserFromMatching_WithValidUserId_ShouldReturnOk() throws Exception {
        // Arrange
        Long userId = 1L;

        // Act & Assert
        mockMvc.perform(delete("/matching/delete-request/{userId}", userId))
                .andExpect(status().isOk());

        verify(matchingService, times(1)).removeUserFromMatchingService(userId);
    }

    @Test
    void testDeleteUserFromMatching_WithDifferentUserId_ShouldCallService() throws Exception {
        // Arrange
        Long userId = 999L;

        // Act & Assert
        mockMvc.perform(delete("/matching/delete-request/{userId}", userId))
                .andExpect(status().isOk());

        verify(matchingService).removeUserFromMatchingService(999L);
    }

    @Test
    void testDeleteUserFromMatching_ShouldAcceptLargeUserIds() throws Exception {
        // Arrange
        Long userId = 999999999L;

        // Act & Assert
        mockMvc.perform(delete("/matching/delete-request/{userId}", userId))
                .andExpect(status().isOk());

        verify(matchingService).removeUserFromMatchingService(999999999L);
    }

    @Test
    void testAddUserToMatching_WithMissingContentType_ShouldReturnUnsupportedMediaType() throws Exception {
        // Arrange
        MatchingRequestDto requestDto = new MatchingRequestDto(1L, 1500);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // Act & Assert
        mockMvc.perform(post("/matching/request-matching")
                        .content(requestBody))
                .andExpect(status().isUnsupportedMediaType());

        verify(matchingService, never()).addUserToMatchingService(any());
    }

    @Test
    void testAddUserToMatching_WithEmptyBody_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/matching/request-matching")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());

        verify(matchingService, never()).addUserToMatchingService(any());
    }

    @Test
    void testDeleteUserFromMatching_WithInvalidPathVariable_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/matching/delete-request/invalid"))
                .andExpect(status().isBadRequest());

        verify(matchingService, never()).removeUserFromMatchingService(anyLong());
    }

    @Test
    void testRequestMatchingEndpoint_ShouldAcceptPostOnly() throws Exception {
        // Arrange
        MatchingRequestDto requestDto = new MatchingRequestDto(1L, 1500);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // Act & Assert - GET should fail
        mockMvc.perform(get("/matching/request-matching")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isMethodNotAllowed());

        verify(matchingService, never()).addUserToMatchingService(any());
    }

    @Test
    void testDeleteRequestEndpoint_ShouldAcceptDeleteOnly() throws Exception {
        // Act & Assert - POST should fail
        mockMvc.perform(post("/matching/delete-request/1"))
                .andExpect(status().isMethodNotAllowed());

        verify(matchingService, never()).removeUserFromMatchingService(anyLong());
    }

    @Test
    void testAddUserToMatching_MultipleRequests_ShouldProcessEachIndependently() throws Exception {
        // Arrange
        MatchingRequestDto request1 = new MatchingRequestDto(1L, 1500);
        MatchingRequestDto request2 = new MatchingRequestDto(2L, 1600);

        // Act & Assert - First request
        mockMvc.perform(post("/matching/request-matching")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        // Act & Assert - Second request
        mockMvc.perform(post("/matching/request-matching")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk());

        verify(matchingService, times(2)).addUserToMatchingService(any(MatchingRequestDto.class));
    }
}