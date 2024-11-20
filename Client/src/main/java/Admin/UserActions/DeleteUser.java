package Admin.UserActions;

import Admin.GeneralActions.UserActionBase;
import Utils.FieldValidator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class DeleteUser extends UserActionBase {

    @FXML private TextField idField;
    @FXML private Button backButton;

    @FXML
    private void deleteUserAction() {
        if (!FieldValidator.validateNumericField(idField, "ID пользователя должен быть числом.")) return;

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("command", "USER;DELETE;");
        requestData.put("data", Map.of("id", Integer.parseInt(idField.getText())));

        Stage stage = (Stage) idField.getScene().getWindow();
        sendCommandToServer(requestData, stage);
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}