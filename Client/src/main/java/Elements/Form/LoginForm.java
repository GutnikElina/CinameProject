package Elements.Form;

import MainApp.MainApp;
import Utils.GsonFactory;
import Utils.UIUtils;
import com.google.gson.Gson;
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
    private final Gson gson = GsonFactory.create();

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
            UIUtils.showAlert("Ошибка", "Логин и пароль не должны быть пустыми.", Alert.AlertType.ERROR);
            return;
        }

        try {
            RequestDTO request = new RequestDTO();
            request.setCommand("LOGIN");
            request.setData(Map.of("username", username, "password", password));

            String jsonRequest = gson.toJson(request);

            MainApp.getInstance().getServerConnection().sendMessage(jsonRequest);
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось отправить запрос: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}