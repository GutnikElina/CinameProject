package Admin.UserActions;

import Admin.GeneralActions.UserActionBase;
import Models.User;
import Utils.AppUtils;
import Utils.UIUtils;
import Utils.FieldValidator;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class FindUser extends UserActionBase {

    @FXML
    private TextField userIdField;
    @FXML
    private Label usernameLabel, roleLabel, createdAtLabel;
    @FXML
    private Button backButton;

    @FXML
    private void fetchUserDetails() {
        if (!FieldValidator.validateNumericField(userIdField, "Введите корректный ID пользователя!")) {
            return;
        }

        int userId = Integer.parseInt(userIdField.getText());
        String response = AppUtils.sendToServer("USER;GET;" + userId);

        if (response.startsWith("USER_FOUND;")) {
            String[] userData = response.split(";");
            User user = new User();
            user.setId(Integer.parseInt(userData[1]));
            user.setUsername(userData[2]);
            user.setRole(userData[3]);
            user.setCreatedAt(LocalDateTime.parse(userData[4]));
            updateUserDetails(user);
        } else {
            UIUtils.showAlert("Ошибка", "Пользователь не найден.", Alert.AlertType.INFORMATION);
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
