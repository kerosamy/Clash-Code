package com.clashcode.backend.exception;

public class AIRequestFailedException extends RuntimeException {

    public AIRequestFailedException() {
        super("AI not available currently");
    }

    public AIRequestFailedException(String message) {
        super(message);
    }

    public AIRequestFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AIRequestFailedException(Throwable cause) {
        super("AI not available currently", cause);
    }
}