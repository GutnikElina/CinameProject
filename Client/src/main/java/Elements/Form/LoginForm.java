package Elements.Form;

import MainApp.MainApp;
import Utils.AppUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginForm {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button backButton;

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

        MainApp.getInstance().getServerConnection()
                .sendMessage("LOGIN;" + username + ";" + password);
    }
}