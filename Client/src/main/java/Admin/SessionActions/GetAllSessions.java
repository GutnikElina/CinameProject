package Admin.SessionActions;

import Models.RequestDTO;
import Models.ResponseDTO;
import Utils.AppUtils;
import Utils.GsonFactory;
import Utils.UIUtils;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GetAllSessions {
    @FXML private ListView<String> listView;
    @FXML private Button backButton;
    private final Gson gson = GsonFactory.create();
    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    private void initialize() {
        fetchAllSessions();
        backButton.setOnAction(e -> handleBackButton());
    }

    private void fetchAllSessions() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            RequestDTO request = new RequestDTO("SESSION;GET_ALL", Map.of());
            String jsonRequest = gson.toJson(request);

            out.println(jsonRequest);

            String response = in.readLine();
            if (response != null) {
                ResponseDTO responseDTO = gson.fromJson(response, ResponseDTO.class);

                if ("SUCCESS".equals(responseDTO.getStatus())) {
                    List<String> sessions = new ArrayList<>();
                    Map<String, Object> responseData = responseDTO.getData();

                    if (responseData != null && responseData.get("data") instanceof List) {
                        List<Map<String, Object>> sessionsData = (List<Map<String, Object>>) responseData.get("data");

                        for (Map<String, Object> sessionData : sessionsData) {
                            String formattedSession = formatSessionData(sessionData);
                            sessions.add(formattedSession);
                        }
                    }
                    updateListView(sessions);
                } else {
                    handleError(responseDTO.getMessage());
                }
            }

        } catch (Exception e) {
            handleError("Не удалось получить данные: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String formatSessionData(Map<String, Object> sessionData) {
        String startTime = (String) sessionData.getOrDefault("startTime", "Неизвестно");
        String endTime = (String) sessionData.getOrDefault("endTime", "Неизвестно");

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.forLanguageTag("ru"));
        String formattedStartTime = startTime.equals("Неизвестно") ? startTime : formatDateTime(startTime, inputFormatter, outputFormatter);
        String formattedEndTime = endTime.equals("Неизвестно") ? endTime : formatDateTime(endTime, inputFormatter, outputFormatter);

        Double price = (Double) sessionData.getOrDefault("price", 0.0);
        return String.format("ID: %s\nФильм: %s\nЗал: %s\nНачало: %s\nКонец: %s\nЦена: %.2f",
                sessionData.getOrDefault("id", "Неизвестно"),
                sessionData.getOrDefault("movieTitle", "Неизвестно"),
                sessionData.getOrDefault("hallName", "Неизвестно"),
                formattedStartTime,
                formattedEndTime,
                price);
    }

    private String formatDateTime(String dateTimeStr, DateTimeFormatter inputFormatter, DateTimeFormatter outputFormatter) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, inputFormatter);
            return dateTime.format(outputFormatter);
        } catch (DateTimeParseException e) {
            return "Неверный формат времени";
        }
    }

    private void updateListView(List<String> items) {
        listView.getItems().clear();
        if (items == null || items.isEmpty()) {
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
        UIUtils.showAlert("Ошибка", message, Alert.AlertType.ERROR);
    }
}
