package com.romanimazione.view.fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.romanimazione.view.MainApp;

import java.io.IOException;

/**
 * Boundary class for the Party Form View.
 * Responsibilities:
 * - Load the FXML file
 * - Show the scene (via MainApp or Stage)
 */
public class PartyFXView {



    public void render() throws IOException {
        com.romanimazione.config.Configuration config = new com.romanimazione.config.Configuration();
        String fxmlPath = config.getProperty("view.party_form.name", "party_form");
        
        // Option 1: Use MainApp to switch root (Single Page Application style)
        MainApp.setRoot(fxmlPath);
    }

    public void showList() throws IOException {
        // Could also externalize this string if desired
        MainApp.setRoot("party_list");
    }

    public void openAssignmentDialog(com.romanimazione.bean.PartyBean party) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/animator_selection.fxml"));
        Parent page = loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Assign Animator");
        dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
        // It's tricky to get the owner window without passing it, but for now we can open it.
        // Or better: Let MainApp handle stage or pass it. 
        // For simplicity in this context, we create a new Stage. 
        // Ideally, we'd pass the owner Stage, but let's assume standalone or we get primary stage helper.
        
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        com.romanimazione.controller.graphic.JavaFXAnimatorSelectionController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.initData(party);

        dialogStage.showAndWait();
    }
}
