package com.clashcode.backend.notification;

import com.clashcode.backend.notification.channel.NotificationChannel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final List<NotificationChannel> channels;

    public NotificationService(List<NotificationChannel> channels) {
        this.channels = channels;
    }

    public void sendWelcomeNotification(String email) {
        String message = "Welcome to ClashCode!";
        channels.forEach(channel -> channel.send(email, message));
    }
}
