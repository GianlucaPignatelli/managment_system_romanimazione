package com.romanimazione.controller.graphic;

import com.romanimazione.bean.UserBean;
import com.romanimazione.exception.DAOException;
import com.romanimazione.view.MainApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaFXUserManagementController {

    @FXML private TableView<UserBean> usersTable;
    @FXML private TableColumn<UserBean, Void> actionColumn;

    private ObservableList<UserBean> usersList = FXCollections.observableArrayList();
    private static final Logger LOGGER = Logger.getLogger(JavaFXUserManagementController.class.getName());

    @FXML
    public void initialize() {
        loadUsers();
        usersTable.setItems(usersList);
        setupActionColumn();
    }

    private void loadUsers() {
        usersList.clear();
        try {
            com.romanimazione.controller.application.AdminUserController controller = new com.romanimazione.controller.application.AdminUserController();
            List<UserBean> beans = controller.getAllUsers();
            usersList.addAll(beans);
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load users: " + e.getMessage());
        }
    }

    private void setupActionColumn() {
        Callback<TableColumn<UserBean, Void>, TableCell<UserBean, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<UserBean, Void> call(final TableColumn<UserBean, Void> param) {
                return new UserActionCell();
            }
        };
        actionColumn.setCellFactory(cellFactory);
    }

    private class UserActionCell extends TableCell<UserBean, Void> {
        private final Button deleteButton = new Button("Delete");

        public UserActionCell() {
            deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            deleteButton.setOnAction((event) -> {
                UserBean user = getTableView().getItems().get(getIndex());
                handleDeleteUser(user);
            });
        }

        @Override
        public void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                UserBean currentUser = getTableView().getItems().get(getIndex());
                if (currentUser.isSuperAdmin()) {
                    deleteButton.setDisable(true);
                    deleteButton.setText("Super Admin");
                } else {
                    deleteButton.setDisable(false);
                    deleteButton.setText("Delete");
                }
                setGraphic(deleteButton);
            }
        }
    }

    private void handleDeleteUser(UserBean user) {
        // Confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Delete user " + user.getUsername() + "?");
        alert.setContentText("Are you sure? This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                com.romanimazione.controller.application.AdminUserController controller = new com.romanimazione.controller.application.AdminUserController();
                controller.deleteUser(user);
                usersList.remove(user);
                showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully.");
            } catch (DAOException | IllegalArgumentException e) {
                 showAlert(Alert.AlertType.ERROR, "Error", "Could not delete user: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleBack() {
        try {
            MainApp.setRoot("admin_dashboard");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating back", e);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
