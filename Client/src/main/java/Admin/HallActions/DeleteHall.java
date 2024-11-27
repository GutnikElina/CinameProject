package Admin.HallActions;

import Admin.GeneralActions.HallActionBase;
import Utils.AppUtils;
import Utils.FieldValidator;
import Utils.GsonFactory;
import Utils.UIUtils;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Map;

public class DeleteHall extends HallActionBase {
    @FXML private TextField idField;
    @FXML private Button backButton;

    private final Gson gson = GsonFactory.create();

    @FXML
    private void deleteHallAction() {
        if (!FieldValidator.validateNumericField(idField, "ID зала должен быть числом.")) return;

        try {
            String command = createDeleteCommand(idField.getText());
            String response = AppUtils.sendJsonCommandToServer(command);

            if (response != null) {
                if (response.contains("HALL_DELETED")) {
                    UIUtils.showAlert("Зал удален", "Удаление прошло успешно.", Alert.AlertType.INFORMATION);
                } else if (response.contains("HALL_NOT_FOUND")) {
                    UIUtils.showAlert("Ошибка", "Зал с таким ID не найден.", Alert.AlertType.ERROR);
                } else {
                    UIUtils.showAlert("Ошибка", "Не удалось удалить зал. " + response, Alert.AlertType.ERROR);
                }
                Stage stage = (Stage) idField.getScene().getWindow();
                stage.close();
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось отправить запрос на сервер: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private String createDeleteCommand(String hallId) {
        return gson.toJson(Map.of(
                "command", "HALL;DELETE",
                "data", Map.of("id", hallId)
        ));
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
