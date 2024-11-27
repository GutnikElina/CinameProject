package Admin.GeneralActions;

import Models.User;
import Utils.AppUtils;
import Utils.GsonFactory;
import Utils.UIUtils;
import com.google.gson.Gson;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public abstract class UserActionBase {

    private final Gson gson = GsonFactory.create();

    protected void sendUserCommand(User user, Stage stage) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("command", "USER;ADD;");
        requestData.put("data", Map.of(
                "username", user.getUsername(),
                "password", user.getPassword(),
                "role", user.getRole()
        ));
        sendCommandToServer(requestData, stage);
    }

    protected void sendUserUpdateCommand(User user, Stage stage) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("command", "USER;UPDATE;");
        requestData.put("data", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "password", user.getPassword(),
                "role", user.getRole()
        ));
        sendCommandToServer(requestData, stage);
    }

    protected void sendCommandToServer(Map<String, Object> requestData, Stage stage) {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String jsonRequest = gson.toJson(requestData);
            System.out.println(jsonRequest);
            out.println(jsonRequest);
            String response = in.readLine();

            handleServerResponse(response, stage);
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Ошибка при соединении с сервером: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void handleServerResponse(String response, Stage stage) {
        System.out.println(response);
        if (response.contains("USER_ADDED")) {
            UIUtils.showAlert("Успешная операция", "Пользователь успешно добавлен.", Alert.AlertType.INFORMATION);
            stage.close();
        } else if (response.contains("USER_UPDATED")) {
            UIUtils.showAlert("Успешная операция", "Пользователь успешно обновлен.", Alert.AlertType.INFORMATION);
            stage.close();
        } else if (response.contains("USER_DELETED")) {
            UIUtils.showAlert("Успешная операция", "Пользователь успешно удален.", Alert.AlertType.INFORMATION);
            stage.close();
        } else if (response.contains("USER_NOT_FOUND")) {
            UIUtils.showAlert("Ошибка", "Пользователь с таким ID не найден.", Alert.AlertType.ERROR);
        } else if (response.contains("ERROR")) {
            String[] responseParts = response.split(";", 2);
            if (responseParts.length > 1) {
                UIUtils.showAlert("Ошибка", responseParts[1], Alert.AlertType.ERROR);
            } else {
                UIUtils.showAlert("Ошибка", "Неизвестная ошибка", Alert.AlertType.ERROR);
            }
        } else {
            UIUtils.showAlert("Ошибка", "Неизвестный ответ от сервера.", Alert.AlertType.ERROR);
        }
    }

    protected User createUser(String username, String password, String role) {
        return new User(0, username, password, role, null, null);
    }
}