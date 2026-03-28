package com.appointment.domain;

import java.time.LocalDateTime;

public class Appointment {

    private LocalDateTime start;
    private LocalDateTime end;
    private int participants;
    private AppointmentStatus status;

    private TimeSlot timeSlot;
    private User user;

    public Appointment(LocalDateTime start, LocalDateTime end, int participants) {
        this.start = start;
        this.end = end;
        this.participants = participants;
        this.status = AppointmentStatus.PENDING;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public int getParticipants() {
        return participants;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void confirm() {
        this.status = AppointmentStatus.CONFIRMED;
    }


    public boolean isFuture() {
        return start.isAfter(LocalDateTime.now());
    }

    
    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}