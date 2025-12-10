package com.clashcode.backend.notification.event;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class UserSignedUpEvent extends ApplicationEvent {
    private final String username;

    public UserSignedUpEvent(Object source, String username, String email) {
        super(source);
        this.username = username;
    }
}
