package com.clashcode.matching_service.integration;

import com.clashcode.matching_service.dto.MatchingRequestDto;
import com.clashcode.matching_service.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MatchingServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedisService redisService;

    @Test
    void testFullMatchingFlow() throws Exception {
        // Mock Redis responses if needed
        doNothing().when(redisService).insertUser(anyLong(), anyInt());
        doNothing().when(redisService).removeUser(anyLong());

        // POST add user
        mockMvc.perform(post("/matching/request-matching")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"userRating\":1500}"))
                .andExpect(status().isOk());

        // DELETE remove user
        mockMvc.perform(delete("/matching/delete-request/1"))
                .andExpect(status().isOk());
    }
}
