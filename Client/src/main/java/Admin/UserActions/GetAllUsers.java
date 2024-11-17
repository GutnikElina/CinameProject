package Admin.UserActions;

import Utils.AppUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GetAllUsers {
    @FXML
    private ListView<String> listView;
    @FXML
    private Button backButton;

    private String command = "USER;GET_ALL;";

    @FXML
    private void initialize() {
        fetchAllUsers();
        backButton.setOnAction(e -> handleBackButton());
    }

    private void fetchAllUsers() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(command);

            List<String> users = new ArrayList<>();
            String response;
            while ((response = in.readLine()) != null) {
                if (response.equals("END_OF_USERS")) {
                    break;
                }
                if (isValidResponse(response)) {
                    users.add(formatUserResponse(response));
                } else if (response.startsWith("ERROR;")) {
                    handleError("Не удалось получить данные.");
                    break;
                }
            }
            updateListView(users);

        } catch (IOException e) {
            handleError("Не удалось получить данные.");
        }
    }

    private boolean isValidResponse(String response) {
        return response.startsWith("USER_FOUND;");
    }

    private String formatUserResponse(String response) {
        String[] userData = response.split(";");
        int userId = Integer.parseInt(userData[1]);
        String username = userData[2];
        String role = userData[3];
        return "ID: " + userId + " - " + username + " (" + role + ")";
    }

    private void updateListView(List<String> users) {
        listView.getItems().clear();
        if (users.isEmpty()) {
            listView.getItems().add("Нет доступных пользователей.");
        } else {
            listView.getItems().addAll(users);
        }
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    private void handleError(String message) {
        AppUtils.showAlert("Ошибка", message, Alert.AlertType.ERROR);
    }
}
