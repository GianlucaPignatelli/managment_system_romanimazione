package com.romanimazione.controller.graphic;

import com.romanimazione.bean.SessionBean;
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
        com.romanimazione.bean.SessionBean.getInstance().setCurrentUser(null);
        new com.romanimazione.view.fx.MainFXView().showHome();
    }

    @FXML
    private void handleCreateParty() throws IOException {
        new com.romanimazione.view.fx.PartyFXView().render();
    }
    
    @FXML
    private void handleListParties() throws IOException {
        new com.romanimazione.view.fx.PartyFXView().showList();
    }
}
