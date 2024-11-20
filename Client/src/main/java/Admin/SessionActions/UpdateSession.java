package Admin.SessionActions;

import Models.Session;
import Utils.AppUtils;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class UpdateSession{
    @FXML
    private TextField priceField;
    @FXML
    private TextField idField, startTimeField, endTimeField;

    @FXML
    private ComboBox<String> movieComboBox, hallComboBox;
    @FXML
    private DatePicker startDatePicker, endDatePicker;


    private final Map<String, Integer> movieIdMap = new HashMap<>();
    private final Map<String, Integer> hallIdMap = new HashMap<>();

    @FXML
    private void initialize() {
        loadMovieAndHallData();
    }

    private void loadMovieAndHallData() {
        String movieResponse = AppUtils.sendToServerAndGetFullResponse("MOVIE;GET_ALL");
        if (movieResponse.startsWith("MOVIE_FOUND")) {
            for (String line : movieResponse.split("\n")) {
                if (line.startsWith("MOVIE_FOUND")) {
                    String[] parts = line.split(";");
                    String movieTitle = parts[2];
                    int movieId = Integer.parseInt(parts[1]);
                    movieComboBox.getItems().add(movieTitle);
                    movieIdMap.put(movieTitle, movieId);
                }
            }
        } else {
            UIUtils.showAlert("Ошибка", "Не удалось загрузить фильмы.", Alert.AlertType.ERROR);
        }

        String hallResponse = AppUtils.sendToServerAndGetFullResponse("HALL;GET_ALL");
        if (hallResponse.startsWith("HALL_FOUND")) {
            for (String line : hallResponse.split("\n")) {
                if (line.startsWith("HALL_FOUND")) {
                    String[] parts = line.split(";");
                    String hallName = parts[2];
                    int hallId = Integer.parseInt(parts[1]);
                    hallComboBox.getItems().add(hallName);
                    hallIdMap.put(hallName, hallId);
                }
            }
        } else {
            UIUtils.showAlert("Ошибка", "Не удалось загрузить залы.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void updateSessionAction() {
        if (!FieldValidator.validateNumericField(idField, "Введите корректный ID сеанса.")) {
            return;
        }

        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null ||
                startTimeField.getText().trim().isEmpty() || endTimeField.getText().trim().isEmpty()) {
            UIUtils.showAlert("Ошибка", "Все поля времени и даты должны быть заполнены.", Alert.AlertType.ERROR);
            return;
        }

        if (movieComboBox.getValue() == null || hallComboBox.getValue() == null) {
            UIUtils.showAlert("Ошибка", "Выберите фильм и зал.", Alert.AlertType.ERROR);
            return;
        }

        try {
            String startDate = startDatePicker.getValue().toString();
            String endDate = endDatePicker.getValue().toString();
            String startTime = startTimeField.getText().trim();
            String endTime = endTimeField.getText().trim();

            if (!startTime.matches("\\d{2}:\\d{2}") || !endTime.matches("\\d{2}:\\d{2}")) {
                UIUtils.showAlert("Ошибка", "Время должно быть в формате HH:mm", Alert.AlertType.ERROR);
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime startDateTime = LocalDateTime.parse(startDate + " " + startTime, formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endDate + " " + endTime, formatter);

            if (endDateTime.isBefore(startDateTime)) {
                UIUtils.showAlert("Ошибка", "Время окончания не может быть раньше времени начала.", Alert.AlertType.ERROR);
                return;
            }

            priceField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    priceField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
            String priceText = priceField.getText();
            if (priceText == null || priceText.isEmpty()) {
                throw new IllegalArgumentException("Цена должна быть указана!");
            }

            BigDecimal price = new BigDecimal(priceText);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Цена должна быть больше нуля!");
            }

            String command = String.format("SESSION;UPDATE;%d;%d;%d;%s;%s;%s",
                    Integer.parseInt(idField.getText()),
                    movieIdMap.get(movieComboBox.getValue()),
                    hallIdMap.get(hallComboBox.getValue()),
                    startDateTime.format(formatter),
                    endDateTime.format(formatter),
                    price.toPlainString());

            String response = AppUtils.sendToServer(command);
            if (response.contains("ERROR")) {
                UIUtils.showAlert("Ошибка", "Ошибка при обновлении сеанса: " + response, Alert.AlertType.ERROR);
            } else {
                UIUtils.showAlert("Успех", "Сеанс успешно обновлен.", Alert.AlertType.INFORMATION);
                handleBackButton();
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Ошибка при обновлении сеанса: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBackButton() {
        ((Stage) idField.getScene().getWindow()).close();
    }
}