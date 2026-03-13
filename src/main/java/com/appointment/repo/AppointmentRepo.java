package com.appointment.repo;

import com.appointment.domain.Appointment;

import java.util.ArrayList;
import java.util.List;

public class AppointmentRepo {

    private List<Appointment> appointments = new ArrayList<>();

    public void save(Appointment appointment) {
        appointments.add(appointment);
    }

    public List<Appointment> findAll() {
        return appointments;
    }
}