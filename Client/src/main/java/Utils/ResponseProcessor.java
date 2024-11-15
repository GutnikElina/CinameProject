package Utils;

import Elements.Menu.AdminMenu;
import Elements.Menu.MainMenu;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class ResponseProcessor {

    public void handleResponse(String response) {
        handleResponse(response, null);
    }

    public void handleResponse(String response, Stage stage) {
        if (response == null) return;

        String[] parts = response.split(";");
        Platform.runLater(() -> {
            switch (parts[0]) {
                case "LOGIN_SUCCESS":
                    AdminMenu.show("token");
                    break;
                case "LOGIN_FAILED":
                    AppUtils.showAlert("Ошибка", "Неправильный логин или пароль.", Alert.AlertType.ERROR);
                    break;
                case "REGISTER_SUCCESS":
                    AppUtils.showAlert("Успешно", "Регистрация прошла успешно!", Alert.AlertType.INFORMATION);
                    if (stage != null) {
                        stage.close();
                    }
                    MainMenu.show(stage != null ? stage : new Stage());
                    break;
                case "ERROR":
                    AppUtils.showAlert("Ошибка", parts.length > 1 ? parts[1] : "Неизвестная ошибка", Alert.AlertType.ERROR);
                    break;
                case "USER_UPDATED":
                    AppUtils.showAlert("Успешно", "Пользователь успешно обновлен.", Alert.AlertType.INFORMATION);
                    break;
                case "USER_UPDATE_FAILED":
                    AppUtils.showAlert("Ошибка", "Не удалось обновить пользователя.", Alert.AlertType.ERROR);
                    break;
                default:
                    AppUtils.showAlert("Ошибка", "Неизвестный ответ от сервера: " + response, Alert.AlertType.ERROR);
            }
        });
    }
}