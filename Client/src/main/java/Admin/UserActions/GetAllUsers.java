package Admin.UserActions;

import Utils.GsonFactory;
import Utils.UIUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GetAllUsers {

    @FXML private ListView<String> listView;
    @FXML private Button backButton;
    private final Gson gson = GsonFactory.create();

    @FXML
    private void initialize() {
        fetchAllUsers();
        backButton.setOnAction(e -> handleBackButton());
    }

    private void fetchAllUsers() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            JsonObject requestData = new JsonObject();
            requestData.addProperty("command", "USER;GET_ALL;");
            String jsonRequest = gson.toJson(requestData);

            out.println(jsonRequest);

            String jsonResponse = in.readLine();
            if (jsonResponse == null) {
                throw new RuntimeException("Пустой ответ от сервера");
            }
            JsonObject responseMap = gson.fromJson(jsonResponse, JsonObject.class);

            if ("SUCCESS".equals(responseMap.get("status").getAsString())) {
                JsonObject dataObject = responseMap.getAsJsonObject("data");

                JsonArray usersData = dataObject.getAsJsonArray("data");

                updateListView(usersData);
            } else if ("ERROR".equals(responseMap.get("status").getAsString())) {
                String errorMessage = responseMap.get("message").getAsString();
                UIUtils.showAlert("Ошибка", errorMessage, Alert.AlertType.ERROR);
            } else {
                UIUtils.showAlert("Ошибка", "Неизвестный ответ от сервера.", Alert.AlertType.ERROR);
            }

        } catch (JsonSyntaxException e) {
            UIUtils.showAlert("Ошибка", "Ошибка обработки JSON: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось получить данные: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void updateListView(JsonArray usersData) {
        listView.getItems().clear();
        if (usersData.size() == 0) {
            listView.getItems().add("Нет доступных пользователей.");
        } else {
            for (int i = 0; i < usersData.size(); i++) {
                JsonObject user = usersData.get(i).getAsJsonObject();
                int userId = user.get("id").getAsInt();
                String username = user.get("username").getAsString();
                String role = user.get("role").getAsString();
                listView.getItems().add("ID: " + userId + " - " + username + " (" + role + ")");
            }
        }
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}