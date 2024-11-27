package Admin.HallActions;

import Admin.GeneralActions.HallActionBase;
import Models.Hall;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddHall extends HallActionBase {

    @FXML public Button addButton;
    @FXML private TextField hallField;
    @FXML private TextField capacityField;

    @FXML
    private void addHallAction() {
        String name = hallField.getText().trim();

        if (!FieldValidator.validateTextField(hallField, "Название зала должно быть не меньше двух символов", 2)) return;
        if (!FieldValidator.validatePositiveNumericField(capacityField, "Вместимость должна быть положительным числом")) return;

        int capacity = Integer.parseInt(capacityField.getText().trim());

        Hall hall = new Hall();
        hall.setName(name);
        hall.setCapacity(capacity);
        Stage stage = (Stage) addButton.getScene().getWindow();
        sendHallCommand("HALL;ADD", hall, stage);
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
    }
}
