package com.appointment;

import com.appointment.domain.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentTest {

    @Test
    void validateDurationSuccess() {
        Appointment app = new Appointment(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                1
        );

        assertDoesNotThrow(app::validateDuration);
    }

    @Test
    void validateDurationFail() {
        Appointment app = new Appointment(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(5),
                1
        );

        assertThrows(IllegalArgumentException.class, app::validateDuration);
    }

    @Test
    void validateParticipantsSuccess() {
        Appointment app = new Appointment(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                3
        );

        assertDoesNotThrow(app::validateParticipants);
    }

    @Test
    void validateParticipantsFail() {
        Appointment app = new Appointment(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                10
        );

        assertThrows(IllegalArgumentException.class, app::validateParticipants);
    }

    @Test
    void validateTypeSuccess() {
        Appointment app = new Appointment(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                2
        );

        app.setType(AppointmentType.GROUP);

        assertDoesNotThrow(app::validateType);
    }

    @Test
    void validateTypeFail() {
        Appointment app = new Appointment(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                1
        );

        app.setType(AppointmentType.GROUP);

        assertThrows(IllegalArgumentException.class, app::validateType);
    }

    @Test
    void validateAllSuccess() {
        Appointment app = new Appointment(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                2
        );

        app.setType(AppointmentType.INDIVIDUAL);

        assertDoesNotThrow(app::validateAll);
    }

    @Test
    void validateAllFail() {
        Appointment app = new Appointment(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(5),
                10
        );

        app.setType(AppointmentType.GROUP);

        assertThrows(IllegalArgumentException.class, app::validateAll);
    }

    @Test
    void confirmChangesStatus() {
        Appointment app = new Appointment(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                1
        );

        app.confirm();

        assertEquals(AppointmentStatus.CONFIRMED, app.getStatus());
    }

    @Test
    void isFutureTrue() {
        Appointment app = new Appointment(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                1
        );

        assertTrue(app.isFuture());
    }

    @Test
    void isFutureFalse() {
        Appointment app = new Appointment(
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
                1
        );

        assertFalse(app.isFuture());
    }
}