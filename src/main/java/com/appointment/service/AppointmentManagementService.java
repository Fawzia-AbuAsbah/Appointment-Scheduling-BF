package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.domain.TimeSlot;
import com.appointment.domain.User;
import com.appointment.exception.InvalidAppointmentException;
import com.appointment.exception.UnauthorizedActionException;

public class AppointmentManagementService {

    // Edit appointment
    public void modifyAppointment(Appointment appointment,
                                  TimeSlot newSlot,
                                  User user) {

        if (!appointment.isFuture()) {
            throw new InvalidAppointmentException("Cannot modify past appointment");
        }

        if (!appointment.getUser().equals(user)) {
            throw new UnauthorizedActionException("Unauthorized");
        }

        appointment.getTimeSlot().setBooked(false);
        newSlot.setBooked(true);

        appointment.setTimeSlot(newSlot);
    }

    // Cancel by user
    public void cancelAppointment(Appointment appointment, User user) {

        if (!appointment.isFuture()) {
            throw new InvalidAppointmentException("Cannot cancel past appointment");
        }

        if (!appointment.getUser().equals(user)) {
            throw new UnauthorizedActionException("Unauthorized");
        }

        appointment.getTimeSlot().setBooked(false);
    }

    // Cancel by admin
    public void cancelAsAdmin(Appointment appointment) {

        appointment.getTimeSlot().setBooked(false);
    }
}