package com.clashcode.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class FriendRequestExistsException extends RuntimeException {
    public FriendRequestExistsException(String message) {
        super(message);
    }
}
