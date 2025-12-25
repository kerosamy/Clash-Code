package com.clashcode.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FAILED_DEPENDENCY)
public class AIRequestFailedException extends RuntimeException {
    public AIRequestFailedException() {
        super("AI not available currently");
    }
}
