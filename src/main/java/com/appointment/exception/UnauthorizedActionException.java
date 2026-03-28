package com.appointment.exception;

/**
 * Thrown when user tries to perform unauthorized action.
 */
public class UnauthorizedActionException extends RuntimeException {

    public UnauthorizedActionException(String message) {
        super(message);
    }
}