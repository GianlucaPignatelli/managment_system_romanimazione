package com.romanimazione.controller.graphic;

import com.romanimazione.bean.UserBean;
import com.romanimazione.dao.DAOFactory;
import com.romanimazione.dao.PartyDAO;
import com.romanimazione.dao.UserDAO;
import com.romanimazione.entity.AssignmentStatus;
import com.romanimazione.entity.Party;
import com.romanimazione.entity.User;
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
            UserDAO userDAO = DAOFactory.getDAOFactory().getUserDAO();
            List<User> users = userDAO.findAllUsers();
            for (User u : users) {
                UserBean bean = new UserBean();
                bean.setId(u.getId()); // Assuming User has ID
                bean.setUsername(u.getUsername());
                bean.setNome(u.getNome());
                bean.setCognome(u.getCognome());
                bean.setEmail(u.getEmail());
                bean.setRole(u.getRole());
                bean.setSuperAdmin(u.isSuperAdmin());
                usersList.add(bean);
            }
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load users: " + e.getMessage());
        }
    }

    private void setupActionColumn() {
        Callback<TableColumn<UserBean, Void>, TableCell<UserBean, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<UserBean, Void> call(final TableColumn<UserBean, Void> param) {
                return new TableCell<>() {
                    private final Button deleteButton = new Button("Delete");

                    {
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
                            // Disable delete for Self or other Super Admins (optional policy)
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
                };
            }
        };
        actionColumn.setCellFactory(cellFactory);
    }

    private void handleDeleteUser(UserBean user) {
        // Confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Delete user " + user.getUsername() + "?");
        alert.setContentText("Are you sure? This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            
            // CONSTRAINT CHECK
            if ("ANIMATORE".equalsIgnoreCase(user.getRole())) {
                if (hasActiveAssignments(user.getUsername())) {
                     showAlert(Alert.AlertType.WARNING, "Cannot Delete", 
                             "Animator " + user.getUsername() + " has active party assignments.\n" +
                             "Please remove them from all parties in 'List All Parties' before deleting.");
                     return;
                }
            }

            try {
                UserDAO userDAO = DAOFactory.getDAOFactory().getUserDAO();
                 // Create dummy user object for deletion (DAO usually needs just ID or Username)
                User userEntity = new User();
                userEntity.setId(user.getId());
                userEntity.setUsername(user.getUsername());
                
                userDAO.deleteUser(userEntity.getUsername());
                usersList.remove(user);
                showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully.");
            } catch (DAOException e) {
                 showAlert(Alert.AlertType.ERROR, "Error", "Could not delete user: " + e.getMessage());
            }
        }
    }

    private boolean hasActiveAssignments(String username) {
        try {
            PartyDAO partyDAO = DAOFactory.getDAOFactory().getPartyDAO();
            List<Party> allParties = partyDAO.findAllParties();
            
            for (Party p : allParties) {
                if (p.getAssignmentStatuses().containsKey(username)) {
                    AssignmentStatus status = p.getAssignmentStatuses().get(username);
                    // Constraint: Cannot delete if assigned (ACCEPTED) or maybe even PENDING?
                    // User said: "remove them from 'list all parties' first". 
                    // This implies ANY presence in the list.
                    return true;
                }
            }
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error checking assignments", e);
            return true; // Fail safe
        }
        return false;
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
