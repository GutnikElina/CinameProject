package Admin.GeneralActions;

import Models.Session;
import Models.RequestDTO;
import Models.ResponseDTO;
import Utils.AppUtils;
import Utils.GsonFactory;
import Utils.UIUtils;
import com.google.gson.Gson;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.util.Map;

public abstract class SessionActionBase {

    private final Gson gson = GsonFactory.create();

    protected void sendSessionCommand(String action, Session filmSession, Stage stage) {
        try {
            RequestDTO requestDTO = new RequestDTO();
            requestDTO.setCommand(action);
            requestDTO.setData(Map.of(
                    "startTime", filmSession.getStartTime().toString(),
                    "endTime", filmSession.getEndTime().toString(),
                    "movieId", String.valueOf(filmSession.getMovieId()),
                    "hallId", String.valueOf(filmSession.getHallId()),
                    "price", filmSession.getPrice().toPlainString()
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

    protected void sendSessionUpdateCommand(String action, Session filmSession, Stage stage) {
        try {
            RequestDTO requestDTO = new RequestDTO();
            requestDTO.setCommand(action);
            requestDTO.setData(Map.of(
                    "sessionId", filmSession.getId().toString(),
                    "startTime", filmSession.getStartTime().toString(),
                    "endTime", filmSession.getEndTime().toString(),
                    "movieId", String.valueOf(filmSession.getMovieId()),
                    "hallId", String.valueOf(filmSession.getHallId()),
                    "price", filmSession.getPrice().toPlainString()
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
