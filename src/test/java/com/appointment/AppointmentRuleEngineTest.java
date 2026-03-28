package com.appointment;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentType;
import com.appointment.service.AppointmentRuleEngine;
import com.appointment.strategy.DurationRuleStrategy;
import com.appointment.strategy.ParticipantRuleStrategy;
import com.appointment.strategy.TypeBasedRuleStrategy;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentRuleEngineTest {

    @Test
    void validAppointment() {

        Appointment app = new Appointment(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                2
        );
        app.setType(AppointmentType.GROUP);

        AppointmentRuleEngine engine = new AppointmentRuleEngine();

        engine.addStrategy(new DurationRuleStrategy());
        engine.addStrategy(new ParticipantRuleStrategy());
        engine.addStrategy(new TypeBasedRuleStrategy());

        assertTrue(engine.validate(app));
    }

    @Test
    void durationFail() {

        Appointment app = new Appointment(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(6),
                2
        );

        AppointmentRuleEngine engine = new AppointmentRuleEngine();
        engine.addStrategy(new DurationRuleStrategy());

        assertFalse(engine.validate(app));
    }

    @Test
    void participantsFail() {

        Appointment app = new Appointment(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                10
        );

        AppointmentRuleEngine engine = new AppointmentRuleEngine();
        engine.addStrategy(new ParticipantRuleStrategy());

        assertFalse(engine.validate(app));
    }

    @Test
    void groupFail() {

        Appointment app = new Appointment(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                1
        );
        app.setType(AppointmentType.GROUP);

        AppointmentRuleEngine engine = new AppointmentRuleEngine();
        engine.addStrategy(new TypeBasedRuleStrategy());

        assertFalse(engine.validate(app));
    }

    @Test
    void individualFail() {

        Appointment app = new Appointment(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                3
        );
        app.setType(AppointmentType.INDIVIDUAL);

        AppointmentRuleEngine engine = new AppointmentRuleEngine();
        engine.addStrategy(new TypeBasedRuleStrategy());

        assertFalse(engine.validate(app));
    }
}