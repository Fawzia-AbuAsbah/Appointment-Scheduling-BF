package com.appointment.domain;

/**
 * Represents a system user.
 * @author Team
 * @version 1.0
 */
public class User {

    protected String username;
    protected String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}