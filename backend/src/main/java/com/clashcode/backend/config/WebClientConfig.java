package com.clashcode.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {
    @Bean
    public WebClient matchingWebClient(WebClient.Builder builder,
                                       @Value("${MATCHING_SERVICE_URL}") String url) {
        return builder
                .baseUrl(url)
                .build();
    }
}
