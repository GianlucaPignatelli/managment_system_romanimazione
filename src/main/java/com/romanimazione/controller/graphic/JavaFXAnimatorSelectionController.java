package com.romanimazione.controller.graphic;

import com.romanimazione.bean.PartyBean;
import com.romanimazione.bean.UserBean;
import com.romanimazione.controller.application.PartyController;
import com.romanimazione.exception.DAOException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;

public class JavaFXAnimatorSelectionController {

    @FXML private Label partyDetailsLabel;
    @FXML private TableView<UserBean> animatorTable;

    private PartyController partyController;
    private PartyBean currentParty;
    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void initData(PartyBean party) {
        this.currentParty = party;
        this.partyController = new PartyController();
        
        partyDetailsLabel.setText(String.format("%s - %s (%s-%s)", 
            party.getName(), party.getDate(), party.getStartTime(), party.getEndTime()));
            
        loadAvailableAnimators();
    }

    private void loadAvailableAnimators() {
        try {
            List<UserBean> animators = partyController.findEligibleAnimators(currentParty);
            animatorTable.setItems(FXCollections.observableArrayList(animators));
        } catch (DAOException e) {
            showAlert("Error", "Could not load availabilities: " + e.getMessage());
        }
    }

    @FXML
    private void handleConfirm() {
        UserBean selectedAnimator = animatorTable.getSelectionModel().getSelectedItem();
        if (selectedAnimator == null) {
            showAlert("No Selection", "Please select an animator.");
            return;
        }

        try {
            partyController.assignAnimator(currentParty, selectedAnimator);
            showAlert("Success", "Animator assigned successfully!");
            dialogStage.close();
        } catch (DAOException | IllegalArgumentException e) {
            showAlert("Error", "Assignment failed: " + e.getMessage());
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
}
