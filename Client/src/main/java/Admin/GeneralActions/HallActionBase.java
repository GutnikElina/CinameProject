package Admin.GeneralActions;

import Models.Hall;
import Utils.AppUtils;
import Utils.GsonFactory;
import Utils.UIUtils;
import com.google.gson.Gson;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public abstract class HallActionBase {

    private final Gson gson = GsonFactory.create();

    protected void sendHallCommand(String action, Hall hall, Stage stage) {
        try {
            String hallJson = gson.toJson(hall);
            String command = String.format("{\"command\":\"%s\", \"data\":%s}", action, hallJson);
            String response = AppUtils.sendJsonCommandToServer(command);

            if (response.contains("ERROR")) {
                UIUtils.showAlert("Ошибка", "Произошла ошибка.", Alert.AlertType.ERROR);
            } else {
                UIUtils.showAlert("Успешная операция", "Операция выполнена успешно.", Alert.AlertType.INFORMATION);
                if (stage != null) stage.close();
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось отправить запрос на сервер: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
