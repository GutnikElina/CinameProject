package Admin.UserActions;

import Admin.GeneralActions.UserActionBase;
import Models.ResponseDTO;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class DeleteUser extends UserActionBase {

    @FXML private TextField idField;
    @FXML private Button backButton;

    private final Gson gson = new Gson();

    @FXML
    private void deleteUserAction() {
        if (!FieldValidator.validatePositiveNumericField(idField, "ID пользователя должен быть положительным числом.")) {
            return;
        }

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            int userId = Integer.parseInt(idField.getText());

            Map<String, Object> requestData = Map.of(
                    "command", "USER;DELETE",
                    "data", Map.of("id", userId)
            );

            String jsonRequest = gson.toJson(requestData);
            out.println(jsonRequest);

            String responseJson = in.readLine();
            ResponseDTO response = gson.fromJson(responseJson, ResponseDTO.class);
            switch (response.getStatus()) {
                case "USER_DELETED":
                    UIUtils.showAlert("Пользователь удален", "Пользователь был успешно удален.", Alert.AlertType.INFORMATION);
                    handleBackButton();
                    break;
                case "ERROR":
                    if ("USER_NOT_FOUND".equals(response.getMessage())) {
                        UIUtils.showAlert("Ошибка", "Пользователь с таким ID не найден.", Alert.AlertType.ERROR);
                        handleBackButton();
                    } else {
                        UIUtils.showAlert("Ошибка", "Ошибка: " + response.getMessage(), Alert.AlertType.ERROR);
                    }
                    break;
                default:
                    UIUtils.showAlert("Ошибка", "Неизвестный статус ответа.", Alert.AlertType.ERROR);
                    break;
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Ошибка при соединении с сервером: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }
}
