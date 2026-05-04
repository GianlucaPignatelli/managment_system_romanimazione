package com.romanimazione.controller.graphic;

import com.romanimazione.bean.EquipmentBean;
import com.romanimazione.bean.SessionBean;
import com.romanimazione.controller.application.EquipmentController;
import com.romanimazione.exception.DAOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;

public class JavaFXEquipmentController {

    @FXML private TableView<EquipmentBean> equipmentTable;
    @FXML private TableColumn<EquipmentBean, String> idCol;
    @FXML private TableColumn<EquipmentBean, String> nameCol;
    @FXML private TableColumn<EquipmentBean, String> categoryCol;
    @FXML private TableColumn<EquipmentBean, String> quantityCol;
    @FXML private TableColumn<EquipmentBean, String> conditionCol;
    @FXML private TableColumn<EquipmentBean, String> adminCol;

    @FXML private TextField nameField;
    @FXML private ComboBox<String> categoryBox;
    @FXML private TextField quantityField;
    @FXML private ComboBox<String> conditionBox;

    private static final List<String> CATEGORIES = java.util.Arrays.asList(
        "borsone giochi", "carretto", "borsa magia", "cassa audio", "gonfiabile"
    );
    private static final List<String> CONDITIONS = java.util.Arrays.asList(
        "ottimo", "buono", "discreto", "rotto"
    );

    private static final String SUCCESS_TITLE = "Success";
    private static final String ERROR_TITLE = "Error";

    private EquipmentController appController;
    private ObservableList<EquipmentBean> eqList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        appController = new EquipmentController();
        
        if (idCol != null) idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (nameCol != null) nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        if (categoryCol != null) categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        if (quantityCol != null) quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        if (conditionCol != null) conditionCol.setCellValueFactory(new PropertyValueFactory<>("condition"));
        if (adminCol != null) adminCol.setCellValueFactory(new PropertyValueFactory<>("adminUsername"));

        if (equipmentTable != null) {
            equipmentTable.setItems(eqList);
            loadEquipment();
        }

        if (categoryBox != null) categoryBox.setItems(FXCollections.observableArrayList(CATEGORIES));
        if (conditionBox != null) conditionBox.setItems(FXCollections.observableArrayList(CONDITIONS));
    }

    private void loadEquipment() {
        eqList.clear();
        try {
            List<EquipmentBean> beans = appController.getAllEquipment();
            eqList.addAll(beans);
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Error Loading", e.getMessage());
        }
    }

    @FXML
    public void handleAddEquipment() {
        try {
            EquipmentBean bean = new EquipmentBean();
            bean.setName(nameField.getText());
            bean.setCategory(categoryBox.getValue() != null ? categoryBox.getValue() : "");
            bean.setQuantity(quantityField.getText());
            bean.setCondition(conditionBox.getValue() != null ? conditionBox.getValue() : "");
            bean.setAdminUsername(SessionBean.getInstance().getCurrentUser().getUsername());
            
            appController.addEquipment(bean);
            showAlert(Alert.AlertType.INFORMATION, SUCCESS_TITLE, "Equipment added successfully.");
            loadEquipment();
            clearFields();
        } catch (IllegalArgumentException | DAOException e) {
            showAlert(Alert.AlertType.ERROR, ERROR_TITLE, e.getMessage());
        }
    }

    @FXML
    public void handleUpdateEquipment() {
        if (equipmentTable == null) return;
        EquipmentBean selected = equipmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Select an equipment to update.");
            return;
        }

        try {
            EquipmentBean bean = new EquipmentBean();
            bean.setId(selected.getId());
            // If field is empty, keep old value
            bean.setName(nameField.getText().isEmpty() ? selected.getName() : nameField.getText());
            bean.setCategory(categoryBox.getValue() != null && !categoryBox.getValue().isEmpty() ? categoryBox.getValue() : selected.getCategory());
            bean.setQuantity(quantityField.getText().isEmpty() ? selected.getQuantity() : quantityField.getText());
            bean.setCondition(conditionBox.getValue() != null && !conditionBox.getValue().isEmpty() ? conditionBox.getValue() : selected.getCondition());
            bean.setAdminUsername(SessionBean.getInstance().getCurrentUser().getUsername());
            
            appController.updateEquipment(bean);
            showAlert(Alert.AlertType.INFORMATION, SUCCESS_TITLE, "Equipment updated successfully.");
            loadEquipment();
            clearFields();
        } catch (IllegalArgumentException | DAOException e) {
            showAlert(Alert.AlertType.ERROR, ERROR_TITLE, e.getMessage());
        }
    }

    @FXML
    public void handleDeleteEquipment() {
        if (equipmentTable == null) return;
        EquipmentBean selected = equipmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Select an equipment to delete.");
            return;
        }
        
        try {
            appController.deleteEquipment(selected);
            showAlert(Alert.AlertType.INFORMATION, SUCCESS_TITLE, "Equipment deleted successfully.");
            loadEquipment();
        } catch (IllegalArgumentException | DAOException e) {
            showAlert(Alert.AlertType.ERROR, ERROR_TITLE, e.getMessage());
        }
    }

    private void clearFields() {
        if (nameField != null) nameField.clear();
        if (categoryBox != null) categoryBox.getSelectionModel().clearSelection();
        if (quantityField != null) quantityField.clear();
        if (conditionBox != null) conditionBox.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    public void handleBack() {
        try {
            com.romanimazione.view.MainApp.setRoot("admin_dashboard");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not go back: " + e.getMessage());
        }
    }
}
