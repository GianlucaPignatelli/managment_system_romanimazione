package com.romanimazione.view.fx;

import com.romanimazione.view.MainApp;
import java.io.IOException;

/**
 * Boundary class for Authentication views.
 */
public class LoginFXView {

    public void showLogin() throws IOException {
        MainApp.setRoot("login");
    }

    public void showRegister() throws IOException {
        MainApp.setRoot("register");
    }
}
