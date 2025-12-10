package com.clashcode.backend.notification.channel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketChannel implements NotificationChannel {
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketChannel(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void send(String recipient, String message) {
        messagingTemplate.convertAndSendToUser(recipient, "/queue/notifications", message);
    }
}