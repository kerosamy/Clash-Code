package com.clashcode.backend.notification.channel;

public interface NotificationChannel {
    void send(String recipient, String message);
}
