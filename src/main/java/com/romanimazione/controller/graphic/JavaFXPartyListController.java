package com.romanimazione.controller.graphic;

import com.romanimazione.bean.PartyBean;
import com.romanimazione.controller.application.PartyController;
import com.romanimazione.exception.DAOException;
import javafx.beans.property.SimpleStringProperty;
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
import com.romanimazione.controller.application.Observer;

public class JavaFXPartyListController implements Observer {

    private static final String ERROR_TITLE = "Error";
    @FXML private TableView<PartyBean> partyTable;
    @FXML private TableColumn<PartyBean, Void> assignColumn;
    @FXML private TableColumn<PartyBean, String> assignmentStatusColumn;
    
    private final PartyController partyController;

    public JavaFXPartyListController() {
        this.partyController = new PartyController();
        this.partyController.attach(this);
    }

    @FXML
    public void initialize() {
        addButtonToTable();
        loadParties();

        partyTable.setRowFactory(tv -> {
            TableRow<PartyBean> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    PartyBean rowData = row.getItem();
                    try {
                        new com.romanimazione.view.fx.PartyFXView().openDetailsDialog(rowData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row ;
        });

        // Assignment Status Logic
        assignmentStatusColumn.setCellValueFactory(cell -> {
            PartyBean p = cell.getValue();
            try {
                int count = partyController.getProposalCount(Integer.parseInt(p.getId()));
                int status = partyController.getAssignmentFeedback(Integer.parseInt(p.getId()));
                
                String icon = "";
                switch(status) {
                    case 1: icon = "✅"; break; // All Accepted
                    case -1: icon = "❌"; break; // Rejected
                    case 0: icon = "⏳"; break; // Pending
                    default: icon = "⚪"; break; // None
                }
                
                return new SimpleStringProperty("Props: " + count + " " + icon);
            } catch (Exception e) {
                return new SimpleStringProperty("?");
            }
        });
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
            
            btnCancel.getStyleClass().add("button-danger");
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
                if (com.romanimazione.entity.PartyStatus.CANCELLED.name().equals(party.getStatus())) {
                    btnAssign.setDisable(true);
                    btnCancel.setDisable(true);
                    btnCancel.setText("Cancelled");
                    btnCancel.getStyleClass().add("button-cancel"); // Greyed out
                } else if (com.romanimazione.entity.PartyStatus.COMPLETED.name().equals(party.getStatus())) {
                    btnAssign.setDisable(true);
                    btnCancel.setDisable(true);
                } else {
                    btnAssign.setDisable(false);
                    btnCancel.setDisable(false);
                    btnCancel.setText("Cancel");
                    btnCancel.getStyleClass().add("button-danger");
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

    @Override
    public void update(String message) {
        javafx.application.Platform.runLater(this::loadParties);
    }
}
