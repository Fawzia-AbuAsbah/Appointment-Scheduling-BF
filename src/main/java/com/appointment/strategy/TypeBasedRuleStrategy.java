package com.appointment.strategy;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentType;

public class TypeBasedRuleStrategy implements AppointmentRuleStrategy {

    @Override
    public boolean isValid(Appointment appointment) {

        if (appointment.getType() == AppointmentType.GROUP) {
            return appointment.getParticipants() >= 2;
        }

        if (appointment.getType() == AppointmentType.INDIVIDUAL) {
            return appointment.getParticipants() == 1;
        }

        if (appointment.getType() == AppointmentType.URGENT) {
            return appointment.isFuture();
        }

        return true;
    }
}