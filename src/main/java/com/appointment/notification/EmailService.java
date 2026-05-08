package com.appointment.notification;

import com.appointment.exception.EmailSendingException;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailService {

    private static final Logger LOGGER =
            Logger.getLogger(EmailService.class.getName());

    private final String username;
    private final String password;

    public EmailService(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void sendEmail(String to, String subject, String body) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );

            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            LOGGER.info("Email sent successfully to " + to);

        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send email", e);
            throw new EmailSendingException("Failed to send email", e);
        }
    }
}