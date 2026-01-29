package com.romanimazione.controller.graphic;

import com.romanimazione.bean.PartyBean;
import com.romanimazione.controller.application.PartyController;
import com.romanimazione.exception.InvalidPartyException;
import com.romanimazione.exception.DAOException;
import com.romanimazione.view.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class JavaFXPartyController {

    @FXML private TextField nameField; // Event Name
    @FXML private ComboBox<String> typeBox;
    @FXML private TextField addressField;
    @FXML private DatePicker datePicker;
    
    @FXML private TextField clientNameField;
    @FXML private TextField clientPhoneField;
    @FXML private TextField startTimeField; // HH:mm
    @FXML private TextField endTimeField;   // HH:mm
    @FXML private TextField childrenCountField;
    @FXML private TextField animatorsRequiredField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField costField;

    @FXML private Label errorLabel;

    private final PartyController partyController;

    public JavaFXPartyController() {
        this.partyController = new PartyController();
    }

    @FXML
    public void initialize() {
        typeBox.getItems().addAll(partyController.getPartyTypes());
    }

    @FXML
    private void handleSave() {
        errorLabel.setVisible(false);
        try {
            // Text to Object Conversion
            String name = nameField.getText();
            String type = typeBox.getValue();
            String address = addressField.getText();
            LocalDate date = datePicker.getValue();
            
            String clientName = clientNameField.getText();
            String clientPhone = clientPhoneField.getText();
            
            LocalTime start = parseTime(startTimeField.getText(), "Start Time");
            LocalTime end = parseTime(endTimeField.getText(), "End Time");
            
            Integer children = null;
            if (childrenCountField.getText() != null && !childrenCountField.getText().trim().isEmpty()) {
                try {
                    children = Integer.parseInt(childrenCountField.getText().trim());
                } catch (NumberFormatException e) {
                    throw new InvalidPartyException("Children count must be a number.");
                }
            }
            
            int animators = 0;
            try {
                animators = Integer.parseInt(animatorsRequiredField.getText().trim());
            } catch (NumberFormatException e) {
                throw new InvalidPartyException("Animators required must be a number.");
            }
            
            String desc = descriptionArea.getText();
            
            double cost = 0.0;
            try {
                cost = Double.parseDouble(costField.getText().trim());
            } catch (NumberFormatException e) {
                throw new InvalidPartyException("Cost must be a valid number (e.g. 100.50).");
            }

            PartyBean bean = new PartyBean(name, type, address, date, clientName, clientPhone, start, end, children, animators, desc, cost);
            
            partyController.createParty(bean);
            
            MainApp.setRoot("admin_dashboard");
            
        } catch (InvalidPartyException | DAOException | IOException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
        } catch (Exception e) {
             e.printStackTrace();
             errorLabel.setText("System error: " + e.getMessage());
             errorLabel.setVisible(true);
        }
    }
    
    private LocalTime parseTime(String text, String fieldName) throws InvalidPartyException {
        if (text == null || text.trim().isEmpty()) return null;
        try {
            return LocalTime.parse(text);
        } catch (DateTimeParseException e) {
            throw new InvalidPartyException(fieldName + " Invalid. Use HH:mm format (e.g. 14:30).");
        }
    }

    @FXML
    private void handleCancel() throws IOException {
        MainApp.setRoot("admin_dashboard");
    }
}
