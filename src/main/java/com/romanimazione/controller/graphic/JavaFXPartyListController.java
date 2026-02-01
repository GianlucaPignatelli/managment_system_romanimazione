package com.romanimazione.controller.graphic;

import com.romanimazione.bean.PartyBean;
import com.romanimazione.controller.application.PartyController;
import com.romanimazione.exception.DAOException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;

public class JavaFXPartyListController {

    private static final String ERROR_TITLE = "Error";
    @FXML private TableView<PartyBean> partyTable;
    @FXML private TableColumn<PartyBean, Void> assignColumn; // Void because button doesn't map to a field
    
    private final PartyController partyController;

    public JavaFXPartyListController() {
        this.partyController = new PartyController();
    }

    @FXML
    public void initialize() {
        addButtonToTable();
        loadParties();
    }

    private void addButtonToTable() {
        Callback<TableColumn<PartyBean, Void>, TableCell<PartyBean, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<PartyBean, Void> call(final TableColumn<PartyBean, Void> param) {
                return new ActionCell();
            }
        };

        assignColumn.setCellFactory(cellFactory);
    }
    
    // Named Inner Class to resolve SonarCloud "Anonymous inner class containing only this method" and initializer issues
    private class ActionCell extends TableCell<PartyBean, Void> {
        private final Button btnAssign = new Button("Assign");
        private final Button btnCancel = new Button("Cancel");
        private final HBox pane = new HBox(5, btnAssign, btnCancel);

        public ActionCell() {
            btnAssign.setOnAction(event -> {
                PartyBean data = getTableView().getItems().get(getIndex());
                openAssignmentDialog(data);
            });
            
            btnCancel.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white;");
            btnCancel.setOnAction(event -> {
                PartyBean data = getTableView().getItems().get(getIndex());
                handleCancelParty(data);
            });
        }

        @Override
        public void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                PartyBean party = getTableView().getItems().get(getIndex());
                
                // Visual Logic
                if (party.getStatus() == com.romanimazione.entity.PartyStatus.CANCELLED) {
                    btnAssign.setDisable(true);
                    btnCancel.setDisable(true);
                    btnCancel.setText("Cancelled");
                    btnCancel.setStyle("-fx-background-color: #555; -fx-text-fill: white;"); // Greyed out
                } else if (party.getStatus() == com.romanimazione.entity.PartyStatus.COMPLETED) {
                    btnAssign.setDisable(true);
                    btnCancel.setDisable(true);
                } else {
                    btnAssign.setDisable(false);
                    btnCancel.setDisable(false);
                    btnCancel.setText("Cancel");
                    btnCancel.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white;");
                }
                
                setGraphic(pane);
            }
        }
    }
    
    private void handleCancelParty(PartyBean party) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Party");
        alert.setHeaderText("Are you sure you want to cancel the party: " + party.getName() + "?");
        alert.setContentText("This action cannot be undone. Assigned animators will be notified.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                partyController.cancelParty(party);
                loadParties(); // Refresh list to update status
            } catch (DAOException e) {
                showAlert(ERROR_TITLE, "Could not cancel party: " + e.getMessage());
            }
        }
    }
    
    private void openAssignmentDialog(PartyBean party) {
        try {
            new com.romanimazione.view.fx.PartyFXView().openAssignmentDialog(party);
        } catch (IOException e) {
            java.util.logging.Logger.getLogger(JavaFXPartyListController.class.getName()).log(java.util.logging.Level.SEVERE, "Error loading assignment dialog", e);
            showAlert(ERROR_TITLE, "Could not load assignment dialog: " + e.getMessage());
        }
    }

    private void loadParties() {
        try {
            List<PartyBean> parties = partyController.getAllParties();
            partyTable.setItems(FXCollections.observableArrayList(parties));
        } catch (DAOException e) {
            showAlert("Error loading parties", e.getMessage());
        }
    }

    @FXML
    private void handleBack() throws IOException {
        new com.romanimazione.view.fx.MainFXView().showAdminDashboard();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(ERROR_TITLE);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
