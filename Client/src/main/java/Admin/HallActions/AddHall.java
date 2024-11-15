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

public class AddHall extends HallActionBase {

    private TextField nameField;
    private TextField capacityField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Добавить зал");

        nameField = UIUtils.createTextField("Название зала");
        capacityField = UIUtils.createTextField("Вместимость");

        VBox vbox = UIUtils.createVBox(15, javafx.geometry.Pos.CENTER, nameField, capacityField,
                UIUtils.createButton("Добавить зал", 200, e -> addHallAction(primaryStage), false));
        vbox.setPadding(new javafx.geometry.Insets(15));
        vbox.getStyleClass().add("vbox");

        Scene scene = new Scene(vbox, 350, 220);
        scene.getStylesheets().add("/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addHallAction(Stage stage) {
        String name = nameField.getText().trim();

        if (!FieldValidator.validateTextField(nameField, "Название зала не может быть пустым", 3)) return;
        if (!FieldValidator.validatePositiveNumericField(capacityField, "Вместимость должна быть положительным числом")) return;

        int capacity = Integer.parseInt(capacityField.getText().trim());

        Hall hall = new Hall();
        hall.setName(name);
        hall.setCapacity(capacity);

        sendHallCommand("ADD", hall, stage);
    }
}
