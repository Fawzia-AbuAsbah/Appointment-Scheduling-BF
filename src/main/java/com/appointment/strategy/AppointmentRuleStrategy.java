package com.appointment.strategy;

import com.appointment.domain.Appointment;

public interface AppointmentRuleStrategy {

    boolean isValid(Appointment appointment);
}