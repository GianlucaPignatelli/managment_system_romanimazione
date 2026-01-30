package com.romanimazione.controller.graphic;

import com.romanimazione.bean.PartyBean;
import com.romanimazione.controller.application.PartyController;
import com.romanimazione.exception.DAOException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.List;

public class JavaFXPartyListController {

    @FXML private TableView<PartyBean> partyTable;
    
    private final PartyController partyController;

    public JavaFXPartyListController() {
        this.partyController = new PartyController();
    }

    @FXML
    public void initialize() {
        loadParties();
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
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
