package com.romanimazione.controller.graphic;

import com.romanimazione.bean.PartyBean;
import com.romanimazione.controller.application.PartyController;
import com.romanimazione.exception.DAOException;
import com.romanimazione.exception.InvalidPartyException;
import com.romanimazione.view.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class JavaFXPartyController {

    @FXML private TextField nameField;
    @FXML private ComboBox<String> typeBox;
    @FXML private TextField addressField;
    @FXML private DatePicker datePicker;
    @FXML private TextField clientNameField;
    @FXML private TextField clientPhoneField;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
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
            
            Integer children = parseInteger(childrenCountField.getText(), "Children count", false);
            int animators = parseInteger(animatorsRequiredField.getText(), "Animators required", true);
            double cost = parseDouble(costField.getText(), "Cost");
            
            String desc = descriptionArea.getText();

            PartyBean bean = new PartyBean();
            bean.setName(name);
            bean.setType(type);
            bean.setAddress(address);
            bean.setDate(date);
            bean.setClientName(clientName);
            bean.setClientPhone(clientPhone);
            bean.setStartTime(start);
            bean.setEndTime(end);
            bean.setChildrenCount(children);
            bean.setAnimatorsRequired(animators);
            bean.setDescription(desc);
            bean.setCost(cost);
            
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

    private Integer parseInteger(String text, String fieldName, boolean required) throws InvalidPartyException {
        if (text == null || text.trim().isEmpty()) {
            if (required) throw new InvalidPartyException(fieldName + " is required.");
            return null;
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidPartyException(fieldName + " must be a number.");
        }
    }

    private double parseDouble(String text, String fieldName) throws InvalidPartyException {
        if (text == null || text.trim().isEmpty()) return 0.0;
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidPartyException(fieldName + " must be a valid number (e.g. 100.50).");
        }
    }

    @FXML
    private void handleCancel() throws IOException {
        MainApp.setRoot("admin_dashboard");
    }
}
