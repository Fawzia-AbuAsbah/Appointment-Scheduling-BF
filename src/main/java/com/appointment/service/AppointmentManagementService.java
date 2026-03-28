package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.domain.TimeSlot;
import com.appointment.domain.User;

public class AppointmentManagementService {

    //Edit appointment 
    public void modifyAppointment(Appointment appointment,
                                  TimeSlot newSlot,
                                  User user) {

        if (!appointment.isFuture()) {
            throw new IllegalStateException("Cannot modify past appointment");
        }

        if (!appointment.getUser().equals(user)) {
            throw new SecurityException("Unauthorized");
        }

       
        appointment.getTimeSlot().setBooked(false);

        //
        newSlot.setBooked(true);

        appointment.setTimeSlot(newSlot);
    }

    
    public void cancelAppointment(Appointment appointment, User user) {

        if (!appointment.isFuture()) {
            throw new IllegalStateException("Cannot cancel past appointment");
        }

        if (!appointment.getUser().equals(user)) {
            throw new SecurityException("Unauthorized");
        }

        appointment.getTimeSlot().setBooked(false);
    }

    // 
    public void cancelAsAdmin(Appointment appointment) {

        appointment.getTimeSlot().setBooked(false);
    }
}