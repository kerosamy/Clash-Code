package com.clashcode.backend.ai;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class ProblemAIReviewPrompt {

    private final String template;

    public ProblemAIReviewPrompt(ResourceLoader resourceLoader) throws IOException {
        Resource resource =
                resourceLoader.getResource("classpath:prompts/problem-ai-review-v1.txt");
        this.template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    public String build(Map<String, String> values) {
        String result = template;
        for (var entry : values.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
}

