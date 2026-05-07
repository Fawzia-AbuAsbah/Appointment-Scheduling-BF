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

    private static final String CHOICE_PROMPT = "Choice: ";
    private static final String EMAIL_DOMAIN = "@gmail.com";
    private static final String INVALID_INDEX_MESSAGE = "❌ Invalid index";
    private static final String NO_APPOINTMENTS_MESSAGE = "You have no appointments.";
    private static final String NO_CONSECUTIVE_SLOTS_MESSAGE = "❌ Not enough consecutive available slots.";

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        List<User> users = createUsers();
        List<User> admins = createAdmins();

        Schedule schedule = new Schedule();
        List<TimeSlot> allSlots = new ArrayList<>();
        List<Appointment> appointments = new ArrayList<>();

        ensureWeeklyCalendar(schedule, allSlots);

        AppointmentRepo repo = new AppointmentRepo();
        AppointmentRuleEngine engine = new AppointmentRuleEngine();
        engine.addStrategy(new TypeBasedRuleStrategy());
        BookingService bookingService = new BookingService(repo, engine);

        NotificationService notification =
                new EmailNotificationService(
                        new EmailService("s12217816@stu.najah.edu", "xdhl usqv exvz eukl")
                );

        ReminderService reminderService = new ReminderService(notification);

        runMainMenu(
                input,
                users,
                admins,
                schedule,
                allSlots,
                appointments,
                bookingService,
                notification,
                reminderService
        );
    }

    private static List<User> createUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User("balsamadnanmashaqi", "123"));
        users.add(new User("fawzia.y123456", "456"));
        return users;
    }

    private static List<User> createAdmins() {
        List<User> admins = new ArrayList<>();
        admins.add(new User("admin", "admin"));
        return admins;
    }

    private static void runMainMenu(
            Scanner input,
            List<User> users,
            List<User> admins,
            Schedule schedule,
            List<TimeSlot> allSlots,
            List<Appointment> appointments,
            BookingService bookingService,
            NotificationService notification,
            ReminderService reminderService
    ) {
        while (true) {
            refreshData(schedule, allSlots, appointments);

            printMainMenu();
            int mainChoice = readInt(input);

            if (mainChoice == 1) {
                handleAdminLogin(
                        input,
                        admins,
                        schedule,
                        allSlots,
                        appointments,
                        bookingService,
                        notification,
                        reminderService
                );
            } else if (mainChoice == 2) {
                handleUserLogin(
                        input,
                        users,
                        schedule,
                        allSlots,
                        appointments,
                        bookingService,
                        reminderService
                );
            } else if (mainChoice == 3) {
                System.out.println("👋 Bye");
                break;
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Admin");
        System.out.println("2. User");
        System.out.println("3. Exit");
        System.out.print(CHOICE_PROMPT);
    }

    private static void handleAdminLogin(
            Scanner input,
            List<User> admins,
            Schedule schedule,
            List<TimeSlot> allSlots,
            List<Appointment> appointments,
            BookingService bookingService,
            NotificationService notification,
            ReminderService reminderService
    ) {
        System.out.print("Username: ");
        String username = input.next();

        System.out.print("Password: ");
        String password = input.next();

        if (!isValidLogin(admins, username, password)) {
            System.out.println("❌ Wrong admin login");
            return;
        }

        handleAdminMenu(
                input,
                schedule,
                allSlots,
                appointments,
                bookingService,
                notification,
                reminderService
        );
    }

    private static boolean isValidLogin(List<User> users, String username, String password) {
        return users.stream()
                .anyMatch(user -> user.getUsername().equals(username)
                        && user.getPassword().equals(password));
    }

    private static void handleAdminMenu(
            Scanner input,
            Schedule schedule,
            List<TimeSlot> allSlots,
            List<Appointment> appointments,
            BookingService bookingService,
            NotificationService notification,
            ReminderService reminderService
    ) {
        while (true) {
            refreshData(schedule, allSlots, appointments);

            printAdminMenu();
            int choice = readInt(input);

            if (choice == 1) {
                viewAllAppointments(appointments);
            } else if (choice == 2) {
                adminCancelAppointment(input, allSlots, appointments, notification);
            } else if (choice == 3) {
                adminModifyAppointment(input, allSlots, appointments, bookingService, notification);
            } else if (choice == 4) {
                addSlotsByAdmin(input, schedule, allSlots);
            } else if (choice == 5) {
                printCalendar(allSlots);
            } else if (choice == 6) {
                sendRemindersToAllUsers(appointments, reminderService);
            } else if (choice == 7) {
                break;
            }
        }
    }

    private static void printAdminMenu() {
        System.out.println("\n--- ADMIN MENU ---");
        System.out.println("1. View All Appointments");
        System.out.println("2. Cancel Appointment");
        System.out.println("3. Modify Appointment");
        System.out.println("4. Add Slots");
        System.out.println("5. View Calendar");
        System.out.println("6. Send Reminders To All Users");
        System.out.println("7. Back");
        System.out.print(CHOICE_PROMPT);
    }

    private static void viewAllAppointments(List<Appointment> appointments) {
        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }

        for (int i = 0; i < appointments.size(); i++) {
            Appointment appointment = appointments.get(i);
            System.out.println(
                    i + " | User: " + appointment.getUser().getUsername()
                            + " | Start: " + appointment.getStart().format(DATE_TIME_FORMAT)
                            + " | End: " + appointment.getEnd().format(DATE_TIME_FORMAT)
                            + " | Type: " + appointment.getType()
            );
        }
    }

    private static void adminCancelAppointment(
            Scanner input,
            List<TimeSlot> allSlots,
            List<Appointment> appointments,
            NotificationService notification
    ) {
        if (appointments.isEmpty()) {
            System.out.println("No appointments to cancel.");
            return;
        }

        System.out.print("Appointment index: ");
        int index = readInt(input);

        if (!isValidIndex(index, appointments.size())) {
            System.out.println(INVALID_INDEX_MESSAGE);
            return;
        }

        Appointment appointment = appointments.get(index);

        freeSlotsForAppointment(appointment, allSlots);
        appointment.cancel();
        appointments.remove(index);

        notification.sendNotification(
                appointment.getUser().getUsername() + EMAIL_DOMAIN,
                "Your appointment has been cancelled"
        );

        System.out.println("✅ Cancelled + Email Sent");
    }

    private static void adminModifyAppointment(
            Scanner input,
            List<TimeSlot> allSlots,
            List<Appointment> appointments,
            BookingService bookingService,
            NotificationService notification
    ) {
        if (appointments.isEmpty()) {
            System.out.println("No appointments to modify.");
            return;
        }

        System.out.print("Appointment index: ");
        int index = readInt(input);

        if (!isValidIndex(index, appointments.size())) {
            System.out.println(INVALID_INDEX_MESSAGE);
            return;
        }

        Appointment oldAppointment = appointments.get(index);
        Appointment newAppointment = createModifiedAppointment(input, allSlots, oldAppointment, bookingService);

        if (newAppointment == null) {
            return;
        }

        appointments.set(index, newAppointment);

        notification.sendNotification(
                newAppointment.getUser().getUsername() + EMAIL_DOMAIN,
                "Your appointment has been updated to: "
                        + newAppointment.getStart().format(DATE_TIME_FORMAT)
                        + " -> " + newAppointment.getEnd().format(DATE_TIME_FORMAT)
        );

        System.out.println("✅ Modified + Email Sent");
    }

    private static Appointment createModifiedAppointment(
            Scanner input,
            List<TimeSlot> allSlots,
            Appointment oldAppointment,
            BookingService bookingService
    ) {
        List<TimeSlot> oldSlots = getSlotsForAppointment(oldAppointment, allSlots);
        markSlots(oldSlots, false);

        printCalendar(allSlots);

        System.out.print("Start slot index: ");
        int startIndex = readInt(input);

        System.out.print("Duration in minutes (30/60/90/120): ");
        int durationMinutes = readInt(input);

        if (!isValidDuration(durationMinutes)) {
            markSlots(oldSlots, true);
            System.out.println("❌ Invalid duration. Max is 120 minutes and it must be multiple of 30.");
            return null;
        }

        List<TimeSlot> neededSlots =
                getConsecutiveAvailableSlots(allSlots, startIndex, durationMinutes / 30);

        if (neededSlots == null) {
            markSlots(oldSlots, true);
            System.out.println(NO_CONSECUTIVE_SLOTS_MESSAGE);
            return null;
        }

        Appointment newAppointment = buildAppointmentFromSlots(
                neededSlots,
                oldAppointment.getUser(),
                oldAppointment.getParticipants(),
                oldAppointment.getType()
        );

        boolean booked = bookingService.book(newAppointment);

        if (!booked) {
            markSlots(oldSlots, true);
            System.out.println("❌ Modification failed بسبب مخالفة أحد الشروط");
            return null;
        }

        markSlots(neededSlots, true);
        return newAppointment;
    }

    private static Appointment buildAppointmentFromSlots(
            List<TimeSlot> slots,
            User user,
            int participants,
            AppointmentType type
    ) {
        Appointment appointment = new Appointment(
                slots.get(0).getStart(),
                slots.get(slots.size() - 1).getEnd(),
                participants
        );

        appointment.setUser(user);
        appointment.setType(type);
        appointment.setTimeSlot(slots.get(0));

        return appointment;
    }

    private static void addSlotsByAdmin(
            Scanner input,
            Schedule schedule,
            List<TimeSlot> allSlots
    ) {
        input.nextLine();

        try {
            System.out.println("Enter start date/time (yyyy-MM-dd HH:mm): ");
            String startText = input.nextLine();

            System.out.println("Enter end date/time (yyyy-MM-dd HH:mm): ");
            String endText = input.nextLine();

            LocalDateTime start = LocalDateTime.parse(startText, DATE_TIME_FORMAT);
            LocalDateTime end = LocalDateTime.parse(endText, DATE_TIME_FORMAT);

            if (!isValidSlotRange(start, end)) {
                return;
            }

            addHalfHourSlots(schedule, allSlots, start, end);
            sortSlots(allSlots);

            System.out.println("✅ Slots added successfully.");
        } catch (Exception e) {
            System.out.println("❌ Invalid date format.");
        }
    }

    private static boolean isValidSlotRange(LocalDateTime start, LocalDateTime end) {
        if (!start.isAfter(LocalDateTime.now())) {
            System.out.println("❌ Start time must be in the future.");
            return false;
        }

        if (!end.isAfter(start)) {
            System.out.println("❌ End must be after start.");
            return false;
        }

        if (start.getMinute() % 30 != 0 || end.getMinute() % 30 != 0) {
            System.out.println("❌ Time must be on 30-minute boundaries.");
            return false;
        }

        return true;
    }

    private static void sendRemindersToAllUsers(
            List<Appointment> appointments,
            ReminderService reminderService
    ) {
        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }

        int sentCount = 0;

        for (Appointment appointment : appointments) {
            if (appointment.getEnd().isAfter(LocalDateTime.now())) {
                reminderService.sendReminder(
                        appointment.getUser().getUsername() + EMAIL_DOMAIN,
                        appointment
                );
                sentCount++;
            }
        }

        System.out.println("✅ Reminders sent to " + sentCount + " users.");
    }

    private static void handleUserLogin(
            Scanner input,
            List<User> users,
            Schedule schedule,
            List<TimeSlot> allSlots,
            List<Appointment> appointments,
            BookingService bookingService,
            ReminderService reminderService
    ) {
        System.out.println("Choose user:");
        for (int i = 0; i < users.size(); i++) {
            System.out.println(i + " - " + users.get(i).getUsername());
        }

        int index = readInt(input);

        if (!isValidIndex(index, users.size())) {
            System.out.println("❌ Invalid user index");
            return;
        }

        User user = users.get(index);

        System.out.print("Password: ");
        if (!user.getPassword().equals(input.next())) {
            System.out.println("❌ Wrong password");
            return;
        }

        handleUserMenu(input, user, schedule, allSlots, appointments, bookingService, reminderService);
    }

    private static void handleUserMenu(
            Scanner input,
            User user,
            Schedule schedule,
            List<TimeSlot> allSlots,
            List<Appointment> appointments,
            BookingService bookingService,
            ReminderService reminderService
    ) {
        while (true) {
            refreshData(schedule, allSlots, appointments);

            printUserMenu();
            int choice = readInt(input);

            if (choice == 1) {
                printCalendar(allSlots);
            } else if (choice == 2) {
                bookAppointment(input, user, allSlots, appointments, bookingService, reminderService);
            } else if (choice == 3) {
                userModifyAppointment(input, user, allSlots, appointments, bookingService);
            } else if (choice == 4) {
                userCancelAppointment(input, user, allSlots, appointments);
            } else if (choice == 5) {
                viewUserAppointments(appointments, user);
            } else if (choice == 6) {
                break;
            }
        }
    }

    private static void printUserMenu() {
        System.out.println("\n--- USER MENU ---");
        System.out.println("1. View Calendar");
        System.out.println("2. Book");
        System.out.println("3. Modify My Appointment");
        System.out.println("4. Cancel My Appointment");
        System.out.println("5. My Appointments");
        System.out.println("6. Back");
        System.out.print(CHOICE_PROMPT);
    }

    private static void bookAppointment(
            Scanner input,
            User user,
            List<TimeSlot> allSlots,
            List<Appointment> appointments,
            BookingService bookingService,
            ReminderService reminderService
    ) {
        printCalendar(allSlots);

        System.out.print("Start slot index: ");
        int startIndex = readInt(input);

        System.out.print("Duration in minutes (30/60/90/120): ");
        int durationMinutes = readInt(input);

        if (!isValidDuration(durationMinutes)) {
            System.out.println("❌ Invalid duration. Max is 120 minutes and it must be multiple of 30.");
            return;
        }

        List<TimeSlot> neededSlots =
                getConsecutiveAvailableSlots(allSlots, startIndex, durationMinutes / 30);

        if (neededSlots == null) {
            System.out.println(NO_CONSECUTIVE_SLOTS_MESSAGE);
            return;
        }

        System.out.print("Participants: ");
        int participants = readInt(input);

        AppointmentType type = chooseAppointmentType(input);
        if (type == null) {
            return;
        }

        Appointment appointment = buildAppointmentFromSlots(neededSlots, user, participants, type);

        boolean booked = bookingService.book(appointment);

        if (!booked) {
            System.out.println("❌ Booking failed بسبب مخالفة أحد الشروط");
            return;
        }

        markSlots(neededSlots, true);
        appointments.add(appointment);

        reminderService.sendReminder(user.getUsername() + EMAIL_DOMAIN, appointment);

        System.out.println("✅ Booked from "
                + appointment.getStart().format(DATE_TIME_FORMAT)
                + " to "
                + appointment.getEnd().format(DATE_TIME_FORMAT));
    }

    private static AppointmentType chooseAppointmentType(Scanner input) {
        AppointmentType[] types = AppointmentType.values();

        for (int i = 0; i < types.length; i++) {
            System.out.println(i + " - " + types[i]);
        }

        System.out.print("Type: ");
        int typeIndex = readInt(input);

        if (!isValidIndex(typeIndex, types.length)) {
            System.out.println("❌ Invalid type");
            return null;
        }

        return types[typeIndex];
    }

    private static void userModifyAppointment(
            Scanner input,
            User user,
            List<TimeSlot> allSlots,
            List<Appointment> appointments,
            BookingService bookingService
    ) {
        List<Appointment> myAppointments = getUserAppointments(appointments, user);

        if (myAppointments.isEmpty()) {
            System.out.println(NO_APPOINTMENTS_MESSAGE);
            return;
        }

        printUserAppointmentList(myAppointments);

        System.out.print("Choose your appointment index: ");
        int myIndex = readInt(input);

        if (!isValidIndex(myIndex, myAppointments.size())) {
            System.out.println(INVALID_INDEX_MESSAGE);
            return;
        }

        Appointment oldAppointment = myAppointments.get(myIndex);
        Appointment newAppointment = createModifiedAppointment(input, allSlots, oldAppointment, bookingService);

        if (newAppointment == null) {
            return;
        }

        int realIndex = appointments.indexOf(oldAppointment);
        appointments.set(realIndex, newAppointment);

        System.out.println("✅ Modified");
    }

    private static void userCancelAppointment(
            Scanner input,
            User user,
            List<TimeSlot> allSlots,
            List<Appointment> appointments
    ) {
        List<Appointment> myAppointments = getUserAppointments(appointments, user);

        if (myAppointments.isEmpty()) {
            System.out.println(NO_APPOINTMENTS_MESSAGE);
            return;
        }

        printUserAppointmentList(myAppointments);

        System.out.print("Choose your appointment index: ");
        int myIndex = readInt(input);

        if (!isValidIndex(myIndex, myAppointments.size())) {
            System.out.println(INVALID_INDEX_MESSAGE);
            return;
        }

        Appointment appointment = myAppointments.get(myIndex);

        freeSlotsForAppointment(appointment, allSlots);
        appointment.cancel();
        appointments.remove(appointment);

        System.out.println("✅ Cancelled");
    }

    private static void viewUserAppointments(List<Appointment> appointments, User user) {
        List<Appointment> myAppointments = getUserAppointments(appointments, user);

        if (myAppointments.isEmpty()) {
            System.out.println(NO_APPOINTMENTS_MESSAGE);
            return;
        }

        for (int i = 0; i < myAppointments.size(); i++) {
            Appointment appointment = myAppointments.get(i);
            System.out.println(
                    i + " | Start: " + appointment.getStart().format(DATE_TIME_FORMAT)
                            + " | End: " + appointment.getEnd().format(DATE_TIME_FORMAT)
                            + " | Type: " + appointment.getType()
            );
        }
    }

    private static void printUserAppointmentList(List<Appointment> appointments) {
        for (int i = 0; i < appointments.size(); i++) {
            Appointment appointment = appointments.get(i);
            System.out.println(
                    i + " | " + appointment.getStart().format(DATE_TIME_FORMAT)
                            + " -> " + appointment.getEnd().format(DATE_TIME_FORMAT)
                            + " | " + appointment.getType()
            );
        }
    }

    private static void refreshData(
            Schedule schedule,
            List<TimeSlot> allSlots,
            List<Appointment> appointments
    ) {
        removePastData(allSlots, appointments);
        ensureWeeklyCalendar(schedule, allSlots);
    }

    private static boolean isValidIndex(int index, int size) {
        return index >= 0 && index < size;
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

            if (!slotExists(allSlots, current, next)) {
                TimeSlot slot = new TimeSlot(current, next);
                schedule.addSlot(slot);
                allSlots.add(slot);
            }

            current = next;
        }
    }

    private static boolean slotExists(
            List<TimeSlot> allSlots,
            LocalDateTime start,
            LocalDateTime end
    ) {
        for (TimeSlot slot : allSlots) {
            if (slot.getStart().equals(start) && slot.getEnd().equals(end)) {
                return true;
            }
        }
        return false;
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