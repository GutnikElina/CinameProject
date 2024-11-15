package Admin.HallActions;

import Models.Hall;
import Utils.AppUtils;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UpdateHall extends HallActionBase {

    private TextField idField;
    private TextField nameField;
    private TextField capacityField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Обновить зал");

        idField = UIUtils.createTextField("Введите ID зала");
        nameField = UIUtils.createTextField("Введите название зала");
        capacityField = UIUtils.createTextField("Введите вместимость зала");

        VBox vbox = UIUtils.createVBox(15, javafx.geometry.Pos.CENTER, idField, nameField, capacityField,
                UIUtils.createButton("Обновить зал", 200, e -> updateHallAction(primaryStage), false));

        vbox.setPadding(new javafx.geometry.Insets(15));
        vbox.getStyleClass().add("vbox");

        Scene scene = new Scene(vbox, 350, 250);
        scene.getStylesheets().add("/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateHallAction(Stage stage) {
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
        sendHallCommand("UPDATE", hall, stage);
    }
}