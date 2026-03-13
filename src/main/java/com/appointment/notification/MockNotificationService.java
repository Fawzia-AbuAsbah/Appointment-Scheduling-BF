package com.appointment.notification;

import java.util.ArrayList;
import java.util.List;

public class MockNotificationService implements NotificationService {

    private List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendNotification(String to, String message) {

        sentMessages.add(to + ":" + message);
    }

    public List<String> getSentMessages() {
        return sentMessages;
    }
}