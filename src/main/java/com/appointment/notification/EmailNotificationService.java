package com.appointment.notification;

public class EmailNotificationService implements NotificationService {

    private EmailService emailService;

    public EmailNotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendNotification(String to, String message) {

        String subject = "Appointment Reminder";

        emailService.sendEmail(to, subject, message);
    }
}