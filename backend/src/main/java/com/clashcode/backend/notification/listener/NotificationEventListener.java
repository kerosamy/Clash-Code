package com.clashcode.backend.notification.listener;

import com.clashcode.backend.notification.event.UserSignedUpEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleUserSignedUp(UserSignedUpEvent event) {
        String message = "🎉 Welcome " + event.getUsername() + "!";
        messagingTemplate.convertAndSend("/topic/signup", message);
    }
}
