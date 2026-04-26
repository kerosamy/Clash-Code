package com.clashcode.matching_service.main_backend;

import com.clashcode.matching_service.main_backend.dto.MatchCreationDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MainBackendClient {
    private final WebClient webClient;
    public MainBackendClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public void MatchingTwoPlayers(MatchCreationDto dto) {
        webClient.post()
                .uri("/matches/start-rated-match")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

}
