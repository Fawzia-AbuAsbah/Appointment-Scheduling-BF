package com.appointment.domain;

import java.time.LocalDateTime;

public class Appointment {

    private LocalDateTime start;
    private LocalDateTime end;
    private int participants;
    private AppointmentStatus status;

    private TimeSlot timeSlot;
    private User user;
    private AppointmentType type;
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
    public AppointmentType getType() {
        return type;
    }

    public void setType(AppointmentType type) {
        this.type = type;
    }
    
 

 // Duration Rule
 public void validateDuration() {
     long hours = java.time.Duration.between(start, end).toHours();

     if (hours > 2) {
         throw new IllegalArgumentException("❌ Max duration is 2 hours");
     }
 }

 // Participants Rule
 public void validateParticipants() {
     if (participants > 5) {
         throw new IllegalArgumentException("❌ Max participants is 5");
     }
 }

 // Type Rule (Sprint 5 🔥)
 public void validateType() {
     if (type == AppointmentType.GROUP && participants < 2) {
         throw new IllegalArgumentException("❌ Group appointment needs at least 2 participants");
     }
 }
 
 

 public void validateAll() {
	  validateDuration();
	    validateParticipants();
	    validateType();
	
 }
}