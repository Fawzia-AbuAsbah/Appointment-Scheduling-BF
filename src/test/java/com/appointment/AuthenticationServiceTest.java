package com.appointment;

import com.appointment.repo.AdminRepo;
import com.appointment.service.AuthenticationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationServiceTest {

    @Test
    void loginSuccess() {

        AuthenticationService service =
                new AuthenticationService(new AdminRepo());

        assertTrue(service.login("admin", "1234"));
    }

    @Test
    void loginFail() {

        AuthenticationService service =
                new AuthenticationService(new AdminRepo());

        assertFalse(service.login("admin", "wrong"));
    }

    @Test
    void logoutTest() {

        AuthenticationService service =
                new AuthenticationService(new AdminRepo());

        service.login("admin", "1234");
        service.logout();

        assertFalse(service.isLoggedIn());
    }
}