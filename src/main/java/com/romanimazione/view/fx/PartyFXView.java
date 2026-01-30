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

    private static final String FXML_PATH = "/view/fxml/party_form.fxml";

    public void render() throws IOException {
        // Option 1: Use MainApp to switch root (Single Page Application style)
        // This keeps the primary stage
        MainApp.setRoot("party_form");
        
        // Note: Strict Boundary classes might return the Parent node
        // so the Controller can decide where to put it, but in this 
        // ISPW context, "Boundary" often implies "Screen Manager" for that use case.
    }

    public void showList() throws IOException {
        MainApp.setRoot("party_list");
    }
    
    /**
     * Alternative method if we wanted to open in a new window (Stage)
     */
    public void renderInNewWindow(String fxmlPath) throws IOException {
        if (fxmlPath == null || fxmlPath.isEmpty()) {
            fxmlPath = FXML_PATH;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Create New Party");
        stage.setScene(new Scene(root));
        stage.show();
    }
    
    // Overload for default
    public void renderInNewWindow() throws IOException {
        renderInNewWindow(FXML_PATH);
    }
}
