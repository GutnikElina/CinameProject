package Admin.UserActions;

import Admin.GeneralActions.UserActionBase;
import Models.User;
import Utils.UIUtils;
import Utils.FieldValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.Map;

public class FindUser extends UserActionBase {

    @FXML private TextField userIdField;
    @FXML private Label usernameLabel, roleLabel, createdAtLabel;
    @FXML private Button backButton;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    private void fetchUserDetails() {
        if (!FieldValidator.validateNumericField(userIdField, "Введите корректный ID пользователя!")) {
            return;
        }

        try {
            int userId = Integer.parseInt(userIdField.getText());
            Map<String, Object> requestData = Map.of(
                    "command", "USER;GET;",
                    "data", Map.of("id", userId)
            );

            String response = sendJsonCommandToServer(requestData);

            if (response.startsWith("{")) {
                Map<String, Object> userData = objectMapper.readValue(response, Map.class);

                if (userData.containsKey("id") &&
                        userData.containsKey("username") &&
                        userData.containsKey("role") &&
                        userData.containsKey("createdAt")) {

                    User user = new User();
                    user.setId((Integer) userData.get("id"));
                    user.setUsername((String) userData.get("username"));
                    user.setRole((String) userData.get("role"));
                    user.setCreatedAt(LocalDateTime.parse((String) userData.get("createdAt")));

                    updateUserDetails(user);
                } else {
                    UIUtils.showAlert("Ошибка", "Некорректный формат ответа от сервера.", Alert.AlertType.ERROR);
                }
            } else {
                UIUtils.showAlert("Ошибка", "Некорректный ответ от сервера.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Ошибка при выполнении запроса: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void updateUserDetails(User user) {
        usernameLabel.setText(user.getUsername());
        roleLabel.setText(user.getRole());
        createdAtLabel.setText(user.getCreatedAt().toString());
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}