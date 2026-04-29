package com.romanimazione.controller.graphic;

import com.romanimazione.bean.PartyBean;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class JavaFXPartyDetailsController {

    @FXML private Label nameLabel;
    @FXML private Label typeLabel;
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Label addressLabel;
    @FXML private Label clientLabel;
    @FXML private Label costLabel;
    @FXML private Label servicesLabel;
    @FXML private Label notesLabel;

    public void setPartyData(PartyBean party) {
        if (party == null) return;
        nameLabel.setText(party.getName());
        typeLabel.setText(party.getType());
        dateLabel.setText(party.getDate());
        timeLabel.setText(party.getStartTime() + " - " + party.getEndTime());
        addressLabel.setText(party.getAddress());
        clientLabel.setText(party.getClientName() + " (" + party.getClientPhone() + ")");
        costLabel.setText("€ " + party.getCost());
        servicesLabel.setText(party.getDescription());
        notesLabel.setText("Status: " + party.getStatus()); // Using notes for status or description
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) nameLabel.getScene().getWindow();
        stage.close();
    }
}
