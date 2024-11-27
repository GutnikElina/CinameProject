package Admin.SessionActions;

import Models.RequestDTO;
import Models.ResponseDTO;
import Utils.AppUtils;
import Utils.GsonFactory;
import Utils.UIUtils;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.Map;

public class DeleteSession {

    @FXML private TextField sessionIdField;
    @FXML private Button backButton;
    private final Gson gson = GsonFactory.create();

    @FXML
    private void deleteSessionAction() {
        try {
            String sessionIdStr = sessionIdField.getText();

            if (sessionIdStr == null || sessionIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("ID сеанса не должен быть пустым");
            }

            int sessionId = Integer.parseInt(sessionIdStr);

            RequestDTO requestDTO = new RequestDTO();
            requestDTO.setCommand("SESSION;DELETE");
            requestDTO.setData(Map.of("sessionId", String.valueOf(sessionId)));
            String jsonRequest = gson.toJson(requestDTO);

            String jsonResponse = AppUtils.sendJsonCommandToServer(jsonRequest);
            ResponseDTO response = gson.fromJson(jsonResponse, ResponseDTO.class);

            if ("SUCCESS".equals(response.getStatus())) {
                UIUtils.showAlert("Успех", response.getMessage(), Alert.AlertType.INFORMATION);
                handleBackButton();
            } else if ("SESSION_NOT_FOUND".equals(response.getStatus())) {
                UIUtils.showAlert("Ошибка", "Сеанс с таким ID не найден", Alert.AlertType.ERROR);
            } else {
                UIUtils.showAlert("Ошибка", response.getMessage(), Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            UIUtils.showAlert("Ошибка", "ID сеанса должен быть числом", Alert.AlertType.ERROR);
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось удалить сеанс: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) sessionIdField.getScene().getWindow();
        stage.close();
    }
}