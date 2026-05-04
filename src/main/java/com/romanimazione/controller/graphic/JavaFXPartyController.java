package com.romanimazione.controller.graphic;

import com.romanimazione.bean.PartyBean;
import com.romanimazione.controller.application.Observer;
import com.romanimazione.controller.application.PartyController;
import com.romanimazione.exception.DAOException;
import com.romanimazione.exception.InvalidPartyException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import javafx.fxml.Initializable;
import java.net.URL;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class JavaFXPartyController implements Observer, Initializable {

    @FXML private TextField nameField;
    @FXML private ComboBox<String> typeBox;
    @FXML private ComboBox<String> equipmentCategoryBox;
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
        // Attach this view to the Logic Controller
        this.partyController.attach(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeBox.getItems().addAll(partyController.getPartyTypes());
        if (equipmentCategoryBox != null) {
            equipmentCategoryBox.getItems().addAll(java.util.Arrays.asList(
                "borsone giochi", "carretto", "borsa magia", "cassa audio", "gonfiabile"
            ));
        }
    }

    @FXML
    private void handleSave() {
        errorLabel.setVisible(false);
        try {
            // Text to Object Conversion (Form Validation)
            String name = nameField.getText();
            String type = typeBox.getValue();
            String address = addressField.getText();
            LocalDate date = datePicker.getValue();
            String clientName = clientNameField.getText();
            String clientPhone = clientPhoneField.getText();
            String start = startTimeField.getText();
            String end = endTimeField.getText();
            
            String children = childrenCountField.getText();
            String animators = animatorsRequiredField.getText();
            String cost = costField.getText();
            
            String desc = descriptionArea.getText();

            PartyBean bean = new PartyBean();
            bean.setName(name);
            bean.setType(type);
            bean.setAddress(address);
            bean.setDate(date != null ? date.toString() : null);
            bean.setClientName(clientName);
            bean.setClientPhone(clientPhone);
            bean.setStartTime(start);
            bean.setEndTime(end);
            bean.setChildrenCount(children);
            bean.setAnimatorsRequired(animators);
            bean.setDescription(desc);
            bean.setCost(cost);
            bean.setEquipmentCategory(equipmentCategoryBox.getValue() != null ? equipmentCategoryBox.getValue() : "");
            
            // Delegate Business Logic to Application Controller
            partyController.createParty(bean);
            
            // Navigation is now handled in update() via Observer Pattern
            
        } catch (InvalidPartyException | DAOException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
        } catch (Exception e) {
             java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE, "System error", e);
             errorLabel.setText("System error: " + e.getMessage());
             errorLabel.setVisible(true);
        }
    }
    
    @Override
    public void update(String message) {
        // Observer Logic: React to changes in the Subject
        if ("Party Created Successfully".equals(message)) {
            try {
                new com.romanimazione.view.fx.MainFXView().showAdminDashboard();
            } catch (IOException e) {
                java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE, "Nav error", e);
            }
        }
    }
    


    @FXML
    private void handleCancel() throws IOException {
        new com.romanimazione.view.fx.MainFXView().showAdminDashboard();
    }
}
