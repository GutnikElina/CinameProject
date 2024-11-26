package Admin.GeneralActions;

import Models.Movie;
import Models.RequestDTO;
import Models.ResponseDTO;
import Utils.AppUtils;
import Utils.GsonFactory;
import Utils.UIUtils;
import com.google.gson.Gson;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.util.Map;

public abstract class MovieActionBase {

    private final Gson gson = GsonFactory.create();

    protected void sendMovieCommand(String action, Movie movie, Stage stage) {
        try {
            RequestDTO requestDTO = new RequestDTO();
            requestDTO.setCommand(action);
            requestDTO.setData(Map.of(
                    "title", movie.getTitle(),
                    "genre", movie.getGenre(),
                    "duration", String.valueOf(movie.getDuration()),
                    "releaseDate", movie.getReleaseDate(),
                    "description", movie.getDescription()
            ));
            String requestJson = gson.toJson(requestDTO);
            String responseJson = AppUtils.sendJsonCommandToServer(requestJson);
            ResponseDTO response = gson.fromJson(responseJson, ResponseDTO.class);

            if ("ERROR".equals(response.getStatus())) {
                UIUtils.showAlert("Ошибка", response.getMessage(), Alert.AlertType.ERROR);
            } else {
                UIUtils.showAlert("Успешное выполнение", response.getMessage(), Alert.AlertType.INFORMATION);
                if (stage != null) stage.close();
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось отправить запрос на сервер: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
