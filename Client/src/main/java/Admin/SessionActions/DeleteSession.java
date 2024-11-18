package Admin.SessionActions;

import Utils.AppUtils;
import Utils.UIUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class DeleteSession {

    @FXML
    private TextField sessionIdField;
    @FXML
    private Button backButton;

    @FXML
    private void deleteSessionAction() {
        try {
            String sessionIdStr = sessionIdField.getText();

            if (sessionIdStr == null || sessionIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("ID сеанса не должен быть пустым");
            }

            int sessionId = Integer.parseInt(sessionIdStr);

            String deleteSessionCommand = String.format("SESSION;DELETE;%d", sessionId);
            String response = AppUtils.sendToServer(deleteSessionCommand);

            if (response.startsWith("SUCCESS")) {
                UIUtils.showAlert("Успех", "Сеанс удален", Alert.AlertType.INFORMATION);
                closeStage();
            } else if (response.equals("SESSION_NOT_FOUND")) {
                UIUtils.showAlert("Ошибка", "Сеанс с таким ID не найден", Alert.AlertType.ERROR);
                closeStage();
            } else {
                UIUtils.showAlert("Ошибка", "Не удалось удалить сеанс: " + response, Alert.AlertType.ERROR);
                closeStage();
            }

        } catch (NumberFormatException e) {
            UIUtils.showAlert("Ошибка", "ID сеанса должен быть числом", Alert.AlertType.ERROR);
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось удалить сеанс: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) sessionIdField.getScene().getWindow();
        stage.close();
    }
}
