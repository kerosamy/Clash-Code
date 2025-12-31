package com.clashcode.backend.ai;

import com.clashcode.backend.exception.AIRequestFailedException;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeminiInterface {
    private static final String MODEL = "gemini-2.5-flash";
    private final Client client;

    public GeminiInterface(@Value("${GEMINI_API_KEY:}") String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Gemini API key is missing");
        }

        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    public String getResponse(String prompt) {
        try {
            GenerateContentResponse response = client.models.generateContent(
                    MODEL,
                    prompt,
                    null
            );

            return response.text();
        } catch (Exception e) {
            throw new AIRequestFailedException();
        }
    }
}