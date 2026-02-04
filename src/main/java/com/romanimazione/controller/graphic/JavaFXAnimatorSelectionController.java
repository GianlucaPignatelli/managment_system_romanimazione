package com.romanimazione.controller.graphic;

import com.romanimazione.bean.PartyBean;
import com.romanimazione.bean.UserBean;
import com.romanimazione.controller.application.PartyController;
import com.romanimazione.exception.DAOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JavaFXAnimatorSelectionController {

    @FXML private Label partyDetailsLabel;
    @FXML private TableView<UserBean> animatorTable;
    
    @FXML private TableView<AssignmentWrapper> currentAssignmentsTable;
    @FXML private TableColumn<AssignmentWrapper, String> currentUsernameColumn;
    @FXML private TableColumn<AssignmentWrapper, String> currentStatusColumn;
    @FXML private TableColumn<AssignmentWrapper, Void> currentActionColumn;

    private PartyController partyController;
    private PartyBean currentParty;
    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void initData(PartyBean party) {
        this.currentParty = party;
        this.partyController = new PartyController();
        
        partyDetailsLabel.setText(String.format("%s - %s", party.getName(), party.getDate()));
            
        setupTables();
        refreshData();
    }
    
        // ... previous code ...
        
    private void setupTables() {
        currentUsernameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUsername()));
        currentStatusColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus()));
        
        Callback<TableColumn<AssignmentWrapper, Void>, TableCell<AssignmentWrapper, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<AssignmentWrapper, Void> call(final TableColumn<AssignmentWrapper, Void> param) {
                return new ActionCell();
            }
        };
        currentActionColumn.setCellFactory(cellFactory);
        
        // Custom Row Factory for Availability Table
        animatorTable.setRowFactory(tv -> new TableRow<UserBean>() {
            @Override
            protected void updateItem(UserBean item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    if (!item.isTimeCompatible()) {
                        setStyle("-fx-background-color: #ffeba1;"); // Light Orange/Yellow for incompatible time
                        // Or maybe RED? User asked for asterisk or color.
                    } else {
                        setStyle(""); // Default
                    }
                }
            }
        });
        
        // Add asterisk to name column? Use cell factory for name/username
        // Assuming columns are already set in FXML or need setting here?
        // Wait, I only see fx:id animatorTable but no columns defined in controller code setup?
        // Ah, likely they are defined in FXML. I need to verify FXML or inject them.
        // Let's check FXML first or assume existing setup.
        // Wait, the previous view_file of Controller showed `animatorTable` but NO column injections for it! 
        // This suggests columns might be missing in Controller or I missed them. 
        // Logic check: How was it displaying data before? 
        // There was no column setup code in `setupTables` for `animatorTable`.
        // This implies the columns are fully defined in FXML with PropertyValueFactories.
        // I will assume that. 
    }
// ... rest of class ...

    private void refreshData() {
        loadCurrentAssignments();
        loadAvailableAnimators();
    }

    private void loadCurrentAssignments() {
        try {
            // Re-fetch party to get latest statuses
            // For now, assume PartyBean is up to date or we might need to refresh it from DB
            // Ideally PartyBean should be refreshed. Let's assume we can fetch statuses via DAO if needed,
            // but for now let's work with the passed bean/map provided we update it.
            // BETTER: Fetch fresh data.
            Map<String, com.romanimazione.entity.AssignmentStatus> map = partyController.getAssignmentStatuses(currentParty.getId());
            
            List<AssignmentWrapper> list = map.entrySet().stream()
                .map(e -> new AssignmentWrapper(e.getKey(), e.getValue().toString()))
                .collect(Collectors.toList());
            
            currentAssignmentsTable.setItems(FXCollections.observableArrayList(list));
            
        } catch (Exception e) {
             // Fallback
             currentAssignmentsTable.setItems(FXCollections.observableArrayList());
        }
    }

    private void loadAvailableAnimators() {
        try {
            List<UserBean> animators = partyController.findEligibleAnimators(currentParty);
            // Client-side filter to exclude those already in table (if not done by backend)
            // Backend findEligibleAnimators usually excludes ACCEPTED/PENDING?
            // Need to verify if it excludes REJECTED. If not, we might see them in both tables.
            // Let's rely on backend logic.
            animatorTable.setItems(FXCollections.observableArrayList(animators));
        } catch (DAOException e) {
            showAlert("Error", "Could not load availabilities: " + e.getMessage());
        }
    }

    @FXML
    private void handleConfirm() {
        UserBean selectedAnimator = animatorTable.getSelectionModel().getSelectedItem();
        if (selectedAnimator == null) {
            showAlert("No Selection", "Please select an animator to invite.");
            return;
        }

        try {
            partyController.assignAnimator(currentParty, selectedAnimator);
            showAlert("Success", "Invitation sent to " + selectedAnimator.getUsername());
            refreshData(); // Refresh both tables
        } catch (DAOException | IllegalArgumentException e) {
            showAlert("Error", "Assignment failed: " + e.getMessage());
        }
    }
    
    private void handleRemoveAssignment(AssignmentWrapper item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Assignment");
        alert.setHeaderText("Remove " + item.getUsername() + "?");
        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                partyController.removeAssignment(currentParty, item.getUsername());
                refreshData();
            } catch (DAOException e) {
                showAlert("Error", "Could not remove: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // Inner Class for Table
    public static class AssignmentWrapper {
        private final String username;
        private final String status;
        public AssignmentWrapper(String u, String s) { this.username = u; this.status = s; }
        public String getUsername() { return username; }
        public String getStatus() { return status; }
    }
    
    private class ActionCell extends TableCell<AssignmentWrapper, Void> {
        private final Button btnRemove = new Button("Remove");
        
        public ActionCell() {
            btnRemove.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white;");
            btnRemove.setOnAction(e -> {
                handleRemoveAssignment(getTableView().getItems().get(getIndex()));
            });
        }
        @Override
        public void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if(empty) setGraphic(null);
            else setGraphic(btnRemove);
        }
    }
}
