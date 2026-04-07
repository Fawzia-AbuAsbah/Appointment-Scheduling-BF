# Appointment-Scheduling-BF


Team Members :
** Balsam Adnan Mashaqi (Reg. Num:12217816)
** Fawzia Yousef AbuAsbah(Reg. Num:12219765)

#Project Description

This project is an Appointment Scheduling System developed using Java.
It allows users and administrators to manage appointments efficiently through a simple CLI interface.

#The system supports:

*Booking appointments
*Modifying appointments
*Cancelling appointments
*Viewing available time slots
*Sending email notifications

# Features
  User
View available time slots
Book an appointment
Modify appointment
Cancel appointment
View personal appointments

 Admin
View all appointments
Modify any appointment
Cancel any appointment
Send notifications to users


#Business Rules
Maximum appointment duration is 2 hours
Maximum participants is 5
GROUP appointment requires at least 2 participants
INDIVIDUAL appointment requires exactly 1 participant
URGENT appointment must be in the future
Cannot modify or cancel past appointments

# System Architecture

The project follows a layered architecture:

domain → core entities (Appointment, User, TimeSlot)
service → business logic
repo → data storage
strategy → rule validation using Strategy Pattern
notification → email services
presentation → CLI interface (Main)

#Testing

The system includes unit tests using JUnit 5.

Tested components:

Appointment validation
BookingService
RuleEngine and strategies
Appointment management

Mocking is used in testing notification services.

# Test Coverage

The project achieves more than 80% test coverage.

# How to Run
Run the application
Open the project in your IDE (Eclipse / IntelliJ)
Run:
Main.java
Run tests

If using Maven:
mvn clean test

#Notifications

The system uses an email service to send:

Booking confirmations
Cancellation notifications
Reminder messages

#Submission Notes
This project is submitted as a group work.
All members are listed above.
The full source code is available in this repository.