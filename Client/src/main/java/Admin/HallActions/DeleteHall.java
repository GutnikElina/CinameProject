package Admin.HallActions;

import Admin.GeneralActions.HallActionBase;
import Utils.AppUtils;
import Utils.FieldValidator;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DeleteHall extends HallActionBase {

    @FXML
    private TextField idField;
    @FXML
    private Button backButton;

    @FXML
    private void deleteHallAction() {
        if (!FieldValidator.validateNumericField(idField, "ID зала должен быть числом.")) return;

        String command = "HALL;DELETE;" + idField.getText();
        Stage stage = (Stage) idField.getScene().getWindow();
        String response = AppUtils.sendToServer(command);

        if (response != null) {
            if (response.contains("HALL_DELETED")) {
                AppUtils.showAlert("Зал удален", "Удаление прошло успешно.", Alert.AlertType.INFORMATION);
                stage.close();
            } else if (response.contains("HALL_NOT_FOUND")) {
                AppUtils.showAlert("Ошибка", "Зал с таким ID не найден.", Alert.AlertType.ERROR);
                stage.close();
            } else {
                AppUtils.showAlert("Ошибка", "Не удалось удалить зал. " + response, Alert.AlertType.ERROR);
                stage.close();
            }
        }
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}

