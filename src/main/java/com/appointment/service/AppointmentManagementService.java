package com.appointment.service;

import com.appointment.domain.Appointment; 
import com.appointment.domain.TimeSlot;
import com.appointment.domain.User;
import com.appointment.exception.InvalidAppointmentException;
import com.appointment.exception.UnauthorizedActionException;
/**
 * Service responsible for managing existing appointments.
 * 
 * <p>This service allows users to modify or cancel their future appointments,
 * while also allowing administrators to cancel appointments when needed.</p>
 * 
 * <p>The service enforces important business rules such as:</p>
 * <ul>
 *   <li>An appointment cannot be modified or cancelled if it is in the past</li>
 *   <li>Only the owner of the appointment can modify or cancel it as a user</li>
 *   <li>An administrator can cancel an appointment regardless of ownership</li>
 * </ul>
 * 
 * <p>If invalid data or unauthorized actions are detected, custom exceptions
 * are thrown to prevent illegal operations.</p>
 * 
 * @author Team
 * @version 1.0
 */
public class AppointmentManagementService {

    // MODIFY
	public void modifyAppointment(Appointment appointment, TimeSlot newSlot, User user) {

	    if (appointment == null || newSlot == null || user == null) {
	        throw new InvalidAppointmentException("Invalid data");
	    }

	    if (!appointment.isFuture()) {
	        throw new InvalidAppointmentException("Cannot modify past appointment");
	    }

	    if (appointment.getUser() == null || !appointment.getUser().equals(user)) {
	        throw new UnauthorizedActionException("Unauthorized");
	    }

	    if (newSlot.isBooked()) {
	        throw new InvalidAppointmentException("New slot is already booked");
	    }

	    if (appointment.getTimeSlot() != null) {
	        appointment.getTimeSlot().setBooked(false);
	    }

	    newSlot.setBooked(true);
	    appointment.setTimeSlot(newSlot);
	}

    // CANCEL USER
    public void cancelAppointment(Appointment appointment, User user) {

        if (appointment == null || user == null) {
            throw new InvalidAppointmentException("Invalid data");
        }

        if (!appointment.isFuture()) {
            throw new InvalidAppointmentException("Cannot cancel past appointment");
        }

        if (appointment.getUser() == null || !appointment.getUser().equals(user)) {
            throw new UnauthorizedActionException("Unauthorized");
        }

        if (appointment.getTimeSlot() != null) {
            appointment.getTimeSlot().setBooked(false);
        }

        appointment.cancel();
    }
    // CANCEL ADMIN
    public void cancelAsAdmin(Appointment appointment) {

        if (appointment == null) {
            throw new InvalidAppointmentException("Invalid data");
        }

        if (appointment.getTimeSlot() != null) {
            appointment.getTimeSlot().setBooked(false);
        }

        appointment.cancel();
    }
}