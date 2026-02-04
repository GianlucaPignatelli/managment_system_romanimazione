package com.romanimazione.controller.application;

import com.romanimazione.bean.CredentialsBean;
import com.romanimazione.bean.UserBean;
import com.romanimazione.dao.DAOFactory;
import com.romanimazione.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

    private LoginController loginController;

    @BeforeEach
    void setUp() throws Exception {
        // Use DEMO (Memory) DAO for isolation
        DAOFactory.setFactoryType(DAOFactory.DEMO);
        loginController = new LoginController();
        
        // Seed the "admin" user for logic tests
        com.romanimazione.entity.User admin = new com.romanimazione.entity.Amministratore();
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setRole("AMMINISTRATORE");
        // Ensure it doesn't exist (if static persists)
        DAOFactory.getDAOFactory().getUserDAO().deleteUser("admin"); 
        DAOFactory.getDAOFactory().getUserDAO().saveUser(admin);
    }

    @Test
    void testLoginSuccess() throws Exception {
        // Assuming DemoDAO seeds "admin"/"admin" or specific users.
        // We know from DemoDAO implementation (standard mock) it likely has admin/admin
        CredentialsBean creds = new CredentialsBean();
        creds.setUsername("admin");
        creds.setPassword("admin");

        UserBean user = loginController.login(creds);

        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        assertEquals("AMMINISTRATORE", user.getRole());
    }

    @Test
    void testLoginFailure_WrongPassword() {
        CredentialsBean creds = new CredentialsBean();
        creds.setUsername("admin");
        creds.setPassword("wrongpass");

        assertThrows(UserNotFoundException.class, () -> {
            loginController.login(creds);
        });
    }

    @Test
    void testLoginFailure_UserNotFound() {
        CredentialsBean creds = new CredentialsBean();
        creds.setUsername("nonexistent");
        creds.setPassword("whatever");

        assertThrows(UserNotFoundException.class, () -> {
            loginController.login(creds);
        });
    }
}
