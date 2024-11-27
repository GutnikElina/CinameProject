package Utils;

import Elements.Menu.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import Models.ResponseDTO;
import com.google.gson.Gson;

public class ResponseProcessor {

    private final Gson gson = new Gson();
    public void handleResponse(String response) {
        if (response == null) return;
        try {
            ResponseDTO responseDTO = gson.fromJson(response, ResponseDTO.class);

            Platform.runLater(() -> {
                switch (responseDTO.getStatus()) {
                    case "LOGIN_SUCCESS":
                        handleLoginSuccess(responseDTO);
                        break;
                    case "LOGIN_FAILED":
                        UIUtils.showAlert("Ошибка", "Неправильный логин или пароль.", Alert.AlertType.ERROR);
                        break;
                    case "REGISTER_SUCCESS":
                        UIUtils.showAlert("Успешно", "Регистрация прошла успешно!", Alert.AlertType.INFORMATION);
                        MainMenu.show(new Stage());
                        break;
                    case "ERROR":
                        UIUtils.showAlert("Ошибка", responseDTO.getMessage() != null ? responseDTO.getMessage() : "Неизвестная ошибка", Alert.AlertType.ERROR);
                        break;
                    default:
                        UIUtils.showAlert("Ошибка", "Неизвестный ответ от сервера: " + response, Alert.AlertType.ERROR);
                }
            });
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось обработать ответ от сервера: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleLoginSuccess(ResponseDTO responseDTO) {
        String token = (String) responseDTO.getData().get("token");
        String role = (String) responseDTO.getData().get("userRole");

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
                UIUtils.showAlert("Ошибка", "Неизвестная роль", Alert.AlertType.ERROR);
        }
    }
}