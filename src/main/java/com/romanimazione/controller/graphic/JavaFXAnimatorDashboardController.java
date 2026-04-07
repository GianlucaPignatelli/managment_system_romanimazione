package com.romanimazione.controller.graphic;

import com.romanimazione.bean.SessionBean;
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
        new com.romanimazione.view.fx.AvailabilityFXView().showAvailabilityManagement();
    }
    
    @FXML
    private void goToJobOffers() throws IOException {
        new com.romanimazione.view.fx.MainFXView().showJobOffers();
    }

    @FXML
    private void goToAcceptedJobs() throws IOException {
        new com.romanimazione.view.fx.MainFXView().showAcceptedJobs();
    }

    @FXML
    private void handleLogout() throws IOException {
        com.romanimazione.bean.SessionBean.getInstance().setCurrentUser(null);
        new com.romanimazione.view.fx.MainFXView().showHome();
    }
}
