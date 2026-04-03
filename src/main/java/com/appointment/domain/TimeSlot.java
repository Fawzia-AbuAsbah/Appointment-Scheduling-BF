package com.appointment.domain;

import java.time.LocalDateTime;

/**
 * Represents appointment time slot.
 */
public class TimeSlot {

    private LocalDateTime start;
    private LocalDateTime end;
    private boolean booked;

    public TimeSlot(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
        this.booked = false;
    }
    
    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public boolean isBooked() {
        return booked;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}