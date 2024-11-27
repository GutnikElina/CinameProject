package Admin.HallActions;

import Admin.GeneralActions.HallActionBase;
import Models.Hall;
import Models.RequestDTO;
import Models.ResponseDTO;
import Utils.AppUtils;
import Utils.GsonFactory;
import Utils.UIUtils;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Map;

public class FindHall extends HallActionBase {
    @FXML private TextField hallIdField;
    @FXML private Label nameLabel, capacityLabel;
    @FXML private Button backButton;
    private final Gson gson = GsonFactory.create();

    @FXML
    private void fetchHallDetails() {
        try {
            int hallId = Integer.parseInt(hallIdField.getText());
            sendHallFindRequest(hallId);
        } catch (NumberFormatException e) {
            UIUtils.showAlert("Ошибка", "Введите корректный ID зала!", Alert.AlertType.INFORMATION);
        }
    }

    private void sendHallFindRequest(int hallId) {
        try {
            RequestDTO request = new RequestDTO("HALL;GET", Map.of("id", String.valueOf(hallId)));
            String jsonRequest = gson.toJson(request);
            String response = AppUtils.sendJsonCommandToServer(jsonRequest);
            ResponseDTO responseDTO = gson.fromJson(response, ResponseDTO.class);

            if ("HALL_FOUND".equals(responseDTO.getStatus())) {
                Hall hall = gson.fromJson(gson.toJson(responseDTO.getData().get("hall")), Hall.class);
                updateHallDetails(hall);
            } else {
                UIUtils.showAlert("Ошибка", "Зал не найден.", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось отправить запрос на сервер: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateHallDetails(Hall hall) {
        nameLabel.setText(hall.getName());
        capacityLabel.setText(String.valueOf(hall.getCapacity()));
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
