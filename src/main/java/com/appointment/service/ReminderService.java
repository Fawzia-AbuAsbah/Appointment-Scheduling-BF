package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.notification.NotificationService;

public class ReminderService {

    private NotificationService notificationService;

    public ReminderService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void sendReminder(String email, Appointment appointment) {

        String message =
                "Reminder: You have an appointment at "
                        + appointment.getStart();

        notificationService.sendNotification(email, message);
    }
}