package com.clashcode.backend.Exception;

import com.clashcode.backend.exception.GlobalExceptionHandler;
import com.clashcode.backend.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleUnauthorized_Returns401AndMessage() {
        UnauthorizedException ex = new UnauthorizedException("Not authorized");

        ResponseEntity<Map<String, Object>> response = handler.handleUnauthorized(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).containsKey("timestamp"));
        assertEquals("Not authorized", response.getBody().get("error"));
    }

    @Test
    void handleIllegalArgs_Returns400AndMessage() {
        IllegalArgumentException ex = new IllegalArgumentException("Bad input");

        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgs(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).containsKey("timestamp"));
        assertEquals("Bad input", response.getBody().get("error"));
    }
}
