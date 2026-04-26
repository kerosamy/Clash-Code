package com.clashcode.backend.matcing_service;

import com.clashcode.backend.matching.MatchingServiceClient;
import com.clashcode.backend.matching.dto.MatchingRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingServiceClientTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private MatchingServiceClient matchingServiceClient;

    @BeforeEach
    void setup() {
        when(responseSpec.bodyToMono(Void.class))
                .thenReturn(Mono.empty());
    }

    @Test
    void test_requestMatching_success() {
        MatchingRequestDto dto = new MatchingRequestDto(1L, 1500);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/matching/request-matching"))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(dto))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);

        matchingServiceClient.requestMatching(dto);

        verify(webClient).post();
        verify(requestBodyUriSpec).uri("/matching/request-matching");
        verify(requestBodySpec).bodyValue(dto);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(Void.class);
    }

    @Test
    void test_deleteMatching_success() {
        Long userId = 10L;

        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec
                .uri("/matching/delete-request/{userId}", userId))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);

        matchingServiceClient.deleteMatching(userId);

        verify(webClient).delete();
        verify(requestHeadersUriSpec)
                .uri("/matching/delete-request/{userId}", userId);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(Void.class);
    }
}
