package com.clashcode.matching_service.integration;

import com.clashcode.matching_service.dto.MatchingRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MatchingServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testFullMatchingFlow() throws Exception {
        // Test adding user to matching
        MatchingRequestDto requestDto = new MatchingRequestDto(1L, 1500);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/matching/request-matching")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        // Test deleting user from matching
        mockMvc.perform(delete("/matching/delete-request/1"))
                .andExpect(status().isOk());
    }
}
