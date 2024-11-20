package Admin.UserActions;

import Admin.GeneralActions.UserActionBase;
import Models.User;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class AddUser extends UserActionBase {
    @FXML
    private TextField usernameField, passwordField;
    @FXML
    private Button backButton;
    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private void initialize() {
        roleComboBox.getItems().addAll("guest", "admin", "employee");
    }

    @FXML
    private void addUserAction() {
        boolean valid = true;

        valid &= FieldValidator.validateTextField(usernameField, "Логин должен содержать не менее 3 символов.", 3);
        valid &= FieldValidator.validateTextField(passwordField, "Пароль должен содержать не менее 5 символов.", 5);

        if (roleComboBox.getValue() == null) {
            UIUtils.showAlert("Ошибка", "Пожалуйста, выберите роль пользователя.", Alert.AlertType.ERROR);
            valid = false;
        }

        if (!valid) return;

        User user = createUser(usernameField.getText(), passwordField.getText(), roleComboBox.getValue());
        Stage stage = (Stage) usernameField.getScene().getWindow();
        sendUserCommand(user, stage);
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
