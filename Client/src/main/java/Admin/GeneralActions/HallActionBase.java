package Admin.GeneralActions;

import Models.Hall;
import Utils.AppUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public abstract class HallActionBase {

    private final ObjectMapper objectMapper = new ObjectMapper();

    protected void sendHallCommand(String action, Hall hall, Stage stage) {
        try {
            String hallJson = objectMapper.writeValueAsString(hall);
            String command = String.format("{\"command\":\"%s\", \"data\":%s}", action, hallJson);
            String response = AppUtils.sendToServer(command);

            if (response.contains("ERROR")) {
                AppUtils.showAlert("Ошибка", "Произошла ошибка.", Alert.AlertType.ERROR);
            } else {
                AppUtils.showAlert("Успешная операция", "Операция выполнена успешно.", Alert.AlertType.INFORMATION);
                if (stage != null) stage.close();
            }
        } catch (Exception e) {
            AppUtils.showAlert("Ошибка", "Не удалось отправить запрос на сервер: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
