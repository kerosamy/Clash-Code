package com.clashcode.matching_service.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class MatchingNotifier {
    private static final String GAME_SERVICE_URL = "http://localhost:8080/matches/start-rated-match";

    private final RestTemplate restTemplate;

    public MatchingNotifier(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public void notifyMatch(Long player1, Long player2) {
        try {
            Map<String, Long> payload = new HashMap<>();
            payload.put("playerIdA", player1);
            payload.put("playerIdB", player2);

            restTemplate.postForObject(GAME_SERVICE_URL, payload, Void.class);
            System.out.println("Game service notified for " + player1 + " and " + player2);
        } catch (Exception e) {
            System.err.println("Failed to notify game service: " + e.getMessage());
            // Optional: add retry logic or push to queue
        }
    }
}
