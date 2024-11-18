package Admin.SessionActions;

import Models.Session;
import Utils.AppUtils;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class UpdateSession{

    @FXML
    private TextField idField, startTimeField, endTimeField;

    @FXML
    private ComboBox<String> movieComboBox, hallComboBox;

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
        if (!FieldValidator.validateNumericField(idField, "Введите корректный ID сеанса.") ||
                !FieldValidator.validateTextField(startTimeField, "Введите корректное время начала (yyyy-MM-dd HH:mm).", 1) ||
                !FieldValidator.validateTextField(endTimeField, "Введите корректное время окончания (yyyy-MM-dd HH:mm).", 1)) {
            return;
        }

        if (movieComboBox.getValue() == null || hallComboBox.getValue() == null) {
            UIUtils.showAlert("Ошибка", "Выберите фильм и зал.", Alert.AlertType.ERROR);
            return;
        }

        Session session = new Session();
        session.setId(Integer.parseInt(idField.getText()));
        session.setMovieId(movieIdMap.get(movieComboBox.getValue()));
        session.setHallId(hallIdMap.get(hallComboBox.getValue()));
        session.setStartTime(LocalDateTime.parse(startTimeField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        session.setEndTime(LocalDateTime.parse(endTimeField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        String command = String.format("SESSION;UPDATE;%d;%d;%d;%s;%s",
                session.getId(),
                session.getMovieId(),
                session.getHallId(),
                session.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                session.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        String response = AppUtils.sendToServer(command);
        if (response.contains("ERROR")) {
            UIUtils.showAlert("Ошибка", "Ошибка при обновлении сеанса: " + response, Alert.AlertType.ERROR);
        } else {
            UIUtils.showAlert("Успех", "Сеанс успешно обновлен.", Alert.AlertType.INFORMATION);
            handleBackButton();
        }
    }

    @FXML
    private void handleBackButton() {
        ((Stage) idField.getScene().getWindow()).close();
    }
}
