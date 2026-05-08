package com.appointment;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentType;
import com.appointment.repo.AppointmentRepo;
import com.appointment.service.AppointmentRuleEngine;
import com.appointment.service.BookingService;
import com.appointment.strategy.TypeBasedRuleStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTest {

    @ParameterizedTest
    @CsvSource({
            "1, 2, 3, GROUP, true",
            "1, 5, 2, GROUP, false",
            "1, 2, 10, GROUP, false",
            "1, 2, 1, GROUP, false"
    })
    void bookingScenarios(
            int startAfterHours,
            int endAfterHours,
            int participants,
            AppointmentType type,
            boolean expectedResult
    ) {
        BookingService service = createBookingService();

        Appointment appointment = new Appointment(
                LocalDateTime.now().plusHours(startAfterHours),
                LocalDateTime.now().plusHours(endAfterHours),
                participants
        );

        appointment.setType(type);

        assertEquals(expectedResult, service.book(appointment));
    }

    @Test
    void bookingFailsWhenTypeIsNull() {
        BookingService service = createBookingService();

        Appointment appointment = new Appointment(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                1
        );

        assertFalse(service.book(appointment));
    }

    private BookingService createBookingService() {
        AppointmentRuleEngine engine = new AppointmentRuleEngine();
        engine.addStrategy(new TypeBasedRuleStrategy());

        return new BookingService(new AppointmentRepo(), engine);
    }
}