package com.clashcode.backend.controller;

import com.clashcode.backend.model.User; // Import your User model
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@PreAuthorize("hasRole('USER')")
public class TestController {

    // 1. Public endpoint: Handles both Guests (no token) and Logged-in Users (with token)
    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint(@AuthenticationPrincipal User user) {
        if (user == null) {
            System.out.println("Public Endpoint: User is Guest (No Token)");
            return ResponseEntity.ok("Public endpoint: You are a Guest.");
        } else {
            System.out.println("Public Endpoint: User is " + user.getEmail());
            return ResponseEntity.ok("Public endpoint: You are logged in as " + user.getUsername());
        }
    }

    // 2. Protected endpoint: Prints ID and Email
    @GetMapping("/secure")
    public ResponseEntity<String> secureEndpoint(@AuthenticationPrincipal User user) {
        // Safety check (in case you used permitAll)
        if (user == null) {
            return ResponseEntity.ok("No User found (Did you send the token?)");
        }

        System.out.println("Secure Endpoint Access by: " + user.getEmail());

        return ResponseEntity.ok("Secure endpoint. ID: " + user.getId() + ", Email: " + user.getEmail());
    }

    // 3. JSON endpoint: Returns user details in JSON format
    @GetMapping("/secure-data")
    public ResponseEntity<TestResponse> secureData(@AuthenticationPrincipal User user) {
        String message;
        if (user != null) {
            message = "Authenticated as: " + user.getUsername();
        } else {
            message = "Authenticated as: Guest";
        }

        TestResponse response = new TestResponse("Success", message);
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

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}