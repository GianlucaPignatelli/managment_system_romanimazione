package com.romanimazione.controller.graphic;

import javafx.fxml.FXML;

import java.io.IOException;

public class JavaFXHomeController {

    @FXML
    private void goToLogin() throws IOException {
        new com.romanimazione.view.fx.LoginFXView().showLogin();
    }

    @FXML
    private void goToRegister() throws IOException {
        new com.romanimazione.view.fx.LoginFXView().showRegister();
    }


}
