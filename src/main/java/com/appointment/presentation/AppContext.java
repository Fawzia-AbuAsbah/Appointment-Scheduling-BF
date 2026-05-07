package com.appointment.presentation;

import com.appointment.domain.Appointment;
import com.appointment.domain.Schedule;
import com.appointment.domain.TimeSlot;
import com.appointment.domain.User;
import com.appointment.notification.NotificationService;
import com.appointment.service.BookingService;
import com.appointment.service.ReminderService;

import java.util.List;
import java.util.Scanner;

public class AppContext {

    public Scanner input;
    public Schedule schedule;
    public List<TimeSlot> allSlots;
    public List<Appointment> appointments;
    public BookingService bookingService;
    public NotificationService notification;
    public ReminderService reminderService;
    public List<User> users;
    public List<User> admins;

    public AppContext(
            Scanner input,
            Schedule schedule,
            List<TimeSlot> allSlots,
            List<Appointment> appointments,
            BookingService bookingService,
            NotificationService notification,
            ReminderService reminderService,
            List<User> users,
            List<User> admins
    ) {
        this.input = input;
        this.schedule = schedule;
        this.allSlots = allSlots;
        this.appointments = appointments;
        this.bookingService = bookingService;
        this.notification = notification;
        this.reminderService = reminderService;
        this.users = users;
        this.admins = admins;
    }
}