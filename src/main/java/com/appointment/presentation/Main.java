/*package com.appointment.presentation;

import com.appointment.domain.Schedule;
import com.appointment.domain.TimeSlot;
import com.appointment.repo.AdminRepo;
import com.appointment.service.AuthenticationService;
import com.appointment.service.ScheduleService;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        AdminRepo repo = new AdminRepo();
        AuthenticationService authService = new AuthenticationService(repo);

        boolean success = authService.login("admin", "1234");

        if (success) {
            System.out.println("Login successful");
        } else {
            System.out.println("Invalid credentials");
        }

        Schedule schedule = new Schedule();

        schedule.addSlot(new TimeSlot(
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1)
        ));

        ScheduleService service = new ScheduleService(schedule);

        System.out.println("Available slots: " +
                service.viewAvailableSlots().size());

        authService.logout();
    }
}*/