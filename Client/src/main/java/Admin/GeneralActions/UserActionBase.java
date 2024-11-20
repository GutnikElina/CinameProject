package Admin.GeneralActions;

import Models.User;
import Utils.AppUtils;
import Utils.UIUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public abstract class UserActionBase {

    private final ObjectMapper objectMapper = new ObjectMapper();
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

            String jsonRequest = objectMapper.writeValueAsString(requestData);
            out.println(jsonRequest);
            String response = in.readLine();

            if ("USER_ADDED".equals(response)) {
                UIUtils.showAlert("Успешная операция", "Пользователь успешно добавлен.", Alert.AlertType.INFORMATION);
                stage.close();
            } else if ("USER_UPDATED".equals(response)) {
                UIUtils.showAlert("Успешная операция", "Пользователь успешно обновлен.", Alert.AlertType.INFORMATION);
                stage.close();
            } else if ("USER_NOT_FOUND".equals(response)) {
                UIUtils.showAlert("Ошибка", "Пользователь с таким ID не найден.", Alert.AlertType.ERROR);
            } else if (response.startsWith("ERROR")) {
                UIUtils.showAlert("Ошибка", response.split(";", 2)[1], Alert.AlertType.ERROR);
            } else {
                UIUtils.showAlert("Ошибка", "Неизвестный ответ от сервера.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Ошибка при соединении с сервером: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    protected String sendJsonCommandToServer(Map<String, Object> requestData) throws Exception {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String jsonRequest = objectMapper.writeValueAsString(requestData);
            out.println(jsonRequest);

            String jsonResponse = in.readLine();
            return jsonResponse;
        }
    }

    protected User createUser(String username, String password, String role) {
        return new User(0, username, password, role, LocalDateTime.now(), null);
    }
}
