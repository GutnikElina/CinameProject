package Admin.SessionActions;

import Admin.GeneralActions.SessionActionBase;
import Models.RequestDTO;
import Models.ResponseDTO;
import Models.Session;
import Utils.AppUtils;
import Utils.FieldValidator;
import Utils.GsonFactory;
import Utils.UIUtils;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateSession extends SessionActionBase {
    @FXML public Button updateButton;
    @FXML private TextField priceField;
    @FXML private TextField idField, startTimeField, endTimeField;
    @FXML private ComboBox<String> movieComboBox, hallComboBox;
    @FXML private DatePicker startDatePicker;
    private final Map<String, Integer> movieIdMap = new HashMap<>();
    private final Map<String, Integer> hallIdMap = new HashMap<>();
    private final Gson gson = GsonFactory.create();

    @FXML private void initialize() { loadMovieAndHallData(); }

    private void loadMovieAndHallData() {
        try {
            String movieCommand = "{\"command\":\"MOVIE;GET_ALL\"}";
            String movieResponse = AppUtils.sendJsonCommandToServer(movieCommand);
            System.out.println(movieResponse);

            ResponseDTO responseDTO = gson.fromJson(movieResponse, ResponseDTO.class);

            if ("MOVIES_FOUND".equals(responseDTO.getStatus())) {
                List<Map<String, Object>> movies = (List<Map<String, Object>>) responseDTO.getData().get("movies");
                for (Map<String, Object> movieData : movies) {
                    String movieTitle = (String) movieData.get("title");
                    Integer movieId = ((Double) movieData.get("id")).intValue();

                    movieComboBox.getItems().add(movieTitle);
                    movieIdMap.put(movieTitle, movieId);
                }
            } else {
                UIUtils.showAlert("Ошибка", "Не удалось загрузить фильмы: " + responseDTO.getMessage(), Alert.AlertType.ERROR);
            }

            String hallCommand = "{\"command\":\"HALL;GET_ALL\"}";
            String hallResponse = AppUtils.sendJsonCommandToServer(hallCommand);
            System.out.println(hallResponse);

            responseDTO = gson.fromJson(hallResponse, ResponseDTO.class);
            if ("HALLS_FOUND".equals(responseDTO.getStatus())) {
                List<Map<String, Object>> halls = (List<Map<String, Object>>) responseDTO.getData().get("halls");
                for (Map<String, Object> hallData : halls) {
                    String hallName = (String) hallData.get("name");
                    Integer hallId = ((Double) hallData.get("id")).intValue();

                    hallComboBox.getItems().add(hallName);
                    hallIdMap.put(hallName, hallId);
                }
            } else {
                UIUtils.showAlert("Ошибка", "Не удалось загрузить залы: " + hallResponse, Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось загрузить данные: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void updateSessionAction() {
        boolean valid = true;

        valid &= FieldValidator.validateNumericField(idField, "Введите корректный ID сеанса.");
        valid &= FieldValidator.validateDatePicker(startDatePicker, "Дата начала не выбрана.");
        valid &= FieldValidator.validateTextField(startTimeField, "Время начала не указано.", 5);
        valid &= FieldValidator.validateTextField(endTimeField, "Время окончания не указано.", 5);
        valid &= FieldValidator.validateComboBox(movieComboBox, "Фильм не выбран.");
        valid &= FieldValidator.validateComboBox(hallComboBox, "Зал не выбран.");
        valid &= validatePriceField(priceField);

        if (!valid) return;

        try {
            LocalDate startDate = startDatePicker.getValue();
            String startTime = startTimeField.getText().trim();
            String endTime = endTimeField.getText().trim();

            if (!startTime.matches("\\d{2}:\\d{2}") || !endTime.matches("\\d{2}:\\d{2}")) {
                UIUtils.showAlert("Ошибка", "Время должно быть в формате HH:mm", Alert.AlertType.ERROR);
                return;
            }

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime startLocalTime = LocalTime.parse(startTime, timeFormatter);
            LocalTime endLocalTime = LocalTime.parse(endTime, timeFormatter);

            LocalDateTime startDateTime = LocalDateTime.of(startDate, startLocalTime);
            LocalDateTime endDateTime = LocalDateTime.of(startDate, endLocalTime);

            if (endDateTime.isBefore(startDateTime)) {
                UIUtils.showAlert("Ошибка", "Время окончания не может быть раньше времени начала.", Alert.AlertType.ERROR);
                startTimeField.setStyle("-fx-border-color: #ff4444; -fx-border-width: 2;");
                endTimeField.setStyle("-fx-border-color: #ff4444; -fx-border-width: 2;");
                return;
            }

            String movieTitle = movieComboBox.getValue();
            String hallName = hallComboBox.getValue();
            Integer movieId = movieIdMap.get(movieTitle);
            Integer hallId = hallIdMap.get(hallName);

            BigDecimal price = new BigDecimal(priceField.getText());
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                UIUtils.showAlert("Ошибка", "Цена должна быть больше нуля!", Alert.AlertType.ERROR);
                return;
            }

            Session session = new Session();
            session.setId(Integer.parseInt(idField.getText()));
            session.setStartTime(startDateTime);
            session.setEndTime(endDateTime);
            session.setMovieId(movieId);
            session.setHallId(hallId);
            session.setPrice(new BigDecimal(priceField.getText()));

            Stage stage = (Stage) updateButton.getScene().getWindow();
            sendSessionUpdateCommand("SESSION;UPDATE", session, stage);
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось обновить сеанс: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validatePriceField(TextField priceField) {
        try {
            String priceText = priceField.getText().trim();
            if (priceText.isEmpty()) {
                UIUtils.showAlert("Ошибка", "Цена должна быть указана!", Alert.AlertType.ERROR);
                return false;
            }
            BigDecimal price = new BigDecimal(priceText);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                UIUtils.showAlert("Ошибка", "Цена должна быть больше нуля!", Alert.AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            UIUtils.showAlert("Ошибка", "Цена должна быть числом!", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    @FXML
    private void handleBackButton() {
        ((Stage) idField.getScene().getWindow()).close();
    }
}