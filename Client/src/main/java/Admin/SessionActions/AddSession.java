package Admin.SessionActions;

import Utils.AppUtils;
import Utils.UIUtils;
import Utils.FieldValidator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AddSession {

    @FXML
    private DatePicker startDatePicker, endDatePicker;
    @FXML
    private TextField startTimeField, endTimeField;
    @FXML
    private ComboBox<String> movieComboBox, hallComboBox;
    @FXML
    private TextField priceField;
    @FXML
    private Button addButton, backButton;
    private Map<String, Integer> movieIdMap = new HashMap<>();
    private Map<String, Integer> hallIdMap = new HashMap<>();

    @FXML
    public void initialize() {
        loadMovieAndHallData();
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

    @FXML
    private void addSessionAction() {
        try {
            String startDate = startDatePicker.getValue() != null ? startDatePicker.getValue().toString() : null;
            String endDate = endDatePicker.getValue() != null ? endDatePicker.getValue().toString() : null;
            String startTime = startTimeField.getText();
            String endTime = endTimeField.getText();
            String movieTitle = movieComboBox.getValue();
            String hallName = hallComboBox.getValue();

            if (startDate == null || endDate == null || startTime == null || endTime == null || movieTitle == null || hallName == null) {
                throw new IllegalArgumentException("Все поля должны быть заполнены!");
            }

            Integer movieId = movieIdMap.get(movieTitle);
            Integer hallId = hallIdMap.get(hallName);

            if (movieId == null || hallId == null) {
                throw new IllegalArgumentException("Не удалось найти выбранный фильм или зал.");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime startDateTime = LocalDateTime.parse(startDate + " " + startTime, formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endDate + " " + endTime, formatter);

            if (endDateTime.isBefore(startDateTime)) {
                throw new IllegalArgumentException("Время окончания не может быть раньше времени начала.");
            }

            String priceText = priceField.getText();
            if (priceText == null || priceText.isEmpty()) {
                throw new IllegalArgumentException("Цена должна быть указана!");
            }
            BigDecimal price = new BigDecimal(priceText);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Цена должна быть больше нуля!");
            }

            String addSessionCommand = String.format("SESSION;ADD;%s;%s;%d;%d;%s",
                    startDateTime.format(formatter),
                    endDateTime.format(formatter),
                    movieId,
                    hallId,
                    price.toPlainString());

            String response = AppUtils.sendToServer(addSessionCommand);
            Stage stage = (Stage) addButton.getScene().getWindow();

            if (response.startsWith("SUCCESS")) {
                UIUtils.showAlert("Успех", "Сеанс добавлен успешно!", Alert.AlertType.INFORMATION);
                stage.close();
            } else {
                throw new RuntimeException("Сервер вернул ошибку: " + response);
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось добавить сеанс: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}