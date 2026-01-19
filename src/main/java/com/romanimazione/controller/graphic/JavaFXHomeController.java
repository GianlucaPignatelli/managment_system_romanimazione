package com.romanimazione.controller.graphic;

import com.romanimazione.view.MainApp;
import javafx.fxml.FXML;

import java.io.IOException;

public class JavaFXHomeController {

    @FXML
    private void goToLogin() throws IOException {
        MainApp.setRoot("login");
    }

    @FXML
    private void goToRegister() throws IOException {
        MainApp.setRoot("register");
    }


}
