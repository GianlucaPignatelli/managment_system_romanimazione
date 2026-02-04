package com.romanimazione.controller.application;

import com.romanimazione.bean.UserBean;
import com.romanimazione.dao.DAOFactory;
import com.romanimazione.exception.DuplicateUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationControllerTest {

    private RegistrationController registrationController;

    @BeforeEach
    void setUp() {
        DAOFactory.setFactoryType(DAOFactory.DEMO);
        registrationController = new RegistrationController();
        // Since DemoDAO usage is shared static in memory usually, be careful. 
        // But typically new DemoDAOFactory() creates new lists if implemented that way.
        // If DemoDAO is static singleton, tests might interfere. 
        // Assuming typical "New Instance" behavior for Factory or separate test runs.
    }

    @Test
    void testRegistrationSuccess() throws Exception {
        UserBean newUser = new UserBean();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setEmail("test@gmail.com");
        newUser.setRole("ANIMATORE");
        newUser.setNome("Test");
        newUser.setCognome("User");

        assertDoesNotThrow(() -> registrationController.register(newUser));
    }

    @Test
    void testRegistrationFailure_DuplicateUser() {
        // Assuming 'admin' exists in Demo
        UserBean duplicate = new UserBean();
        duplicate.setUsername("admin"); 
        duplicate.setPassword("pass");
        duplicate.setEmail("admin@gmail.com");

        assertThrows(DuplicateUserException.class, () -> {
            registrationController.register(duplicate);
        });
    }

    @Test
    void testRegistrationFailure_InvalidEmail() {
        UserBean invalidEmailUser = new UserBean();
        invalidEmailUser.setUsername("bademail");
        invalidEmailUser.setPassword("pass");
        invalidEmailUser.setEmail("test@yahoo.com"); // Only gmail allowed
        invalidEmailUser.setRole("ANIMATORE");

        assertThrows(IllegalArgumentException.class, () -> {
            registrationController.register(invalidEmailUser);
        });
    }
}
