package Elements.Form;

import MainApp.MainApp;
import Utils.AppUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Models.RequestDTO;

import java.util.Map;

public class LoginForm {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button backButton;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    private void initialize() {
        loginButton.setOnAction(e -> login());

        backButton.setOnAction(e -> {
            Stage stage = (Stage) backButton.getScene().getWindow();
            MainApp.getInstance().showMainMenu(stage);
        });
    }

    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            AppUtils.showAlert("Ошибка", "Логин и пароль не должны быть пустыми.", Alert.AlertType.ERROR);
            return;
        }

        try {
            RequestDTO request = new RequestDTO();
            request.setCommand("LOGIN");
            request.setData(Map.of("username", username, "password", password));

            String jsonRequest = objectMapper.writeValueAsString(request);

            MainApp.getInstance().getServerConnection().sendMessage(jsonRequest);
        } catch (Exception e) {
            AppUtils.showAlert("Ошибка", "Не удалось отправить запрос: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
