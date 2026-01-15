package com.romanimazione.controller.graphic;

import com.romanimazione.bean.UserBean;
import com.romanimazione.controller.application.Observer;
import com.romanimazione.controller.application.RegistrationController;
import com.romanimazione.view.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class JavaFXRegistrationController implements Observer {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> roleBox;
    @FXML private Label infoLabel;

    private RegistrationController registrationController;

    public JavaFXRegistrationController() {
        this.registrationController = new RegistrationController();
        this.registrationController.attach(this);
    }

    @FXML
    private void handleRegister() {
        UserBean user = new UserBean();
        user.setUsername(usernameField.getText());
        user.setPassword(passwordField.getText());
        user.setNome(nameField.getText());
        user.setCognome(surnameField.getText());
        user.setEmail(emailField.getText());
        user.setRole(roleBox.getValue());

        if (user.getRole() == null) {
            showError("Please select a role.");
            return;
        }

        try {
            registrationController.register(user);
            infoLabel.setText("Registration Successful! Returning to login...");
            infoLabel.setStyle("-fx-text-fill: green;");
            // Optional: delay then switch
        } catch (com.romanimazione.exception.DuplicateUserException | IllegalArgumentException | com.romanimazione.exception.DAOException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Unexpected Error: " + e.getMessage());
        }
    }

    private void showError(String message) {
        infoLabel.setText(message);
        infoLabel.setStyle("-fx-text-fill: red;");
    }


    @FXML
    private void handleBack() throws IOException {
        MainApp.setRoot("home");
    }

    @Override
    public void update(String message) {
        System.out.println("JavaFX Register Update: " + message);
    }
}
