package com.romanimazione.controller.graphic;

import com.romanimazione.bean.CredentialsBean;
import com.romanimazione.controller.application.LoginController;
import com.romanimazione.controller.application.Observer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class JavaFXLoginController implements Observer {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    private LoginController loginController;

    public JavaFXLoginController() {
        this.loginController = new LoginController();
        this.loginController.attach(this);
    }

    private static final String ERROR_STYLE = "-fx-text-fill: red;";

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        CredentialsBean creds = new CredentialsBean(username, password);
        try {
            com.romanimazione.bean.UserBean user = loginController.login(creds);
            com.romanimazione.bean.SessionBean.getInstance().setCurrentUser(user);
            
            errorLabel.setText("Login Successful! Welcome " + user.getNome());
            errorLabel.setStyle("-fx-text-fill: green;");
            
            // Navigate based on role
            String role = user.getRole(); // Assuming "ANIMATORE" or "AMMINISTRATORE"
            if ("ANIMATORE".equalsIgnoreCase(role)) {
                com.romanimazione.view.MainApp.setRoot("animator_dashboard");
            } else if ("AMMINISTRATORE".equalsIgnoreCase(role)) {
                com.romanimazione.view.MainApp.setRoot("admin_dashboard");
            } else {
                errorLabel.setText("Unknown Role: " + role);
                errorLabel.setStyle(ERROR_STYLE);
            }
        } catch (com.romanimazione.exception.UserNotFoundException | com.romanimazione.exception.DAOException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setStyle(ERROR_STYLE);
        } catch (Exception e) {
            errorLabel.setText("Unexpected: " + e.getMessage());
            errorLabel.setStyle(ERROR_STYLE);
        }
    }

    @FXML
    private void goHome() throws java.io.IOException {
        com.romanimazione.view.MainApp.setRoot("home");
    }

    @Override
    public void update(String message) {
        // Platform.runLater if not on FX thread, but here we likely are if called from controller logic synchronously
        System.out.println("JavaFX received update: " + message);
    }
}
