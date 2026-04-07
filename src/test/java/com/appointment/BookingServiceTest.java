package com.appointment;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentType;
import com.appointment.repo.AppointmentRepo;
import com.appointment.service.AppointmentRuleEngine;
import com.appointment.service.BookingService;
import com.appointment.strategy.TypeBasedRuleStrategy;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTest {

    @Test
    void bookingSuccess() {

        AppointmentRuleEngine engine = new AppointmentRuleEngine();
        engine.addStrategy(new TypeBasedRuleStrategy());

        BookingService service =
                new BookingService(new AppointmentRepo(), engine);

        Appointment appointment =
                new Appointment(
                        LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().plusHours(2),
                        3
                );

        appointment.setType(AppointmentType.GROUP);

        assertTrue(service.book(appointment));
    }

    @Test
    void durationTooLong() {

        AppointmentRuleEngine engine = new AppointmentRuleEngine();
        engine.addStrategy(new TypeBasedRuleStrategy());

        BookingService service =
                new BookingService(new AppointmentRepo(), engine);

        Appointment appointment =
                new Appointment(
                        LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().plusHours(5),
                        2
                );

        appointment.setType(AppointmentType.GROUP);

        assertFalse(service.book(appointment));
    }

    @Test
    void tooManyParticipants() {

        AppointmentRuleEngine engine = new AppointmentRuleEngine();
        engine.addStrategy(new TypeBasedRuleStrategy());

        BookingService service =
                new BookingService(new AppointmentRepo(), engine);

        Appointment appointment =
                new Appointment(
                        LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().plusHours(2),
                        10
                );

        appointment.setType(AppointmentType.GROUP);

        assertFalse(service.book(appointment));
    }

    @Test
    void bookingFailsWhenTypeRuleFails() {

        AppointmentRuleEngine engine = new AppointmentRuleEngine();
        engine.addStrategy(new TypeBasedRuleStrategy());

        BookingService service =
                new BookingService(new AppointmentRepo(), engine);

        Appointment appointment =
                new Appointment(
                        LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().plusHours(2),
                        1
                );

        appointment.setType(AppointmentType.GROUP);

        assertFalse(service.book(appointment));
    }

    @Test
    void bookingFailsWhenTypeIsNull() {

        AppointmentRuleEngine engine = new AppointmentRuleEngine();
        engine.addStrategy(new TypeBasedRuleStrategy());

        BookingService service =
                new BookingService(new AppointmentRepo(), engine);

        Appointment appointment =
                new Appointment(
                        LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().plusHours(2),
                        1
                );

        assertFalse(service.book(appointment));
    }
}