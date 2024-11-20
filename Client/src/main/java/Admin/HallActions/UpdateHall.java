package Admin.HallActions;

import Admin.GeneralActions.HallActionBase;
import Models.Hall;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateHall extends HallActionBase {
    @FXML
    public Button updateButton;
    @FXML
    private TextField idField, nameField, capacityField;
    @FXML
    private Button backButton;

    @FXML
    private void updateHallAction(javafx.event.ActionEvent event) {
        if (idField.getText().isEmpty() || nameField.getText().isEmpty() || capacityField.getText().isEmpty()) {
            UIUtils.showAlert("Ошибка", "Пожалуйста, заполните все поля.", Alert.AlertType.ERROR);
            return;
        }

        if (!FieldValidator.validateNumericField(idField, "Некорректный ID зала")) return;
        if (!FieldValidator.validateTextField(nameField, "Название зала не может быть пустым", 3)) return;
        if (!FieldValidator.validatePositiveNumericField(capacityField, "Вместимость должна быть положительным числом")) return;

        int id = Integer.parseInt(idField.getText().trim());
        String name = nameField.getText().trim();
        int capacity = Integer.parseInt(capacityField.getText().trim());

        Hall hall = new Hall(id, name, capacity);
        Stage stage = (Stage) updateButton.getScene().getWindow();
        sendHallCommand("HALL;UPDATE", hall, stage);
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}