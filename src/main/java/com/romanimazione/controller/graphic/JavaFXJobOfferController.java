package com.romanimazione.controller.graphic;

import com.romanimazione.bean.PartyBean;
import com.romanimazione.bean.UserBean;
import com.romanimazione.controller.application.JobOfferController;
import com.romanimazione.bean.SessionBean;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;

public class JavaFXJobOfferController {

    @FXML private TableView<PartyBean> offersTable;
    @FXML private TableColumn<PartyBean, String> dateColumn;
    @FXML private TableColumn<PartyBean, String> timeColumn;
    @FXML private TableColumn<PartyBean, String> typeColumn;
    @FXML private TableColumn<PartyBean, String> cityColumn;
    @FXML private TableColumn<PartyBean, String> feeColumn; 
    @FXML private TableColumn<PartyBean, String> statusColumn;
    @FXML private TableColumn<PartyBean, Void> actionColumn;

    private final JobOfferController appController;

    public JavaFXJobOfferController() {
        this.appController = new JobOfferController();
    }

    @FXML
    public void initialize() {
        setupColumns();
        setupInteraction();
        loadOffers();
    }
    
    private void setupInteraction() {
        offersTable.setRowFactory(tv -> {
            TableRow<PartyBean> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    PartyBean rowData = row.getItem();
                    showPartyDetails(rowData);
                }
            });
            return row ;
        });
    }

    private void setupColumns() {
        dateColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate() != null ? cell.getValue().getDate() : ""));
        timeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStartTime() + " - " + cell.getValue().getEndTime()));
        typeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType()));
        cityColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getAddress()));
        feeColumn.setCellValueFactory(cell -> new SimpleStringProperty("€ " + cell.getValue().getCost())); 
        
        statusColumn.setCellValueFactory(cell -> {
             UserBean currentUser = SessionBean.getInstance().getCurrentUser();
             return new SimpleStringProperty(formatStatusText(cell.getValue(), currentUser));
        });

        Callback<TableColumn<PartyBean, Void>, TableCell<PartyBean, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<PartyBean, Void> call(final TableColumn<PartyBean, Void> param) {
                return new ActionCell();
            }
        };
        actionColumn.setCellFactory(cellFactory);
    }
    
    private String formatStatusText(PartyBean party, UserBean currentUser) {
        var statuses = party.getAssignmentStatuses();
        
        if (statuses == null || !statuses.containsKey(currentUser.getUsername())) {
            return "UNKNOWN";
        }

        String status = statuses.get(currentUser.getUsername());
        if (com.romanimazione.entity.AssignmentStatus.PENDING.name().equals(status)) {
            var timestamps = party.getAssignmentTimestamps();
            String timestampStr = timestamps != null ? timestamps.get(currentUser.getUsername()) : null;
            return status + calculateRemainingTimeStr(timestampStr);
        }
        
        return status;
    }
    
    private String calculateRemainingTimeStr(String timestampStr) {
        if (timestampStr == null) return "";
        
        java.time.LocalDateTime assignedAt = java.time.LocalDateTime.parse(timestampStr);
        java.time.Duration rem = java.time.Duration.between(java.time.LocalDateTime.now(), assignedAt.plusHours(24));
        
        if (rem.isNegative()) {
            return " (Expired)";
        }
        
        long hours = rem.toHours();
        long minutes = rem.toMinutes() % 60;
        return String.format(" (%dh %dm left)", hours, minutes);
    }
    
    private class ActionCell extends TableCell<PartyBean, Void> {
        private final Button btnAccept = new Button("Accept");
        private final Button btnReject = new Button("Reject");
        private final HBox paneButtons = new HBox(10, btnAccept, btnReject);
        private final Label lblAccepted = new Label("✅ Accepted");

        public ActionCell() {
            btnAccept.getStyleClass().add("button-success");
            btnAccept.setOnAction(event -> {
                PartyBean party = getTableView().getItems().get(getIndex());
                handleAccept(party);
            });

            btnReject.getStyleClass().add("button-danger");
            btnReject.setOnAction(event -> {
                PartyBean party = getTableView().getItems().get(getIndex());
                handleReject(party);
            });
            
            lblAccepted.getStyleClass().add("label-success");
        }

        @Override
        public void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                PartyBean party = getTableView().getItems().get(getIndex());
                UserBean currentUser = SessionBean.getInstance().getCurrentUser();
                var status = party.getAssignmentStatuses().get(currentUser.getUsername());
                
                if (com.romanimazione.entity.AssignmentStatus.ACCEPTED.name().equals(status)) {
                    setGraphic(lblAccepted);
                } else {
                    setGraphic(paneButtons);
                }
            }
        }
    }
    
    private static final String ERROR_TITLE = "Error";

    private void showPartyDetails(PartyBean party) {
        try {
            new com.romanimazione.view.fx.PartyFXView().openDetailsDialog(party);
        } catch (IOException e) {
            showAlert(ERROR_TITLE, "Could not show details: " + e.getMessage());
        }
    }

    private void loadOffers() {
        UserBean currentUser = SessionBean.getInstance().getCurrentUser();
        try {
            List<PartyBean> offers = appController.getPendingOffers(currentUser);
            offersTable.setItems(FXCollections.observableArrayList(offers));
        } catch (Exception e) {
            showAlert(ERROR_TITLE, "Could not load offers: " + e.getMessage());
        }
    }

    private void handleAccept(PartyBean party) {
        UserBean currentUser = SessionBean.getInstance().getCurrentUser();
        try {
            appController.acceptOffer(party, currentUser);
            loadOffers(); // Refresh
            showAlert("Success", "You accepted the job: " + party.getName());
        } catch (Exception e) {
            showAlert(ERROR_TITLE, "Could not accept offer: " + e.getMessage());
        }
    }

    private void handleReject(PartyBean party) {
        UserBean currentUser = SessionBean.getInstance().getCurrentUser();
        try {
            appController.rejectOffer(party, currentUser);
            loadOffers(); // Refresh
        } catch (Exception e) {
            showAlert(ERROR_TITLE, "Could not reject offer: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() throws IOException {
        new com.romanimazione.view.fx.MainFXView().showAnimatorDashboard();
    }

    private void showAlert(String msgTitle, String msgBody) {
        Alert.AlertType alertType = msgTitle.equals("Success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        Alert a = new Alert(alertType);
        a.setTitle(msgTitle);
        a.setContentText(msgBody);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
