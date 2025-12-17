package com.clashcode.backend.dto;

import com.clashcode.backend.enums.NotificationMode;
import com.clashcode.backend.enums.NotificationType;

public interface NotificationPayload {
    NotificationType getNotificationType();
    String getTitle();
    String getMessage();
    NotificationMode getMode();
    String getDestination(String recipientUsername);
}
