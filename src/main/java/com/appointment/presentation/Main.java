/*package com.appointment.presentation;

import com.appointment.domain.*;
import com.appointment.notification.*;
import com.appointment.service.*;
import com.appointment.repo.AppointmentRepo;
import com.appointment.strategy.TypeBasedRuleStrategy;
import com.appointment.service.AppointmentRuleEngine;
import com.appointment.service.BookingService;
import java.time.LocalDateTime;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        List<User> users = new ArrayList<>();
        users.add(new User("balsam", "123"));
        users.add(new User("fawzia.y123456", "456"));

        List<User> admins = new ArrayList<>();
        admins.add(new User("admin", "admin"));

        Schedule schedule = new Schedule();

        schedule.addSlot(new TimeSlot(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        ));
        schedule.addSlot(new TimeSlot(
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4)
        ));
        schedule.addSlot(new TimeSlot(
                LocalDateTime.now().plusHours(5),
                LocalDateTime.now().plusHours(6)
        ));

        ScheduleService scheduleService = new ScheduleService(schedule);
        AppointmentManagementService managementService = new AppointmentManagementService();

        List<Appointment> appointments = new ArrayList<>();

        
        AppointmentRepo repo = new AppointmentRepo();

        AppointmentRuleEngine engine = new AppointmentRuleEngine();
        engine.addStrategy(new TypeBasedRuleStrategy());

        BookingService bookingService = new BookingService(repo, engine);

       
        NotificationService notification =
                new EmailNotificationService(
                        new EmailService("s12217816@stu.najah.edu", "xdhl usqv exvz eukl")
                );

        ReminderService reminderService = new ReminderService(notification);



        while (true) {

            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Admin");
            System.out.println("2. User");
            System.out.println("3. Exit");

            int mainChoice = input.nextInt();

            // ================= ADMIN =================
            if (mainChoice == 1) {

                System.out.print("Username: ");
                String u = input.next();

                System.out.print("Password: ");
                String p = input.next();

                boolean valid = admins.stream()
                        .anyMatch(a -> a.getUsername().equals(u) && a.getPassword().equals(p));

                if (!valid) {
                    System.out.println("❌ Wrong admin login");
                    continue;
                }

                while (true) {
                    System.out.println("\n--- ADMIN MENU ---");
                    System.out.println("1. View All");
                    System.out.println("2. Cancel");
                    System.out.println("3. Modify");
                    System.out.println("4. Back");

                    int c = input.nextInt();

                    if (c == 1) {
                        for (int i = 0; i < appointments.size(); i++) {
                            Appointment a = appointments.get(i);
                            System.out.println(i + " | " +
                                    a.getUser().getUsername() +
                                    " | " + a.getTimeSlot().getStart());
                        }
                    }

                    else if (c == 2) {
                        System.out.print("Index: ");
                        int i = input.nextInt();

                        Appointment app = appointments.get(i);

                        managementService.cancelAsAdmin(app);
                        appointments.remove(i); 

                        
                        notification.sendNotification(
                        	    app.getUser().getUsername() + "@gmail.com",
                        	    "Your appointment has been cancelled"
                        	);
                        System.out.println("✅ Cancelled + Email Sent");
                    }
                    
                    
                    
                    else if (c == 3) {

                        System.out.print("Index: ");
                        int i = input.nextInt();

                        Appointment app = appointments.get(i);

                        List<TimeSlot> available = scheduleService.viewAvailableSlots();

                        for (int j = 0; j < available.size(); j++) {
                            System.out.println(j + " - " + available.get(j).getStart());
                        }

                        System.out.print("New slot: ");
                        TimeSlot newSlot = available.get(input.nextInt());

                        // admin bypass 
                        managementService.modifyAppointment(
                                app,
                                newSlot,
                                app.getUser()
                        );

                        //  EMAIL 
                        notification.sendNotification(
                        	    app.getUser().getUsername() + "@gmail.com",
                        	    "Your appointment has been updated to: "
                        	            + app.getTimeSlot().getStart()
                        	);
                        System.out.println("✅ Modified + Email Sent");
                    }

                    else if (c == 4) break;
                }
            }

            // ================= USER =================
            else if (mainChoice == 2) {

                System.out.println("Choose user:");
                for (int i = 0; i < users.size(); i++) {
                    System.out.println(i + " - " + users.get(i).getUsername());
                }

                int index = input.nextInt();
                User user = users.get(index);

                System.out.print("Password: ");
                if (!user.getPassword().equals(input.next())) {
                    System.out.println("❌ Wrong password");
                    continue;
                }

                while (true) {

                    System.out.println("\n--- USER MENU ---");
                    System.out.println("1. View Slots");
                    System.out.println("2. Book");
                    System.out.println("3. Modify");
                    System.out.println("4. Cancel");
                    System.out.println("5. My Appointments");
                    System.out.println("6. Back");

                    int c = input.nextInt();

                    // VIEW
                    if (c == 1) {
                        List<TimeSlot> available = scheduleService.viewAvailableSlots();

                        for (int i = 0; i < available.size(); i++) {
                            System.out.println(i + " - " + available.get(i).getStart());
                        }
                    }

                    // BOOK
                    else if (c == 2) {

                        List<TimeSlot> available = scheduleService.viewAvailableSlots();

                        System.out.print("Slot: ");
                        TimeSlot chosen = available.get(input.nextInt());

                        System.out.print("Participants: ");
                        int part = input.nextInt();

                        Appointment app = new Appointment(
                                chosen.getStart(),
                                chosen.getEnd(),
                                part
                        );

                        app.setUser(user);
                        app.setTimeSlot(chosen);

                        AppointmentType[] types = AppointmentType.values();
                        for (int i = 0; i < types.length; i++) {
                            System.out.println(i + " - " + types[i]);
                        }

                        app.setType(types[input.nextInt()]);
                        boolean booked = bookingService.book(app);

                        if (!booked) {
                            System.out.println("❌ Booking failed بسبب مخالفة أحد الشروط");
                            continue;
                        }

                        chosen.setBooked(true);
                        appointments.add(app);

                        reminderService.sendReminder(
                                user.getUsername() + "@gmail.com",
                                app
                        );

                        System.out.println("✅ Booked + Email Sent!");
                    }

                    // MODIFY 
                    else if (c == 3) {

                        System.out.print("Index: ");
                        int i = input.nextInt();

                        Appointment app = appointments.get(i);

                        List<TimeSlot> available = scheduleService.viewAvailableSlots();

                        for (int j = 0; j < available.size(); j++) {
                            System.out.println(j + " - " + available.get(j).getStart());
                        }

                        System.out.print("New slot: ");
                        TimeSlot newSlot = available.get(input.nextInt());

                        managementService.modifyAppointment(app, newSlot, user);

                        System.out.println("✅ Modified");
                    }

                    // CANCEL 
                    else if (c == 4) {

                        System.out.print("Index: ");
                        int i = input.nextInt();

                        Appointment app = appointments.get(i);

                        managementService.cancelAppointment(app, user);
                        appointments.remove(i); 

                        System.out.println("✅ Cancelled");
                    }

                    // MY APPOINTMENTS 
                    else if (c == 5) {

                        for (int i = 0; i < appointments.size(); i++) {
                            if (appointments.get(i).getUser().equals(user)) {
                                System.out.println(i + " - " +
                                        appointments.get(i).getTimeSlot().getStart());
                            }
                        }
                    }

                    else if (c == 6) break;
                }
            }

            else if (mainChoice == 3) {
                System.out.println("👋 Bye");
                break;
            }
        }
    }
}*/