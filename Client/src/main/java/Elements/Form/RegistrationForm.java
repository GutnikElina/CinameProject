package Elements.Form;

import Utils.AppUtils;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import MainApp.MainApp;

public class RegistrationForm {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button registerButton;
    @FXML
    private Button backButton;

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

            MainApp.getInstance().getServerConnection().sendMessage("REGISTER;" + username + ";" + password);
        } else {
            AppUtils.showAlert("Ошибка", "Пожалуйста, проверьте введенные данные.", Alert.AlertType.ERROR);
        }
    }

    private void resetFieldStyles() {
        usernameField.setStyle("-fx-border-color: none;");
        passwordField.setStyle("-fx-border-color: none;");
    }
}