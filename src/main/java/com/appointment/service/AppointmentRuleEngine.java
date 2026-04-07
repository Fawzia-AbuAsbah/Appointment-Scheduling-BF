package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.strategy.AppointmentRuleStrategy;

import java.util.ArrayList;
import java.util.List;
/**
 * Rule engine used to validate appointments against a set of business rules.
 * 
 * <p>This class applies the Strategy design pattern, where each validation
 * rule is represented by an implementation of {@code AppointmentRuleStrategy}.
 * All added strategies are executed in sequence.</p>
 * 
 * <p>If any strategy fails, validation stops and the appointment is considered invalid.</p>
 * 
 * @author Team
 * @version 1.0
 */
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