package com.clashcode.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    // Public endpoint — no token required
    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("Public endpoint is reachable!");
    }

    // Protected endpoint — requires valid JWT
    @GetMapping("/secure")
    public ResponseEntity<String> secureEndpoint() {
        return ResponseEntity.ok("Secure endpoint: You are authenticated!");
    }

    // Another protected example returning JSON
    @GetMapping("/secure-data")
    public ResponseEntity<TestResponse> secureData() {
        TestResponse response = new TestResponse(
                "Access granted",
                "This is protected data only visible with a valid token"
        );
        return ResponseEntity.ok(response);
    }

    // DTO for JSON response
    static class TestResponse {
        private String status;
        private String message;

        public TestResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() { return status; }
        public String getMessage() { return message; }
    }
}
