package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.strategy.AppointmentRuleStrategy;

import java.util.ArrayList;
import java.util.List;

public class AppointmentRuleEngine {

    private List<AppointmentRuleStrategy> strategies = new ArrayList<>();

    public void addStrategy(AppointmentRuleStrategy strategy) {
        strategies.add(strategy);
    }

    public boolean validate(Appointment appointment) {

        for (AppointmentRuleStrategy strategy : strategies) {
            if (!strategy.isValid(appointment)) {
                return false;
            }
        }

        return true;
    }
}