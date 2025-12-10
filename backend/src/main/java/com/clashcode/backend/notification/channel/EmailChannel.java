package com.clashcode.backend.notification.channel;

import org.springframework.stereotype.Component;

@Component
public class EmailChannel implements NotificationChannel {
    @Override
    public void send(String recipient, String message) {
        // integrate with JavaMailSender or external email API
        System.out.println("Sending EMAIL to " + recipient + ": " + message);
    }
}