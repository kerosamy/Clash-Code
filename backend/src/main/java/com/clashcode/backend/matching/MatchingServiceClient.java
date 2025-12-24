package com.clashcode.backend.matching;

import com.clashcode.backend.matching.dto.MatchingRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MatchingServiceClient {
    private final WebClient webClient;
    public MatchingServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public void requestMatching(MatchingRequestDto dto) {
        webClient.post()
                .uri("/matching/request-matching")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
    public void deleteMatching(Long userId) {
        webClient.delete()
                .uri("/matching/delete-request/{userId}", userId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

}
