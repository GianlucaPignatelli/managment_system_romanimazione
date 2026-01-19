package com.romanimazione.controller.graphic;

import com.romanimazione.bean.SessionBean;
import com.romanimazione.view.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class JavaFXAnimatorDashboardController {

    @FXML private Label welcomeLabel;

    @FXML
    public void initialize() {
        if (SessionBean.getInstance().getCurrentUser() != null) {
            welcomeLabel.setText("Welcome, " + SessionBean.getInstance().getCurrentUser().getNome());
        }
    }

    @FXML
    private void goToAvailability() throws IOException {
        MainApp.setRoot("availability");
    }

    @FXML
    private void handleLogout() throws IOException {
        SessionBean.getInstance().setCurrentUser(null);
        MainApp.setRoot("home");
    }
}
