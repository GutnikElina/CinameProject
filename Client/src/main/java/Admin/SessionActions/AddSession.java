package Admin.SessionActions;

import Utils.AppUtils;
import Utils.UIUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.converter.LocalDateStringConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AddSession extends Application {

    private DatePicker startDatePicker, endDatePicker;
    private TextField startTimeField, endTimeField;
    private ComboBox<String> movieComboBox, hallComboBox;
    private Map<String, Integer> movieIdMap = new HashMap<>();
    private Map<String, Integer> hallIdMap = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Добавить сеанс");

        startDatePicker = new DatePicker();
        endDatePicker = new DatePicker();

        startTimeField = new TextField();
        startTimeField.setPromptText("HH:mm");

        endTimeField = new TextField();
        endTimeField.setPromptText("HH:mm");

        Label startLabel = new Label("Начало сеанса:");
        Label endLabel = new Label("Конец сеанса:");
        Label movieLabel = new Label("Выберите фильм:");
        Label hallLabel = new Label("Выберите зал:");

        movieComboBox = new ComboBox<>();
        hallComboBox = new ComboBox<>();

        loadMovieAndHallData();

        Button addButton = new Button("Добавить сеанс");
        addButton.setOnAction(e -> addSessionAction(primaryStage));

        VBox vbox = new VBox(15, startLabel, startDatePicker, startTimeField,
                endLabel, endDatePicker, endTimeField,
                movieLabel, movieComboBox,
                hallLabel, hallComboBox,
                addButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9;");

        Scene scene = new Scene(vbox, 500, 400);
        scene.getStylesheets().add("/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadMovieAndHallData() {
        String movieCommand = "MOVIE;GET_ALL";
        String movieResponse = AppUtils.sendToServerAndGetFullResponse(movieCommand);
        if (movieResponse.startsWith("MOVIE_FOUND")) {
            String[] movieLines = movieResponse.split("\n");
            for (String line : movieLines) {
                if (line.startsWith("MOVIE_FOUND")) {
                    String[] movieParts = line.split(";");
                    String movieTitle = movieParts[2];
                    Integer movieId = Integer.parseInt(movieParts[1]);
                    movieComboBox.getItems().add(movieTitle);
                    movieIdMap.put(movieTitle, movieId);
                }
            }
        } else {
            UIUtils.showAlert("Ошибка", "Не удалось загрузить фильмы: " + movieResponse, Alert.AlertType.ERROR);
        }

        String hallCommand = "HALL;GET_ALL";
        String hallResponse = AppUtils.sendToServerAndGetFullResponse(hallCommand);
        if (hallResponse.startsWith("HALL_FOUND")) {
            String[] hallLines = hallResponse.split("\n");
            for (String line : hallLines) {
                if (line.startsWith("HALL_FOUND")) {
                    String[] hallParts = line.split(";");
                    String hallName = hallParts[2];
                    Integer hallId = Integer.parseInt(hallParts[1]);
                    hallComboBox.getItems().add(hallName);
                    hallIdMap.put(hallName, hallId);
                }
            }
        } else {
            UIUtils.showAlert("Ошибка", "Не удалось загрузить залы: " + hallResponse, Alert.AlertType.ERROR);
        }
    }

    private void addSessionAction(Stage stage) {
        try {
            String startDateStr = startDatePicker.getValue().toString();
            String endDateStr = endDatePicker.getValue().toString();
            String startTimeStr = startTimeField.getText();
            String endTimeStr = endTimeField.getText();
            String movieTitle = movieComboBox.getValue();
            String hallName = hallComboBox.getValue();
            Integer movieId = movieIdMap.get(movieTitle);
            Integer hallId = hallIdMap.get(hallName);

            if (startTimeStr == null || endTimeStr == null || movieTitle == null || hallName == null) {
                throw new IllegalArgumentException("Все поля должны быть заполнены!");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String startTimeFull = startDateStr + " " + startTimeStr;
            String endTimeFull = endDateStr + " " + endTimeStr;

            LocalDateTime startTime = LocalDateTime.parse(startTimeFull, formatter);
            LocalDateTime endTime = LocalDateTime.parse(endTimeFull, formatter);

            if (startTime.isAfter(endTime)) {
                throw new IllegalArgumentException("Время окончания сеанса не может быть раньше времени начала.");
            }

            String addSessionCommand = String.format("SESSION;ADD;%s;%s;%d;%d",
                    startTime.format(formatter), endTime.format(formatter), movieId, hallId);

            String response = AppUtils.sendToServer(addSessionCommand);

            if (response.startsWith("SUCCESS")) {
                UIUtils.showAlert("Успех", "Сеанс добавлен", Alert.AlertType.INFORMATION);
                stage.close();
            } else {
                UIUtils.showAlert("Ошибка", "Не удалось добавить сеанс: " + response, Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось добавить сеанс: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
