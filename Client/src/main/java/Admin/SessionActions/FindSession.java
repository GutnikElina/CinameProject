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

public class FindSession extends Application {

    private TextField idField;
    private Label movieLabel, hallLabel, startTimeLabel, endTimeLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Поиск сеанса по ID");

        idField = new TextField();
        idField.setPromptText("Введите ID сеанса");
        idField.setPrefWidth(300);

        Button searchButton = new Button("Найти");
        searchButton.setOnAction(e -> searchSessionById());

        HBox inputPanel = new HBox(10, new Label("ID сеанса:"), idField, searchButton);
        inputPanel.setAlignment(Pos.CENTER);
        inputPanel.setPadding(new Insets(10));

        movieLabel = createInfoLabel("");
        hallLabel = createInfoLabel("");
        startTimeLabel = createInfoLabel("");
        endTimeLabel = createInfoLabel("");

        GridPane sessionInfoPane = new GridPane();
        sessionInfoPane.setHgap(10);
        sessionInfoPane.setVgap(10);
        sessionInfoPane.setPadding(new Insets(20));
        sessionInfoPane.addRow(0, new Label("Фильм:"), movieLabel);
        sessionInfoPane.addRow(1, new Label("Зал:"), hallLabel);
        sessionInfoPane.addRow(2, new Label("Начало:"), startTimeLabel);
        sessionInfoPane.addRow(3, new Label("Конец:"), endTimeLabel);
        sessionInfoPane.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-radius: 8px; -fx-padding: 10;");

        Button backButton = new Button("Назад");
        backButton.setOnAction(e -> primaryStage.close());

        VBox root = new VBox(20, inputPanel, sessionInfoPane, backButton);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Label createInfoLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        return label;
    }

    private void searchSessionById() {
        String idText = idField.getText().trim();
        if (idText.isEmpty()) {
            AppUtils.showAlert("Ошибка", "Поле ID не может быть пустым.", Alert.AlertType.WARNING);
            return;
        }

        try {
            int sessionId = Integer.parseInt(idText);
            String message = "SESSION;GET;" + sessionId;
            try (Socket socket = new Socket("localhost", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println(message);

                String response = in.readLine();
                if (response == null) {
                    AppUtils.showAlert("Ошибка", "Ответ сервера отсутствует.", Alert.AlertType.ERROR);
                    return;
                }
                if (response.startsWith("SESSION_FOUND;")) {
                    String[] sessionData = response.split(";");
                    movieLabel.setText(sessionData[2]);
                    hallLabel.setText(sessionData[3]);

                    startTimeLabel.setText(formatDateTime(sessionData[4]));
                    endTimeLabel.setText(formatDateTime(sessionData[5]));
                } else if (response.equals("SESSION_NOT_FOUND")) {
                    AppUtils.showAlert("Ошибка", "Сеанс с указанным ID не найден.", Alert.AlertType.WARNING);
                } else if (response.startsWith("ERROR;")) {
                    AppUtils.showAlert("Ошибка", response.split(";", 2)[1], Alert.AlertType.ERROR);
                }
            }
        } catch (NumberFormatException e) {
            AppUtils.showAlert("Ошибка", "ID должен быть числом.", Alert.AlertType.WARNING);
        } catch (IOException e) {
            AppUtils.showAlert("Ошибка", "Не удалось подключиться к серверу.", Alert.AlertType.ERROR);
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

    public static void main(String[] args) {
        launch(args);
    }
}