package com.appointment;

import com.appointment.domain.Appointment;
import com.appointment.repo.AppointmentRepo;
import com.appointment.service.BookingService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTest {

    @Test
    void bookingSuccess() {

        BookingService service =
                new BookingService(new AppointmentRepo());

        Appointment appointment =
                new Appointment(
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(2),
                        3
                );

        assertTrue(service.book(appointment));
    }

    @Test
    void durationTooLong() {

        BookingService service =
                new BookingService(new AppointmentRepo());

        Appointment appointment =
                new Appointment(
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(5),
                        2
                );

        assertFalse(service.book(appointment));
    }

    @Test
    void tooManyParticipants() {

        BookingService service =
                new BookingService(new AppointmentRepo());

        Appointment appointment =
                new Appointment(
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(1),
                        10
                );

        assertFalse(service.book(appointment));
    }
}