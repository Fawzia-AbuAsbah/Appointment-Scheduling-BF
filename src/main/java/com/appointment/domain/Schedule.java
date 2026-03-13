package com.appointment.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages appointment time slots.
 */
public class Schedule {

    private List<TimeSlot> slots = new ArrayList<>();

    public void addSlot(TimeSlot slot) {
        slots.add(slot);
    }

    public List<TimeSlot> getAvailableSlots() {

        List<TimeSlot> available = new ArrayList<>();

        for (TimeSlot slot : slots) {
            if (!slot.isBooked()) {
                available.add(slot);
            }
        }

        return available;
    }
}