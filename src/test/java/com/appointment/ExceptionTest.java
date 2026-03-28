package com.appointment;

import com.appointment.exception.InvalidAppointmentException;
import com.appointment.exception.UnauthorizedActionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionTest {

    @Test
    void testInvalidAppointmentException() {

        InvalidAppointmentException ex =
                new InvalidAppointmentException("Invalid appointment");

        assertEquals("Invalid appointment", ex.getMessage());
    }

    @Test
    void testUnauthorizedActionException() {

        UnauthorizedActionException ex =
                new UnauthorizedActionException("Not allowed");

        assertEquals("Not allowed", ex.getMessage());
    }

    @Test
    void testThrowInvalidAppointmentException() {

        assertThrows(InvalidAppointmentException.class, () -> {
            throw new InvalidAppointmentException("Error");
        });
    }

    @Test
    void testThrowUnauthorizedActionException() {

        assertThrows(UnauthorizedActionException.class, () -> {
            throw new UnauthorizedActionException("Error");
        });
    }
}