package com.appointment.service;

import com.appointment.domain.Schedule;
import com.appointment.domain.TimeSlot;

import java.util.List;

/**
 * Handles appointment slot operations.
 */
public class ScheduleService {

    private Schedule schedule;

    public ScheduleService(Schedule schedule) {
        this.schedule = schedule;
    }

    public List<TimeSlot> viewAvailableSlots() {
        return schedule.getAvailableSlots();
    }
}