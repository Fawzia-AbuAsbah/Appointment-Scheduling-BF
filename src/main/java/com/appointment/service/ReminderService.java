package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.notification.NotificationService;
/**
 * Service responsible for sending appointment reminders.
 * 
 * <p>This service uses a {@code NotificationService} abstraction to send
 * reminder messages to users without depending on a specific notification
 * implementation.</p>
 * 
 * <p>This design improves flexibility and testability, since real or mock
 * notification services can be injected as needed.</p>
 * 
 * @author Team
 * @version 1.0
 */
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