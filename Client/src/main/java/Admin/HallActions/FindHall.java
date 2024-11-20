package Admin.HallActions;

import Admin.GeneralActions.HallActionBase;
import Models.Hall;
import Utils.AppUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Alert;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import Models.RequestDTO;
import Models.ResponseDTO;
import java.util.Map;

public class FindHall extends HallActionBase {
    @FXML private TextField hallIdField;
    @FXML private Label nameLabel, capacityLabel;
    @FXML private Button backButton;

    @FXML
    private void fetchHallDetails() {
        try {
            int hallId = Integer.parseInt(hallIdField.getText());
            sendHallFindRequest(hallId);
        } catch (NumberFormatException e) {
            AppUtils.showAlert("Ошибка", "Введите корректный ID зала!", Alert.AlertType.INFORMATION);
        }
    }

    private void sendHallFindRequest(int hallId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RequestDTO request = new RequestDTO();
            request.setCommand("HALL;GET");
            request.setData(Map.of("id", String.valueOf(hallId)));

            String jsonRequest = objectMapper.writeValueAsString(request);

            String response = AppUtils.sendToServer(jsonRequest);

            if (response.contains("ERROR")) {
                AppUtils.showAlert("Ошибка", "Зал не найден.", Alert.AlertType.INFORMATION);
            } else {
                ResponseDTO responseDTO = objectMapper.readValue(response, ResponseDTO.class);
                if ("HALL_FOUND".equals(responseDTO.getStatus())) {
                    Map<String, Object> data = responseDTO.getData();
                    Hall hall = new Hall();
                    hall.setId((Integer) data.get("id"));
                    hall.setName((String) data.get("name"));
                    hall.setCapacity((Integer) data.get("capacity"));
                    updateHallDetails(hall);
                } else {
                    AppUtils.showAlert("Ошибка", responseDTO.getMessage(), Alert.AlertType.INFORMATION);
                }
            }
        } catch (Exception e) {
            AppUtils.showAlert("Ошибка", "Не удалось отправить запрос на сервер: " + e.getMessage(), Alert.AlertType.ERROR);
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
