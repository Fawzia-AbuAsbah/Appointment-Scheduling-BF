package com.appointment.service;

import com.appointment.domain.Administrator;
import com.appointment.repo.AdminRepo;

import java.util.Optional;

/**
 * Handles login and logout logic.
 */
public class AuthenticationService {

    private AdminRepo repository;
    private Administrator loggedAdmin;

    public AuthenticationService(AdminRepo repository) {
        this.repository = repository;
    }

    public boolean login(String username, String password) {

        Optional<Administrator> admin = repository.findByUsername(username);

        if (admin.isPresent() && admin.get().getPassword().equals(password)) {
            loggedAdmin = admin.get();
            return true;
        }

        return false;
    }

    public void logout() {
        loggedAdmin = null;
    }

    public boolean isLoggedIn() {
        return loggedAdmin != null;
    }
}