package Elements.Form;

import Utils.FieldValidator;
import Utils.GsonFactory;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import MainApp.MainApp;
import Models.RequestDTO;
import com.google.gson.Gson;
import java.util.Map;

public class RegistrationForm {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button registerButton;
    @FXML private Button backButton;

    private final Gson gson = GsonFactory.create();

    @FXML
    private void initialize() {
        registerButton.setOnAction(e -> register());

        backButton.setOnAction(e -> {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
        });
    }

    private void register() {
        resetFieldStyles();

        boolean isValid = true;
        isValid &= FieldValidator.validateTextField(usernameField, "Логин должен содержать минимум 3 символа.", 3);
        isValid &= FieldValidator.validateTextField(passwordField, "Пароль должен содержать минимум 5 символов.", 5);

        if (isValid) {
            String username = usernameField.getText();
            String password = passwordField.getText();

            try {
                RequestDTO request = new RequestDTO();
                request.setCommand("REGISTER");
                request.setData(Map.of("username", username, "password", password));

                String jsonRequest = gson.toJson(request);
                MainApp.getInstance().getServerConnection().sendMessage(jsonRequest);

            } catch (Exception e) {
                UIUtils.showAlert("Ошибка", "Не удалось отправить запрос: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            UIUtils.showAlert("Ошибка", "Пожалуйста, проверьте введенные данные.", Alert.AlertType.ERROR);
        }
    }

    private void resetFieldStyles() {
        usernameField.setStyle("-fx-border-color: none;");
        passwordField.setStyle("-fx-border-color: none;");
    }
}
