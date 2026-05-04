package com.romanimazione.controller.graphic;

import com.romanimazione.bean.PartyBean;
import com.romanimazione.bean.UserBean;
import com.romanimazione.controller.application.AcceptedJobsController;
import com.romanimazione.bean.SessionBean;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class JavaFXAcceptedJobsController {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    
    @FXML private TableView<PartyBean> jobsTable;
    @FXML private TableColumn<PartyBean, String> dateColumn;
    @FXML private TableColumn<PartyBean, String> timeColumn;
    @FXML private TableColumn<PartyBean, String> typeColumn;
    @FXML private TableColumn<PartyBean, String> cityColumn;
    @FXML private TableColumn<PartyBean, String> feeColumn; 
    
    private final AcceptedJobsController appController;

    public JavaFXAcceptedJobsController() {
        this.appController = new AcceptedJobsController();
    }

    @FXML
    public void initialize() {
        setupColumns();
        // Default to all future jobs if empty filters
        loadJobs(LocalDate.now(), null);
    }
    
    private void setupColumns() {
        dateColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate() != null ? cell.getValue().getDate() : ""));
        timeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStartTime() + " - " + cell.getValue().getEndTime()));
        typeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType()));
        cityColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getAddress()));
        feeColumn.setCellValueFactory(cell -> new SimpleStringProperty("€ " + cell.getValue().getCost())); 
        
        jobsTable.setRowFactory(tv -> {
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

    @FXML
    private void handleFilter() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        
        // If neither is selected, default to all future jobs
        if (start == null && end == null) {
            start = LocalDate.now();
        }
        
        loadJobs(start, end);
    }

    private void loadJobs(LocalDate start, LocalDate end) {
        UserBean currentUser = SessionBean.getInstance().getCurrentUser();
        try {
            List<PartyBean> jobs = appController.getAcceptedJobs(currentUser, start, end);
            jobsTable.setItems(FXCollections.observableArrayList(jobs));
        } catch (Exception e) {
            showAlert("Error", "Could not load accepted jobs: " + e.getMessage());
        }
    }

    private void showPartyDetails(PartyBean partyBean) {
        try {
            com.romanimazione.view.fx.PartyFXView dialogView = new com.romanimazione.view.fx.PartyFXView();
            dialogView.openDetailsDialog(partyBean);
        } catch (IOException ex) {
            showAlert("Error", "Could not show details: " + ex.getMessage());
        }
    }

    @FXML
    private void handleBack() throws IOException {
        new com.romanimazione.view.fx.MainFXView().showAnimatorDashboard();
    }

    private void showAlert(String headerTitle, String bodyMsg) {
        Alert.AlertType t = headerTitle.equals("Success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        Alert popup = new Alert(t);
        popup.setTitle(headerTitle);
        popup.setContentText(bodyMsg);
        popup.setHeaderText(null);
        popup.showAndWait();
    }
}
