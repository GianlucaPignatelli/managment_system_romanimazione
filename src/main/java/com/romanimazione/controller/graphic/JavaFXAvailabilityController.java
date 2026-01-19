package com.romanimazione.controller.graphic;

import com.romanimazione.bean.AvailabilityBean;
import com.romanimazione.controller.application.AvailabilityController;
import com.romanimazione.view.MainApp;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class JavaFXAvailabilityController {

    @FXML private DatePicker datePicker;
    @FXML private CheckBox fullDayParams;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private Label errorLabel;

    @FXML private TableView<AvailabilityBean> availabilityTable;
    @FXML private TableColumn<AvailabilityBean, LocalDate> dateColumn;
    @FXML private TableColumn<AvailabilityBean, LocalTime> startColumn;
    @FXML private TableColumn<AvailabilityBean, LocalTime> endColumn;
    @FXML private TableColumn<AvailabilityBean, Boolean> fullDayColumn;

    private AvailabilityController appController;

    // In a real app, this should be taken from the Session/Context
    private String currentUsername = "testUser"; 

    public JavaFXAvailabilityController() {
        this.appController = new AvailabilityController();
    }
    
    // Setter meant to be called when navigating to this view
    public void setUsername(String username) {
        this.currentUsername = username;
        loadData();
    }

    @FXML
    public void initialize() {
        // Load User from Session
        if (com.romanimazione.bean.SessionBean.getInstance().getCurrentUser() != null) {
            this.currentUsername = com.romanimazione.bean.SessionBean.getInstance().getCurrentUser().getUsername();
        }

        dateColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getDate()));
        startColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getStartTime()));
        endColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getEndTime()));
        fullDayColumn.setCellValueFactory(cell -> new SimpleBooleanProperty(cell.getValue().isFullDay()));
        
        // Listener for selection to populate fields for update
        availabilityTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                datePicker.setValue(newSelection.getDate());
                fullDayParams.setSelected(newSelection.isFullDay());
                handleFullDayToggle(); // Refresh fields state
                if (!newSelection.isFullDay()) {
                    startTimeField.setText(newSelection.getStartTime().toString());
                    endTimeField.setText(newSelection.getEndTime().toString());
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
            bean.setId(selected.getId()); // IMPORTANT: Keep the ID
            bean.setUsername(currentUsername);
            bean.setDate(datePicker.getValue());
            bean.setFullDay(fullDayParams.isSelected());

            if (!bean.isFullDay()) {
                if (startTimeField.getText().isEmpty() || endTimeField.getText().isEmpty()) throw new IllegalArgumentException("Time fields empty");
                bean.setStartTime(LocalTime.parse(startTimeField.getText()));
                bean.setEndTime(LocalTime.parse(endTimeField.getText()));
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
            bean.setDate(datePicker.getValue());
            bean.setFullDay(fullDayParams.isSelected());

            if (!bean.isFullDay()) {
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
            bean.setStartTime(LocalTime.parse(startTimeField.getText())); // expects HH:mm
            bean.setEndTime(LocalTime.parse(endTimeField.getText()));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid Time format. Use HH:mm (e.g. 14:30)");
        }
    }

    @FXML
    private void handleBack() throws IOException {
        // Return to Dashboard
        MainApp.setRoot("animator_dashboard");
    }
}
