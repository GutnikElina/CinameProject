package Utils;

import Elements.Menu.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class ResponseProcessor {

    public void handleResponse(String response) {
        if (response == null) return;

        String[] parts = response.split(";");
        Platform.runLater(() -> {
            switch (parts[0]) {
                case "LOGIN_SUCCESS":
                    handleLoginSuccess(parts);
                    break;
                case "LOGIN_FAILED":
                    AppUtils.showAlert("Ошибка", "Неправильный логин или пароль.", Alert.AlertType.ERROR);
                    break;
                case "REGISTER_SUCCESS":
                    AppUtils.showAlert("Успешно", "Регистрация прошла успешно!", Alert.AlertType.INFORMATION);
                    MainMenu.show(new Stage());
                    break;
                case "ERROR":
                    AppUtils.showAlert("Ошибка", parts.length > 1 ? parts[1] : "Неизвестная ошибка", Alert.AlertType.ERROR);
                    break;
                default:
                    AppUtils.showAlert("Ошибка", "Неизвестный ответ от сервера: " + response, Alert.AlertType.ERROR);
            }
        });
    }

    private void handleLoginSuccess(String[] parts) {
        String token = parts[1];
        String role = parts[2];
        switch (role) {
            case "admin":
                AdminMenu.show(token);
                break;
            case "guest":
                UserMenu.show(token);
                break;
            case "employee":
                EmployeeMenu.show(token);
                break;
            default:
                AppUtils.showAlert("Ошибка", "Неизвестная роль", Alert.AlertType.ERROR);
        }
    }
}
