package Admin.SessionActions;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GetAllSessions {
    @FXML
    private ListView<String> listView;
    @FXML
    private Button backButton;

    private String command = "SESSION;GET_ALL;";

    @FXML
    private void initialize() {
        fetchAllItems();
        backButton.setOnAction(e -> handleBackButton());
    }

    private void fetchAllItems() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(command);

            List<String> items = new ArrayList<>();
            String response;
            while ((response = in.readLine()) != null) {
                if (response.equals("END_OF_SESSIONS")) {
                    break;
                }
                if (isValidResponse(response)) {
                    items.add(formatItemResponse(response));
                } else if (response.startsWith("ERROR;")) {
                    handleError("Не удалось получить данные.");
                }
            }
            updateListView(items);

        } catch (IOException e) {
            handleError("Не удалось получить данные.");
        }
    }

    private boolean isValidResponse(String response) {
        return response.startsWith("SESSION_FOUND;");
    }

    private String formatItemResponse(String response) {
        String[] sessionData = response.split(";");
        return String.format("ID: %s\nФильм: %s\nЗал: %s\nНачало: %s\nКонец: %s",
                sessionData[1], sessionData[2], sessionData[3],
                formatDateTime(sessionData[4]), formatDateTime(sessionData[5]));
    }

    private String formatDateTime(String dateTime) {
        try {
            LocalDateTime parsedDateTime = LocalDateTime.parse(dateTime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");
            return parsedDateTime.format(formatter);
        } catch (Exception e) {
            return dateTime;
        }
    }

    private void updateListView(List<String> items) {
        listView.getItems().clear();
        if (items.isEmpty()) {
            listView.getItems().add("Нет доступных сеансов.");
        } else {
            listView.getItems().addAll(items);
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