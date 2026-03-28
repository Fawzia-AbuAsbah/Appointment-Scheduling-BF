package com.appointment.strategy;

import com.appointment.domain.Appointment;

import java.time.Duration;

public class DurationRuleStrategy implements AppointmentRuleStrategy {

    @Override
    public boolean isValid(Appointment appointment) {

        long minutes = Duration.between(
                appointment.getStart(),
                appointment.getEnd()
        ).toMinutes();

        return minutes <= 120; // max 2 hours
    }
}