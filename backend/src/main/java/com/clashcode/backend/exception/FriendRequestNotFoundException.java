package com.clashcode.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FriendRequestNotFoundException extends RuntimeException {
    public FriendRequestNotFoundException() {
        super("Friend Request Not Found");
    }
}
