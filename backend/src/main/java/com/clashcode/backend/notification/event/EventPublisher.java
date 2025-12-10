package com.clashcode.backend.notification.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {
    private final ApplicationEventPublisher publisher;

    public EventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishUserSignedUp(String username, String email) {
        publisher.publishEvent(new UserSignedUpEvent(this, username, email));
    }
}
