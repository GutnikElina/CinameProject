package Admin.UserActions;

import Admin.GeneralActions.UserActionBase;
import Models.User;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateUser extends UserActionBase {
    @FXML
    public Button updateButton;
    @FXML
    private TextField idField, usernameField, passwordField;
    @FXML
    private Button backButton;
    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private void updateUserAction() {
        boolean valid = true;

        valid &= FieldValidator.validateNumericField(idField, "Пожалуйста, введите корректный ID пользователя.");
        valid &= FieldValidator.validateTextField(usernameField, "Логин пользователя должно быть не короче 3 символов.", 3);
        valid &= FieldValidator.validateTextField(passwordField, "Пароль должен содержать хотя бы 5 символов.", 5);

        if (roleComboBox.getValue() == null) {
            UIUtils.showAlert("Ошибка", "Пожалуйста, выберите роль пользователя.", Alert.AlertType.ERROR);
            valid = false;
        }

        Stage stage = (Stage) updateButton.getScene().getWindow();
        if (valid) {
            User user = createUserFromInput();
            sendUserUpdateCommand(user, stage);
        }
    }

    private User createUserFromInput() {
        User user = new User();
        user.setId(Integer.parseInt(idField.getText()));
        user.setUsername(usernameField.getText());
        user.setPassword(passwordField.getText());
        user.setRole(roleComboBox.getValue());
        return user;
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
