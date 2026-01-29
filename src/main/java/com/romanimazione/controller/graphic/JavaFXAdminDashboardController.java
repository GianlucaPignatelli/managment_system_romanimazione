package com.romanimazione.controller.graphic;

import com.romanimazione.bean.SessionBean;
import com.romanimazione.view.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class JavaFXAdminDashboardController {

    @FXML private Label welcomeLabel;

    @FXML
    public void initialize() {
        if (SessionBean.getInstance().getCurrentUser() != null) {
            welcomeLabel.setText("Welcome, " + SessionBean.getInstance().getCurrentUser().getNome());
        }
    }

    @FXML
    private void handleLogout() throws IOException {
        SessionBean.getInstance().setCurrentUser(null);
        MainApp.setRoot("home");
    }

    @FXML
    private void handleCreateParty() throws IOException {
        MainApp.setRoot("party_form");
    }

    @FXML
    private void handleListParties() throws IOException {
        MainApp.setRoot("party_list");
    }
}
