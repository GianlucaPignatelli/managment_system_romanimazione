package com.romanimazione.controller.graphic;

import com.romanimazione.bean.SessionBean;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class JavaFXAdminDashboardController {

    @FXML private javafx.scene.control.Button btnManageUsers;
    @FXML private Label welcomeLabel;

    @FXML
    public void initialize() {
        if (welcomeLabel != null) {
            String username = "Admin";
            com.romanimazione.bean.UserBean user = com.romanimazione.bean.SessionBean.getInstance().getCurrentUser();
            
            if (user != null) {
                username = user.getUsername();
                System.out.println("AdminDashboard: Current user: " + username + ", SuperAdmin: " + user.getIsSuperAdmin());
                
                if (Boolean.parseBoolean(user.getIsSuperAdmin()) || "admin".equalsIgnoreCase(user.getUsername())) {
                    System.out.println("AdminDashboard: Enabling Manage Users button");
                    btnManageUsers.setVisible(true);
                    btnManageUsers.setManaged(true);
                    btnChangeMasterCode.setVisible(true);
                    btnChangeMasterCode.setManaged(true);
                } else {
                    System.out.println("AdminDashboard: Manage Users button remains hidden");
                }
            } else {
                 System.out.println("AdminDashboard: User is null in SessionBean");
            }
            welcomeLabel.setText("Welcome, " + username);
        } else {
            System.out.println("AdminDashboard: welcomeLabel is null!");
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

    @FXML private javafx.scene.control.Button btnChangeMasterCode;

    @FXML
    private void handleManageUsers() throws IOException {
         new com.romanimazione.view.fx.MainFXView().showManageUsers();
    }

    @FXML
    private void handleChangeMasterCode() {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Change Master Code");
        dialog.setHeaderText("Update the Security Master Code");
        dialog.setContentText("Enter new code (min 64 chars):");

        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newCode = result.get();
            try {
                com.romanimazione.bean.SecurityManager.getInstance().setMasterCode(newCode);
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Master Code updated successfully.");
                alert.showAndWait();
            } catch (IllegalArgumentException e) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Code");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }
}
