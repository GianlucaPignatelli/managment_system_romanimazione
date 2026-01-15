package com.romanimazione;

import com.romanimazione.bean.CredentialsBean;
import com.romanimazione.bean.UserBean;
import com.romanimazione.controller.application.LoginController;
import com.romanimazione.dao.UserDAODemo;
import com.romanimazione.exception.DAOException;
import com.romanimazione.exception.UserNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @Test
    void testLoginSuccess() {
        LoginController controller = new LoginController();
        // Inject Demo DAO for consistent testing
        controller.setUserDAO(new UserDAODemo());
        
        CredentialsBean creds = new CredentialsBean("demo", "pass");
        
        Assertions.assertDoesNotThrow(() -> {
            UserBean user = controller.login(creds);
            Assertions.assertEquals("demo", user.getUsername());
        });
    }

    @Test
    void testLoginFailure() {
        LoginController controller = new LoginController();
        controller.setUserDAO(new UserDAODemo());

        CredentialsBean creds = new CredentialsBean("wrong", "wrong");

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            controller.login(creds);
        });
    }
}
