package Admin.UserActions;

import Admin.GeneralActions.UserActionBase;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class DeleteUser extends UserActionBase {

    @FXML
    private TextField idField;
    @FXML
    private Button backButton;

    @FXML
    private void deleteUserAction() {
        if (!FieldValidator.validateNumericField(idField, "ID пользователя должен быть числом.")) return;

        String command = "USER;DELETE;" + idField.getText();
        Stage stage = (Stage) idField.getScene().getWindow();
        sendCommandToServer(command, stage);
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
