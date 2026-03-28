package com.appointment;

import com.appointment.domain.Appointment;
import com.appointment.notification.MockNotificationService;
import com.appointment.service.ReminderService;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ReminderServiceTest {

    @Test
    void reminderSent() {

        MockNotificationService mock = new MockNotificationService();

        ReminderService service = new ReminderService(mock);

        Appointment appointment = new Appointment(
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                2
        );

        service.sendReminder("test@gmail.com", appointment);

        
        assertEquals(1, mock.getSentMessages().size());

        
        assertTrue(mock.getSentMessages().get(0).contains("Reminder"));
    }
}