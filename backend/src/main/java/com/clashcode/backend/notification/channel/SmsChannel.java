package com.clashcode.backend.notification.channel;

import org.springframework.stereotype.Component;

@Component
public class SmsChannel implements NotificationChannel {
    @Override
    public void send(String recipient, String message) {
        // integrate with Twilio or other SMS provider
        System.out.println("Sending SMS to " + recipient + ": " + message);
    }
}
