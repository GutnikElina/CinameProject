package Admin.GeneralActions;

import Models.Movie;
import Utils.AppUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public abstract class MovieActionBase {

    private final ObjectMapper objectMapper = new ObjectMapper();

    protected void sendMovieCommand(String action, Movie movie, Stage stage) {
        try {
            String movieJson = objectMapper.writeValueAsString(movie);
            String command = String.format("{\"command\":\"%s\", \"data\":%s}", action, movieJson);
            String response = AppUtils.sendToServer(command);

            if (response.contains("ERROR")) {
                AppUtils.showAlert("Ошибка", "Произошла ошибка при обработке фильма.", Alert.AlertType.ERROR);
            } else {
                AppUtils.showAlert("Успех", "Операция выполнена успешно.", Alert.AlertType.INFORMATION);
                if (stage != null) stage.close();
            }
        } catch (Exception e) {
            AppUtils.showAlert("Ошибка", "Не удалось отправить запрос на сервер: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
