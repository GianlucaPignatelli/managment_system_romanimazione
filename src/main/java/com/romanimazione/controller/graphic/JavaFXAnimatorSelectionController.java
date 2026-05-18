package com.romanimazione.controller.graphic;

import com.romanimazione.bean.PartyBean;
import com.romanimazione.bean.UserBean;
import com.romanimazione.controller.application.PartyController;
import com.romanimazione.exception.DAOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.List;
import java.util.Map;

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
                    getStyleClass().removeAll(ROW_WARNING_CLASS);
                } else {
                    if (!Boolean.parseBoolean(item.getIsTimeCompatible())) {
                        if (!getStyleClass().contains(ROW_WARNING_CLASS)) getStyleClass().add(ROW_WARNING_CLASS);
                    } else {
                        getStyleClass().removeAll(ROW_WARNING_CLASS);
                    }
                }
            }
        });
    }

    private static final String ERROR_TITLE = "Error";
    private static final String ROW_WARNING_CLASS = "row-warning";

    private void refreshData() {
        loadCurrentAssignments();
        loadAvailableAnimators();
    }

    private void loadCurrentAssignments() {
        try {
            Map<String, com.romanimazione.entity.AssignmentStatus> map = partyController.getAssignmentStatuses(Integer.parseInt(currentParty.getId()));
            Map<String, java.time.LocalDateTime> timestamps = partyController.getAssignmentTimestamps(Integer.parseInt(currentParty.getId()));
            
            List<AssignmentWrapper> list = map.entrySet().stream()
                .map(e -> {
                    String extra = "";
                    if (e.getValue() == com.romanimazione.entity.AssignmentStatus.PENDING) {
                        java.time.LocalDateTime assignedAt = timestamps.get(e.getKey());
                        if (assignedAt != null) {
                             java.time.Duration rem = java.time.Duration.between(java.time.LocalDateTime.now(), assignedAt.plusHours(24));
                             if (!rem.isNegative()) {
                                 extra = String.format(" (%dh %dm left)", rem.toHours(), rem.toMinutes() % 60);
                             } else {
                                 extra = " (Expired)";
                             }
                        }
                    }
                    return new AssignmentWrapper(e.getKey(), e.getValue().toString() + extra);
                })
                .toList();

            currentAssignmentsTable.setItems(FXCollections.observableArrayList(list));
            
        } catch (Exception e) {
             // Fallback
             currentAssignmentsTable.setItems(FXCollections.observableArrayList());
        }
    }

    private void loadAvailableAnimators() {
        try {
            List<UserBean> animators = partyController.findEligibleAnimators(currentParty);
            if (animators.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Nessun Animator Disponibile");
                alert.setHeaderText("Nessun animator disponibile per questa festa.");
                alert.setContentText("Vuoi forzare l'assegnazione mostrando tutti gli animatori del sistema?");
                if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    animators = partyController.findAllAnimatorsForForce(currentParty);
                }
            }
            animatorTable.setItems(FXCollections.observableArrayList(animators));
        } catch (DAOException e) {
            showAlert(ERROR_TITLE, "Could not load availabilities: " + e.getMessage());
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
            showAlert(ERROR_TITLE, "Assignment failed: " + e.getMessage());
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
                showAlert(ERROR_TITLE, "Could not remove: " + e.getMessage());
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
            btnRemove.getStyleClass().add("button-danger");
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
