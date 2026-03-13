package com.appointment.repo;

import com.appointment.domain.Administrator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * In-memory admin repository.
 */
public class AdminRepo {

    private List<Administrator> admins = new ArrayList<>();

    public AdminRepo() {
        admins.add(new Administrator("admin", "1234"));
    }

    public Optional<Administrator> findByUsername(String username) {

        return admins.stream()
                .filter(a -> a.getUsername().equals(username))
                .findFirst();
    }
}