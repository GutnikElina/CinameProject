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

    @FXML
    private TextField hallField;

    @FXML
    private TextField capacityField;

    @FXML
    private Button backButton;

    @FXML
    private void addHallAction() {
        String name = hallField.getText().trim();

        if (!FieldValidator.validateTextField(hallField, "Название зала должно быть не меньше двух символов", 2)) return;
        if (!FieldValidator.validatePositiveNumericField(capacityField, "Вместимость должна быть положительным числом")) return;

        int capacity = Integer.parseInt(capacityField.getText().trim());

        Hall hall = new Hall();
        hall.setName(name);
        hall.setCapacity(capacity);

        sendHallCommand("ADD", hall, null);
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
