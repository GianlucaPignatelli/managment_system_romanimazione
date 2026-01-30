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
}
