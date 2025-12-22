package com.clashcode.matching_service.integration;

import com.clashcode.matching_service.main_backend.MainBackendClient;
import com.clashcode.matching_service.main_backend.dto.MatchCreationDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MainBackendClientTest {

    private MockWebServer mockWebServer;
    private MainBackendClient mainBackendClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();

        mainBackendClient = new MainBackendClient(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testMatchingTwoPlayers_Success() throws InterruptedException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(""));

        MatchCreationDto dto = new MatchCreationDto(1L, 2L);

        // Act
        assertDoesNotThrow(() -> mainBackendClient.MatchingTwoPlayers(dto));

        // Assert
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/matches/start-rated-match", request.getPath());
        assertNotNull(request.getHeader("Content-Type"));
    }

    @Test
    void testMatchingTwoPlayers_WithError() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error"));

        MatchCreationDto dto = new MatchCreationDto(1L, 2L);

        // Act & Assert
        assertThrows(WebClientResponseException.class,
                () -> mainBackendClient.MatchingTwoPlayers(dto));
    }

    @Test
    void testConstructor() {
        WebClient testClient = WebClient.builder().build();
        MainBackendClient client = new MainBackendClient(testClient);
        assertNotNull(client);
    }
}
