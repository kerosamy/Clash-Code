package com.clashcode.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProblemNotFoundException extends RuntimeException {
    public ProblemNotFoundException() {
        super("Problem Not Found");
    }
}
