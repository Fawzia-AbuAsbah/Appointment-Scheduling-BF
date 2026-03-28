package com.appointment.strategy;

import com.appointment.domain.Appointment;

public class ParticipantRuleStrategy implements AppointmentRuleStrategy {

    @Override
    public boolean isValid(Appointment appointment) {

        return appointment.getParticipants() <= 5;
    }
}