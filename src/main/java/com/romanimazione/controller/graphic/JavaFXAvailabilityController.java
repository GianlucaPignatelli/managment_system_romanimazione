package com.romanimazione.controller.graphic;

import com.romanimazione.bean.AvailabilityBean;
import com.romanimazione.controller.application.AvailabilityController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class JavaFXAvailabilityController implements Initializable {

    @FXML private DatePicker datePicker;
    @FXML private CheckBox fullDayParams;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private Label errorLabel;

    @FXML private TableView<AvailabilityBean> availabilityTable;
    @FXML private TableColumn<AvailabilityBean, String> dateColumn;
    @FXML private TableColumn<AvailabilityBean, String> startColumn;
    @FXML private TableColumn<AvailabilityBean, String> endColumn;
    @FXML private TableColumn<AvailabilityBean, String> fullDayColumn;

    private final AvailabilityController appController;

    private String currentUsername = "testUser"; 

    public JavaFXAvailabilityController() {
        this.appController = new AvailabilityController();
    }

    public void setUsername(String username) {
        this.currentUsername = username;
        loadData();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load User from Session
        if (com.romanimazione.bean.SessionBean.getInstance().getCurrentUser() != null) {
            this.currentUsername = com.romanimazione.bean.SessionBean.getInstance().getCurrentUser().getUsername();
        }

        dateColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate()));
        startColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStartTime()));
        endColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEndTime()));
        fullDayColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getIsFullDay()));
        
        // Listener for selection to populate fields for update
        availabilityTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                if (newSelection.getDate() != null) datePicker.setValue(LocalDate.parse(newSelection.getDate()));
                fullDayParams.setSelected(Boolean.parseBoolean(newSelection.getIsFullDay()));
                handleFullDayToggle(); // Refresh fields state
                if (!Boolean.parseBoolean(newSelection.getIsFullDay())) {
                    startTimeField.setText(newSelection.getStartTime());
                    endTimeField.setText(newSelection.getEndTime());
                }
            }
        });
        
        loadData();
    }

    // ... loadData ...

    @FXML
    private void handleUpdate() {
        AvailabilityBean selected = availabilityTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Select an item to update.");
            return;
        }
        
        try {
            // Create a temp bean to validate and grab new values
            AvailabilityBean bean = new AvailabilityBean();
            bean.setId(selected.getId());
            bean.setUsername(currentUsername);
            bean.setDate(datePicker.getValue().toString());
            bean.setIsFullDay(String.valueOf(fullDayParams.isSelected()));

            if (!Boolean.parseBoolean(bean.getIsFullDay())) {
                if (startTimeField.getText().isEmpty() || endTimeField.getText().isEmpty()) throw new IllegalArgumentException("Time fields empty");
                bean.setStartTime(startTimeField.getText());
                bean.setEndTime(endTimeField.getText());
            }

            appController.updateAvailability(bean);
            loadData();
            errorLabel.setText("Updated successfully.");
            errorLabel.setStyle(STYLE_SUCCESS);
        } catch (Exception e) {
             errorLabel.setText(e.getMessage());
             errorLabel.setStyle(STYLE_ERROR);
        }
    }

    @FXML
    private void handleDelete() {
        AvailabilityBean selected = availabilityTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Select an item to delete.");
            return;
        }
        try {
            appController.deleteAvailability(selected);
            loadData();
            errorLabel.setText("Deleted successfully.");
            errorLabel.setStyle(STYLE_SUCCESS);
        } catch (Exception e) {
             errorLabel.setText(e.getMessage());
             errorLabel.setStyle(STYLE_ERROR);
        }
    }

    private void loadData() {
        try {
            List<AvailabilityBean> list = appController.getAvailabilities(currentUsername);
            availabilityTable.setItems(FXCollections.observableArrayList(list));
        } catch (Exception e) {
            errorLabel.setText("Error loading data: " + e.getMessage());
        }
    }

    @FXML
    private void handleFullDayToggle() {
        boolean selected = fullDayParams.isSelected();
        startTimeField.setDisable(selected);
        endTimeField.setDisable(selected);
        if (selected) {
            startTimeField.clear();
            endTimeField.clear();
        }
    }

    private static final String STYLE_ERROR = "-fx-text-fill: red;";
    private static final String STYLE_SUCCESS = "-fx-text-fill: green;";

    @FXML
    private void handleAdd() {
        errorLabel.setText("");
        try {
            AvailabilityBean bean = new AvailabilityBean();
            bean.setUsername(currentUsername);
            bean.setDate(datePicker.getValue().toString());
            bean.setIsFullDay(String.valueOf(fullDayParams.isSelected()));

            if (!Boolean.parseBoolean(bean.getIsFullDay())) {
                parseTimeFields(bean);
            }

            appController.addAvailability(bean);
            
            // Refresh
            loadData();
            // Clear inputs
            datePicker.setValue(null);
            fullDayParams.setSelected(false);
            startTimeField.setDisable(false);
            endTimeField.setDisable(false);
            startTimeField.clear();
            endTimeField.clear();

        } catch (Exception e) {
             errorLabel.setText(e.getMessage());
             errorLabel.setStyle(STYLE_ERROR);
        }
    }
    
    private void parseTimeFields(AvailabilityBean bean) {
        if (startTimeField.getText().isEmpty() || endTimeField.getText().isEmpty()) {
            throw new IllegalArgumentException("Time fields cannot be empty for partial day.");
        }
        try {
            LocalTime.parse(startTimeField.getText()); // validation
            LocalTime.parse(endTimeField.getText()); // validation
            bean.setStartTime(startTimeField.getText());
            bean.setEndTime(endTimeField.getText());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid Time format. Use HH:mm (e.g. 14:30)");
        }
    }

    @FXML
    private void handleBack() throws IOException {
        // Return to Dashboard
        new com.romanimazione.view.fx.MainFXView().showAnimatorDashboard();
    }
}
