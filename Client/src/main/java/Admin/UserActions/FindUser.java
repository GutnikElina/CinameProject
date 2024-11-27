package Admin.UserActions;

import Admin.GeneralActions.UserActionBase;
import Models.User;
import Utils.AppUtils;
import Utils.GsonFactory;
import Utils.UIUtils;
import Utils.FieldValidator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class FindUser extends UserActionBase {

    @FXML private TextField userIdField;
    @FXML private Label usernameLabel, roleLabel, createdAtLabel;
    @FXML private Button backButton;
    private final Gson gson = GsonFactory.create();

    @FXML
    private void fetchUserDetails() {
        if (!FieldValidator.validateNumericField(userIdField, "Введите корректный ID пользователя!")) {
            return;
        }
        try {
            int userId = Integer.parseInt(userIdField.getText());
            Map<String, Object> requestData = Map.of(
                    "command", "USER;GET;",
                    "data", Map.of("id", String.valueOf(userId))
            );

            String jsonResponse = AppUtils.sendJsonCommandToServer(gson.toJson(requestData));
            if (jsonResponse != null && jsonResponse.startsWith("{")) {
                JsonObject userData = gson.fromJson(jsonResponse, JsonObject.class);

                if (userData.has("id") &&
                        userData.has("username") &&
                        userData.has("role") &&
                        userData.has("createdAt")) {

                    User user = new User();
                    user.setId(userData.get("id").getAsInt());
                    user.setUsername(userData.get("username").getAsString());
                    user.setRole(userData.get("role").getAsString());
                    user.setCreatedAt(LocalDateTime.parse(userData.get("createdAt").getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                    updateUserDetails(user);
                } else {
                    UIUtils.showAlert("Ошибка", "Некорректный формат ответа от сервера.", Alert.AlertType.ERROR);
                }
            } else {
                UIUtils.showAlert("Ошибка", "Некорректный ответ от сервера.", Alert.AlertType.ERROR);
            }
        } catch (JsonSyntaxException e) {
            UIUtils.showAlert("Ошибка", "Ошибка обработки JSON: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Ошибка при выполнении запроса: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void updateUserDetails(User user) {
        usernameLabel.setText(user.getUsername());
        roleLabel.setText(user.getRole());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", new Locale("ru", "RU"));
        String formattedDate = user.getCreatedAt().format(formatter);
        createdAtLabel.setText(formattedDate);
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}