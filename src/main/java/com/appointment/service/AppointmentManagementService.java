package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.domain.TimeSlot;
import com.appointment.domain.User;
import com.appointment.exception.InvalidAppointmentException;
import com.appointment.exception.UnauthorizedActionException;

public class AppointmentManagementService {

    // MODIFY
    public void modifyAppointment(Appointment appointment,
                                  TimeSlot newSlot,
                                  User user) {

        if (appointment == null || newSlot == null) {
            throw new InvalidAppointmentException("Invalid data");
        }

        if (!appointment.isFuture()) {
            throw new InvalidAppointmentException("Cannot modify past appointment");
        }

        if (!appointment.getUser().equals(user)) {
            throw new UnauthorizedActionException("Unauthorized");
        }

        if (appointment.getTimeSlot() == null) {
            throw new InvalidAppointmentException("No slot assigned");
        }

        // فك القديم
        appointment.getTimeSlot().setBooked(false);

        // حجز الجديد
        newSlot.setBooked(true);

        appointment.setTimeSlot(newSlot);
    }

    // CANCEL USER
    public void cancelAppointment(Appointment appointment, User user) {

        if (!appointment.isFuture()) {
            throw new InvalidAppointmentException("Cannot cancel past appointment");
        }

        if (!appointment.getUser().equals(user)) {
            throw new UnauthorizedActionException("Unauthorized");
        }

        appointment.getTimeSlot().setBooked(false);
    }

    // CANCEL ADMIN
    public void cancelAsAdmin(Appointment appointment) {

        if (appointment.getTimeSlot() != null) {
            appointment.getTimeSlot().setBooked(false);
        }
    }
}