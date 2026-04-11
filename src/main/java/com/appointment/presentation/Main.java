package com.appointment.presentation;

import com.appointment.domain.*;
import com.appointment.notification.*;
import com.appointment.repo.AppointmentRepo;
import com.appointment.service.*;
import com.appointment.strategy.TypeBasedRuleStrategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        List<User> users = new ArrayList<>();
        users.add(new User("balsam", "123"));
        users.add(new User("fawzia.y123456", "456"));

        List<User> admins = new ArrayList<>();
        admins.add(new User("admin", "admin"));

        Schedule schedule = new Schedule();
        List<TimeSlot> allSlots = new ArrayList<>();
        List<Appointment> appointments = new ArrayList<>();

        ensureWeeklyCalendar(schedule, allSlots);

        ScheduleService scheduleService = new ScheduleService(schedule);

        AppointmentRepo repo = new AppointmentRepo();
        AppointmentRuleEngine engine = new AppointmentRuleEngine();
        engine.addStrategy(new TypeBasedRuleStrategy());
        BookingService bookingService = new BookingService(repo, engine);

        NotificationService notification =
                new EmailNotificationService(
                        new EmailService("s12217816@stu.najah.edu", "xdhl usqv exvz eukl")
                );

        ReminderService reminderService = new ReminderService(notification);

        removePastData(allSlots, appointments);

        while (true) {
            removePastData(allSlots, appointments);
            ensureWeeklyCalendar(schedule, allSlots);

            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Admin");
            System.out.println("2. User");
            System.out.println("3. Exit");
            System.out.print("Choice: ");

            int mainChoice = readInt(input);

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
                    removePastData(allSlots, appointments);
                    ensureWeeklyCalendar(schedule, allSlots);

                    System.out.println("\n--- ADMIN MENU ---");
                    System.out.println("1. View All Appointments");
                    System.out.println("2. Cancel Appointment");
                    System.out.println("3. Modify Appointment");
                    System.out.println("4. Add Slots");
                    System.out.println("5. View Calendar");
                    System.out.println("6. Back");
                    System.out.print("Choice: ");

                    int c = readInt(input);

                    if (c == 1) {
                        if (appointments.isEmpty()) {
                            System.out.println("No appointments found.");
                            continue;
                        }

                        for (int i = 0; i < appointments.size(); i++) {
                            Appointment a = appointments.get(i);
                            System.out.println(
                                    i + " | User: " + a.getUser().getUsername()
                                            + " | Start: " + a.getStart().format(DATE_TIME_FORMAT)
                                            + " | End: " + a.getEnd().format(DATE_TIME_FORMAT)
                                            + " | Type: " + a.getType()
                            );
                        }
                    }

                    else if (c == 2) {
                        if (appointments.isEmpty()) {
                            System.out.println("No appointments to cancel.");
                            continue;
                        }

                        System.out.print("Appointment index: ");
                        int i = readInt(input);

                        if (i < 0 || i >= appointments.size()) {
                            System.out.println("❌ Invalid index");
                            continue;
                        }

                        Appointment app = appointments.get(i);

                        freeSlotsForAppointment(app, allSlots);
                        app.cancel();
                        appointments.remove(i);

                        notification.sendNotification(
                                app.getUser().getUsername() + "@gmail.com",
                                "Your appointment has been cancelled"
                        );

                        System.out.println("✅ Cancelled + Email Sent");
                    }

                    else if (c == 3) {
                        if (appointments.isEmpty()) {
                            System.out.println("No appointments to modify.");
                            continue;
                        }

                        System.out.print("Appointment index: ");
                        int i = readInt(input);

                        if (i < 0 || i >= appointments.size()) {
                            System.out.println("❌ Invalid index");
                            continue;
                        }

                        Appointment oldApp = appointments.get(i);
                        List<TimeSlot> oldSlots = getSlotsForAppointment(oldApp, allSlots);

                        markSlots(oldSlots, false);

                        printCalendar(allSlots);

                        System.out.print("Start slot index: ");
                        int startIndex = readInt(input);

                        System.out.print("Duration in minutes (30/60/90/120): ");
                        int durationMinutes = readInt(input);

                        if (!isValidDuration(durationMinutes)) {
                            markSlots(oldSlots, true);
                            System.out.println("❌ Invalid duration. Max is 120 minutes and it must be multiple of 30.");
                            continue;
                        }

                        List<TimeSlot> neededSlots =
                                getConsecutiveAvailableSlots(allSlots, startIndex, durationMinutes / 30);

                        if (neededSlots == null) {
                            markSlots(oldSlots, true);
                            System.out.println("❌ Not enough consecutive available slots.");
                            continue;
                        }

                        Appointment newApp = new Appointment(
                                neededSlots.get(0).getStart(),
                                neededSlots.get(neededSlots.size() - 1).getEnd(),
                                oldApp.getParticipants()
                        );
                        newApp.setUser(oldApp.getUser());
                        newApp.setType(oldApp.getType());
                        newApp.setTimeSlot(neededSlots.get(0));

                        boolean booked = bookingService.book(newApp);

                        if (!booked) {
                            markSlots(oldSlots, true);
                            System.out.println("❌ Modification failed بسبب مخالفة أحد الشروط");
                            continue;
                        }

                        markSlots(neededSlots, true);
                        appointments.set(i, newApp);

                        notification.sendNotification(
                                newApp.getUser().getUsername() + "@gmail.com",
                                "Your appointment has been updated to: "
                                        + newApp.getStart().format(DATE_TIME_FORMAT)
                                        + " -> " + newApp.getEnd().format(DATE_TIME_FORMAT)
                        );

                        System.out.println("✅ Modified + Email Sent");
                    }

                    else if (c == 4) {
                        input.nextLine();

                        try {
                            System.out.println("Enter start date/time (yyyy-MM-dd HH:mm): ");
                            String startText = input.nextLine();

                            System.out.println("Enter end date/time (yyyy-MM-dd HH:mm): ");
                            String endText = input.nextLine();

                            LocalDateTime start = LocalDateTime.parse(startText, DATE_TIME_FORMAT);
                            LocalDateTime end = LocalDateTime.parse(endText, DATE_TIME_FORMAT);

                            if (!start.isAfter(LocalDateTime.now())) {
                                System.out.println("❌ Start time must be in the future.");
                                continue;
                            }

                            if (!end.isAfter(start)) {
                                System.out.println("❌ End must be after start.");
                                continue;
                            }

                            if (start.getMinute() % 30 != 0 || end.getMinute() % 30 != 0) {
                                System.out.println("❌ Time must be on 30-minute boundaries.");
                                continue;
                            }

                            addHalfHourSlots(schedule, allSlots, start, end);
                            sortSlots(allSlots);

                            System.out.println("✅ Slots added successfully.");
                        } catch (Exception e) {
                            System.out.println("❌ Invalid date format.");
                        }
                    }

                    else if (c == 5) {
                        printCalendar(allSlots);
                    }

                    else if (c == 6) {
                        break;
                    }
                }
            }

            else if (mainChoice == 2) {
                System.out.println("Choose user:");
                for (int i = 0; i < users.size(); i++) {
                    System.out.println(i + " - " + users.get(i).getUsername());
                }

                int index = readInt(input);

                if (index < 0 || index >= users.size()) {
                    System.out.println("❌ Invalid user index");
                    continue;
                }

                User user = users.get(index);

                System.out.print("Password: ");
                if (!user.getPassword().equals(input.next())) {
                    System.out.println("❌ Wrong password");
                    continue;
                }

                while (true) {
                    removePastData(allSlots, appointments);
                    ensureWeeklyCalendar(schedule, allSlots);

                    System.out.println("\n--- USER MENU ---");
                    System.out.println("1. View Calendar");
                    System.out.println("2. Book");
                    System.out.println("3. Modify My Appointment");
                    System.out.println("4. Cancel My Appointment");
                    System.out.println("5. My Appointments");
                    System.out.println("6. Back");
                    System.out.print("Choice: ");

                    int c = readInt(input);

                    if (c == 1) {
                        printCalendar(allSlots);
                    }

                    else if (c == 2) {
                        printCalendar(allSlots);

                        System.out.print("Start slot index: ");
                        int startIndex = readInt(input);

                        System.out.print("Duration in minutes (30/60/90/120): ");
                        int durationMinutes = readInt(input);

                        if (!isValidDuration(durationMinutes)) {
                            System.out.println("❌ Invalid duration. Max is 120 minutes and it must be multiple of 30.");
                            continue;
                        }

                        List<TimeSlot> neededSlots =
                                getConsecutiveAvailableSlots(allSlots, startIndex, durationMinutes / 30);

                        if (neededSlots == null) {
                            System.out.println("❌ Not enough consecutive available slots.");
                            continue;
                        }

                        System.out.print("Participants: ");
                        int part = readInt(input);

                        Appointment app = new Appointment(
                                neededSlots.get(0).getStart(),
                                neededSlots.get(neededSlots.size() - 1).getEnd(),
                                part
                        );

                        app.setUser(user);
                        app.setTimeSlot(neededSlots.get(0));

                        AppointmentType[] types = AppointmentType.values();
                        for (int i = 0; i < types.length; i++) {
                            System.out.println(i + " - " + types[i]);
                        }

                        System.out.print("Type: ");
                        int typeIndex = readInt(input);

                        if (typeIndex < 0 || typeIndex >= types.length) {
                            System.out.println("❌ Invalid type");
                            continue;
                        }

                        app.setType(types[typeIndex]);

                        boolean booked = bookingService.book(app);

                        if (!booked) {
                            System.out.println("❌ Booking failed بسبب مخالفة أحد الشروط");
                            continue;
                        }

                        markSlots(neededSlots, true);
                        appointments.add(app);

                        reminderService.sendReminder(
                                user.getUsername() + "@gmail.com",
                                app
                        );

                        System.out.println("✅ Booked from "
                                + app.getStart().format(DATE_TIME_FORMAT)
                                + " to "
                                + app.getEnd().format(DATE_TIME_FORMAT));
                    }

                    else if (c == 3) {
                        List<Appointment> myAppointments = getUserAppointments(appointments, user);

                        if (myAppointments.isEmpty()) {
                            System.out.println("You have no appointments.");
                            continue;
                        }

                        for (int i = 0; i < myAppointments.size(); i++) {
                            Appointment a = myAppointments.get(i);
                            System.out.println(
                                    i + " | " + a.getStart().format(DATE_TIME_FORMAT)
                                            + " -> " + a.getEnd().format(DATE_TIME_FORMAT)
                                            + " | " + a.getType()
                            );
                        }

                        System.out.print("Choose your appointment index: ");
                        int myIndex = readInt(input);

                        if (myIndex < 0 || myIndex >= myAppointments.size()) {
                            System.out.println("❌ Invalid index");
                            continue;
                        }

                        Appointment oldApp = myAppointments.get(myIndex);
                        List<TimeSlot> oldSlots = getSlotsForAppointment(oldApp, allSlots);

                        markSlots(oldSlots, false);

                        printCalendar(allSlots);

                        System.out.print("New start slot index: ");
                        int startIndex = readInt(input);

                        System.out.print("New duration in minutes (30/60/90/120): ");
                        int durationMinutes = readInt(input);

                        if (!isValidDuration(durationMinutes)) {
                            markSlots(oldSlots, true);
                            System.out.println("❌ Invalid duration.");
                            continue;
                        }

                        List<TimeSlot> neededSlots =
                                getConsecutiveAvailableSlots(allSlots, startIndex, durationMinutes / 30);

                        if (neededSlots == null) {
                            markSlots(oldSlots, true);
                            System.out.println("❌ Not enough consecutive available slots.");
                            continue;
                        }

                        Appointment newApp = new Appointment(
                                neededSlots.get(0).getStart(),
                                neededSlots.get(neededSlots.size() - 1).getEnd(),
                                oldApp.getParticipants()
                        );
                        newApp.setUser(user);
                        newApp.setType(oldApp.getType());
                        newApp.setTimeSlot(neededSlots.get(0));

                        boolean booked = bookingService.book(newApp);

                        if (!booked) {
                            markSlots(oldSlots, true);
                            System.out.println("❌ Modification failed.");
                            continue;
                        }

                        markSlots(neededSlots, true);

                        int realIndex = appointments.indexOf(oldApp);
                        appointments.set(realIndex, newApp);

                        System.out.println("✅ Modified");
                    }

                    else if (c == 4) {
                        List<Appointment> myAppointments = getUserAppointments(appointments, user);

                        if (myAppointments.isEmpty()) {
                            System.out.println("You have no appointments.");
                            continue;
                        }

                        for (int i = 0; i < myAppointments.size(); i++) {
                            Appointment a = myAppointments.get(i);
                            System.out.println(
                                    i + " | " + a.getStart().format(DATE_TIME_FORMAT)
                                            + " -> " + a.getEnd().format(DATE_TIME_FORMAT)
                            );
                        }

                        System.out.print("Choose your appointment index: ");
                        int myIndex = readInt(input);

                        if (myIndex < 0 || myIndex >= myAppointments.size()) {
                            System.out.println("❌ Invalid index");
                            continue;
                        }

                        Appointment app = myAppointments.get(myIndex);

                        freeSlotsForAppointment(app, allSlots);
                        app.cancel();
                        appointments.remove(app);

                        System.out.println("✅ Cancelled");
                    }

                    else if (c == 5) {
                        List<Appointment> myAppointments = getUserAppointments(appointments, user);

                        if (myAppointments.isEmpty()) {
                            System.out.println("You have no appointments.");
                            continue;
                        }

                        for (int i = 0; i < myAppointments.size(); i++) {
                            Appointment a = myAppointments.get(i);
                            System.out.println(
                                    i + " | Start: " + a.getStart().format(DATE_TIME_FORMAT)
                                            + " | End: " + a.getEnd().format(DATE_TIME_FORMAT)
                                            + " | Type: " + a.getType()
                            );
                        }
                    }

                    else if (c == 6) {
                        break;
                    }
                }
            }

            else if (mainChoice == 3) {
                System.out.println("👋 Bye");
                break;
            }
        }
    }

    private static int readInt(Scanner input) {
        while (!input.hasNextInt()) {
            System.out.println("❌ Please enter a number.");
            input.next();
        }
        return input.nextInt();
    }

    private static boolean isValidDuration(int minutes) {
        return minutes > 0 && minutes <= 120 && minutes % 30 == 0;
    }

    private static void addHalfHourSlots(
            Schedule schedule,
            List<TimeSlot> allSlots,
            LocalDateTime start,
            LocalDateTime end
    ) {
        LocalDateTime current = start;

        while (current.isBefore(end)) {
            LocalDateTime next = current.plusMinutes(30);

            boolean exists = false;
            for (TimeSlot slot : allSlots) {
                if (slot.getStart().equals(current) && slot.getEnd().equals(next)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                TimeSlot slot = new TimeSlot(current, next);
                schedule.addSlot(slot);
                allSlots.add(slot);
            }

            current = next;
        }
    }

    private static void sortSlots(List<TimeSlot> allSlots) {
        allSlots.sort(Comparator.comparing(TimeSlot::getStart));
    }

    private static void printCalendar(List<TimeSlot> allSlots) {
        if (allSlots.isEmpty()) {
            System.out.println("No slots available.");
            return;
        }

        sortSlots(allSlots);

        System.out.println("\n========== CALENDAR ==========");

        LocalDate currentDate = null;

        for (int i = 0; i < allSlots.size(); i++) {
            TimeSlot slot = allSlots.get(i);

            if (currentDate == null || !slot.getStart().toLocalDate().equals(currentDate)) {
                currentDate = slot.getStart().toLocalDate();
                System.out.println("\n" + currentDate.format(DATE_FORMAT));
            }

            String status = slot.isBooked()
                    ? RED + "[BOOKED]" + RESET
                    : GREEN + "[AVAILABLE]" + RESET;

            System.out.println(
                    i + " | "
                            + slot.getStart().toLocalTime().format(TIME_FORMAT)
                            + " - "
                            + slot.getEnd().toLocalTime().format(TIME_FORMAT)
                            + " " + status
            );
        }
    }

    private static List<TimeSlot> getConsecutiveAvailableSlots(
            List<TimeSlot> allSlots,
            int startIndex,
            int neededCount
    ) {
        sortSlots(allSlots);

        if (startIndex < 0 || startIndex >= allSlots.size()) {
            return null;
        }

        if (startIndex + neededCount > allSlots.size()) {
            return null;
        }

        List<TimeSlot> result = new ArrayList<>();

        for (int i = startIndex; i < startIndex + neededCount; i++) {
            TimeSlot current = allSlots.get(i);

            if (current.isBooked()) {
                return null;
            }

            if (!result.isEmpty()) {
                TimeSlot previous = result.get(result.size() - 1);
                if (!previous.getEnd().equals(current.getStart())) {
                    return null;
                }
            }

            result.add(current);
        }

        return result;
    }

    private static void markSlots(List<TimeSlot> slots, boolean booked) {
        for (TimeSlot slot : slots) {
            slot.setBooked(booked);
        }
    }

    private static void freeSlotsForAppointment(Appointment appointment, List<TimeSlot> allSlots) {
        List<TimeSlot> slots = getSlotsForAppointment(appointment, allSlots);
        markSlots(slots, false);
    }

    private static List<TimeSlot> getSlotsForAppointment(Appointment appointment, List<TimeSlot> allSlots) {
        List<TimeSlot> result = new ArrayList<>();

        for (TimeSlot slot : allSlots) {
            boolean inside =
                    !slot.getStart().isBefore(appointment.getStart())
                            && !slot.getEnd().isAfter(appointment.getEnd());

            if (inside) {
                result.add(slot);
            }
        }

        result.sort(Comparator.comparing(TimeSlot::getStart));
        return result;
    }

    private static List<Appointment> getUserAppointments(List<Appointment> appointments, User user) {
        List<Appointment> result = new ArrayList<>();

        for (Appointment appointment : appointments) {
            if (appointment.getUser().equals(user)) {
                result.add(appointment);
            }
        }

        return result;
    }

    private static void removePastData(List<TimeSlot> allSlots, List<Appointment> appointments) {
        LocalDateTime now = LocalDateTime.now();

        appointments.removeIf(appointment -> !appointment.getEnd().isAfter(now));
        allSlots.removeIf(slot -> !slot.getEnd().isAfter(now));
    }

    private static void ensureWeeklyCalendar(Schedule schedule, List<TimeSlot> allSlots) {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(6);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (!hasAnySlotForDate(allSlots, date)) {
                addHalfHourSlots(
                        schedule,
                        allSlots,
                        LocalDateTime.of(date, LocalTime.of(9, 0)),
                        LocalDateTime.of(date, LocalTime.of(15, 0))
                );
            }
        }

        sortSlots(allSlots);
    }

    private static boolean hasAnySlotForDate(List<TimeSlot> allSlots, LocalDate date) {
        for (TimeSlot slot : allSlots) {
            if (slot.getStart().toLocalDate().equals(date)) {
                return true;
            }
        }
        return false;
    }
}