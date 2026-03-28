package com.appointment;

import com.appointment.domain.*;
import com.appointment.service.AppointmentManagementService;
import com.appointment.exception.InvalidAppointmentException;
import com.appointment.exception.UnauthorizedActionException;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentManagementServiceTest {

    @Test
    void modifySuccess() {

        User user = new User("u", "123");

        TimeSlot oldSlot = new TimeSlot(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        TimeSlot newSlot = new TimeSlot(
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4)
        );

        oldSlot.setBooked(true);

        Appointment app = new Appointment(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                1
        );

        app.setUser(user);
        app.setTimeSlot(oldSlot);

        AppointmentManagementService service =
                new AppointmentManagementService();

        service.modifyAppointment(app, newSlot, user);

        assertTrue(newSlot.isBooked());
        assertFalse(oldSlot.isBooked());
    }

    @Test
    void modifyPastAppointment() {

        User user = new User("u", "123");

        TimeSlot newSlot = new TimeSlot(
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4)
        );

        Appointment app = new Appointment(
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
                1
        );

        app.setUser(user);

        AppointmentManagementService service =
                new AppointmentManagementService();

        assertThrows(InvalidAppointmentException.class, () -> {
            service.modifyAppointment(app, newSlot, user);
        });
    }

    @Test
    void modifyUnauthorized() {

        User owner = new User("u1", "123");
        User other = new User("u2", "123");

        TimeSlot oldSlot = new TimeSlot(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        TimeSlot newSlot = new TimeSlot(
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4)
        );

        Appointment app = new Appointment(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                1
        );

        app.setUser(owner);
        app.setTimeSlot(oldSlot);

        AppointmentManagementService service =
                new AppointmentManagementService();

        assertThrows(UnauthorizedActionException.class, () -> {
            service.modifyAppointment(app, newSlot, other);
        });
    }

    @Test
    void cancelSuccess() {

        User user = new User("u", "123");

        TimeSlot slot = new TimeSlot(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        slot.setBooked(true);

        Appointment app = new Appointment(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                1
        );

        app.setUser(user);
        app.setTimeSlot(slot);

        AppointmentManagementService service =
                new AppointmentManagementService();

        service.cancelAppointment(app, user);

        assertFalse(slot.isBooked());
    }

    @Test
    void cancelPast() {

        User user = new User("u", "123");

        Appointment app = new Appointment(
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
                1
        );

        app.setUser(user);

        AppointmentManagementService service =
                new AppointmentManagementService();

        assertThrows(InvalidAppointmentException.class, () -> {
            service.cancelAppointment(app, user);
        });
    }

    @Test
    void adminCancel() {

        TimeSlot slot = new TimeSlot(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        slot.setBooked(true);

        Appointment app = new Appointment(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                1
        );

        app.setTimeSlot(slot);

        AppointmentManagementService service =
                new AppointmentManagementService();

        service.cancelAsAdmin(app);

        assertFalse(slot.isBooked());
    }
}