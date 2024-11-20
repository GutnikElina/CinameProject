package Admin.HallActions;

import Admin.GeneralActions.HallActionBase;
import Utils.AppUtils;
import Utils.FieldValidator;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeleteHall extends HallActionBase {
    @FXML private TextField idField;
    @FXML private Button backButton;

    @FXML
    private void deleteHallAction() {
        if (!FieldValidator.validateNumericField(idField, "ID зала должен быть числом.")) return;

        try {
            String command = createDeleteCommand(idField.getText());
            String response = AppUtils.sendToServer(command);

            if (response != null) {
                if (response.contains("HALL_DELETED")) {
                    AppUtils.showAlert("Зал удален", "Удаление прошло успешно.", Alert.AlertType.INFORMATION);
                } else if (response.contains("HALL_NOT_FOUND")) {
                    AppUtils.showAlert("Ошибка", "Зал с таким ID не найден.", Alert.AlertType.ERROR);
                } else {
                    AppUtils.showAlert("Ошибка", "Не удалось удалить зал. " + response, Alert.AlertType.ERROR);
                }
                Stage stage = (Stage) idField.getScene().getWindow();
                stage.close();
            }
        } catch (Exception e) {
            AppUtils.showAlert("Ошибка", "Не удалось отправить запрос на сервер: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private String createDeleteCommand(String hallId) throws Exception {
        return String.format("{\"command\":\"HALL;DELETE\",\"data\":{\"id\":\"%s\"}}", hallId);
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}