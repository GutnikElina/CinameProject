package Admin.SessionActions;

import Utils.AppUtils;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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

public class GetAllSessions extends Application {

    private ListView<String> sessionsListView;
    private Button backButton;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Все сеансы");

        sessionsListView = new ListView<>();
        sessionsListView.setPrefHeight(300);

        backButton = new Button("Назад");

        backButton.setAlignment(Pos.CENTER);
        backButton.setOnAction(e -> primaryStage.close());

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(sessionsListView, backButton);

        fetchAllSessions();

        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fetchAllSessions() {
        String message = "SESSION;GET_ALL;";

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(message);

            String response;
            List<String> sessions = new ArrayList<>();
            while ((response = in.readLine()) != null) {
                if (response.equals("END_OF_SESSIONS")) {
                    break;
                }
                if (response.startsWith("SESSION_FOUND;")) {
                    String[] sessionData = response.split(";");
                    String movieTitle = sessionData[2];
                    String hallName = sessionData[3];
                    String startTime = formatDateTime(sessionData[4]);
                    String endTime = formatDateTime(sessionData[5]);

                    sessions.add(String.format("Фильм: %s\nЗал: %s\nНачало: %s\nКонец: %s",
                            movieTitle, hallName, startTime, endTime));
                } else if (response.startsWith("ERROR;")) {
                    AppUtils.showAlert("Ошибка", "Не удалось получить данные о сеансах.", Alert.AlertType.ERROR);
                    break;
                }
            }
            updateSessionsListView(sessions);

        } catch (IOException e) {
            AppUtils.showAlert("Ошибка", "Не удалось получить данные о сеансах.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
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

    private void updateSessionsListView(List<String> sessions) {
        sessionsListView.getItems().clear();
        if (sessions.isEmpty()) {
            sessionsListView.getItems().add("Нет сеансов.");
        } else {
            sessionsListView.getItems().addAll(sessions);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
