package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.repo.AppointmentRepo;

import java.time.Duration;

/**
 * Service responsible for booking appointments.
 * 
 * <p>This class validates basic booking rules before confirming and saving
 * an appointment. The current rules include:</p>
 * <ul>
 *   <li>Maximum appointment duration is 2 hours</li>
 *   <li>Maximum number of participants is 5</li>
 *   <li>Additional business rules validated through the rule engine</li>
 * </ul>
 * 
 * <p>If the appointment satisfies all rules, it is marked as confirmed
 * and stored in the appointment repository.</p>
 * 
 * @author Team
 * @version 1.0
 */
public class BookingService {

    private static final int MAX_PARTICIPANTS = 5;
    private static final int MAX_DURATION_HOURS = 2;

    private AppointmentRepo repo;
    private AppointmentRuleEngine ruleEngine;

    public BookingService(AppointmentRepo repo, AppointmentRuleEngine ruleEngine) {
        this.repo = repo;
        this.ruleEngine = ruleEngine;
    }

    public boolean book(Appointment appointment) {

        if (appointment == null) {
            return false;
        }

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

        if (!ruleEngine.validate(appointment)) {
            return false;
        }

        appointment.confirm();
        repo.save(appointment);

        return true;
    }
}