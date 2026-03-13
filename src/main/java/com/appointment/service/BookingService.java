package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.repo.AppointmentRepo;

import java.time.Duration;

public class BookingService {

    private static final int MAX_PARTICIPANTS = 5;
    private static final int MAX_DURATION_HOURS = 2;

    private AppointmentRepo repo;

    public BookingService(AppointmentRepo repo) {
        this.repo = repo;
    }

    public boolean book(Appointment appointment) {

        long duration = Duration.between(
                appointment.getStart(),
                appointment.getEnd()
        ).toHours();

        if (duration > MAX_DURATION_HOURS) {
            return false;
        }

        if (appointment.getParticipants() > MAX_PARTICIPANTS) {
            return false;
        }

        appointment.confirm();
        repo.save(appointment);

        return true;
    }
}